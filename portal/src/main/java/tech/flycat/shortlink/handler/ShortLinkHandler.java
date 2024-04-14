package tech.flycat.shortlink.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tech.flycat.shortlink.config.ShortLinkCacheManager;
import tech.flycat.shortlink.entity.RedirectLinkInfo;
import tech.flycat.shortlink.exception.NotFoundException;
import tech.flycat.shortlink.repository.RedirectLinkRelationRepository;

import java.net.URI;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/5
 */
@Service
public class ShortLinkHandler {

    @Autowired
    private RedirectLinkRelationRepository redirectLinkRelationRepository;
    @Autowired
    ShortLinkCacheManager shortLinkCacheManager;
    @Autowired
    private VisitRecordHandler visitRecordHandler;

    public Mono<ServerResponse> redirect(ServerRequest serverRequest) {
        String code = serverRequest.pathVariable("code");
        return Mono.just(code)
                .flatMap(c -> shortLinkCacheManager.tryGetFromCache(code))
                .switchIfEmpty(redirectLinkRelationRepository.findByCodeAndValid(code, true)
                        .map(RedirectLinkInfo::getUrl)
                        .defaultIfEmpty(ShortLinkCacheManager.NO_CACHE)
                        .doOnNext(url -> shortLinkCacheManager.cacheValue(code, url)))
                .filter(url -> StringUtils.hasText(url) && !ShortLinkCacheManager.NO_CACHE.equals(url))
                .switchIfEmpty(Mono.error(new NotFoundException("资源不存在")))
                .doOnNext(url -> visitRecordHandler.push(code, serverRequest))
                .flatMap(url -> ServerResponse.temporaryRedirect(URI.create(url)).build());
    }
}
