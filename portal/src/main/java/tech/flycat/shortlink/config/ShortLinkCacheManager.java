package tech.flycat.shortlink.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/6
 */
@Slf4j
@Component
public class ShortLinkCacheManager {

    public static final String NO_CACHE = "NO_CACHE";

    private final Cache<String, String> cache;

    private final RedisTemplate redisTemplate;

    private final PropertiesContext propertiesContext;

    public ShortLinkCacheManager(@Autowired PropertiesContext propertiesContext, @Autowired(required = false) RedisTemplate redisTemplate) {
        this.propertiesContext = propertiesContext;
        PropertiesContext.CacheConfig cacheConfig = Optional.ofNullable(propertiesContext.getCaffeineConfig())
                .orElseGet(PropertiesContext.CacheConfig::new);

        cache = Caffeine.newBuilder()
                //初始数量
                .initialCapacity(cacheConfig.getInitialCapacity())
                //最大条数
                .maximumSize(cacheConfig.getMaximumSize())
                //expireAfterWrite和expireAfterAccess同时存在时，以expireAfterWrite为准
                //最后一次写操作后经过指定时间过期
                .expireAfterWrite(cacheConfig.getExpireAfterWrite(), TimeUnit.SECONDS)
                //最后一次读或写操作后经过指定时间过期
                .expireAfterAccess(cacheConfig.getExpireAfterAccess(), TimeUnit.SECONDS)
                //监听缓存被移除
                .removalListener((key, val, removalCause) -> { })
                //记录命中
                .recordStats()
                .build();

        if (propertiesContext.getEnableRedisCache()) {
            this.redisTemplate = redisTemplate;
        } else {
            this.redisTemplate = null;
        }
    }

    /**
     * 尝试从缓存获取
     * @param code 短链code
     * @param getIfNotPresent 如果缓存中不存在则执行改回调方法从数据库获取
     * @return 如果获取成功则返回原始url，否则抛出<code>tech.flycat.shortlink.exception.NotFoundException</code>异常
     */
    public Mono<String> tryGetFromCache(String code) {
        return Mono.just(code)
                .mapNotNull(c -> cache.getIfPresent(code))
                .doOnNext(url -> {
                    if (log.isDebugEnabled()) {
                        log.debug("命中caffeine缓存, code: {}, url: {}", code, url);
                    }
                })
                .switchIfEmpty(Mono.fromSupplier(() -> {
                    if (redisTemplate == null) {
                        return null;
                    }

                    return (String)redisTemplate.opsForValue().get(code);
                }).doOnNext(url -> {
                    if (log.isDebugEnabled()) {
                        log.debug("命中redis缓存, code: {}, url: {}", code, url);
                    }
                    if (StringUtils.hasText(url)) {
                        cache.put(code, url);
                    }
                }));
    }


    public void cacheValue(String code, String url) {
        cache.put(code, url);
        if (redisTemplate != null) {
            Long expire = NO_CACHE.equals(url)
                    ? propertiesContext.getEmptyUrlRedisExpireTime() : propertiesContext.getRedisExpireTime();
            redisTemplate.opsForValue().set(code, url, expire, TimeUnit.SECONDS);
        }
    }
}
