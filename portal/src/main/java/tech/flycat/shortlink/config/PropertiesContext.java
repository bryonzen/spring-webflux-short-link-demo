package tech.flycat.shortlink.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/6
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "short-link")
public class PropertiesContext {
    /**
     * caffeine缓存配置
     */
    private CacheConfig caffeineConfig;

    /**
     * 开启redis二级缓存
     */
    private Boolean enableRedisCache = false;

    /**
     * redis过期时间
     */
    private Long redisExpireTime = 86400L;

    /**
     * redis空值过期时间
     */
    private Long emptyUrlRedisExpireTime = 60L;

    @Data
    public static class CacheConfig {
        /**
         * 初始数量
         */
        private Integer initialCapacity = 10;
        /**
         * 最大缓存条数
         */
        private Integer maximumSize = 65536;
        /**
         * 最后一次写操作后经过指定时间过期（时间：s）
         */
        private Integer expireAfterWrite = 86400;
        /**
         * 最后一次读或写操作后经过指定时间过期（时间：s）
         */
        private Integer expireAfterAccess = 86400;
    }
}
