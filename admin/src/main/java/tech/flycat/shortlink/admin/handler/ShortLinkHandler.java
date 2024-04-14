package tech.flycat.shortlink.admin.handler;

import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tech.flycat.shortlink.admin.dto.CreateShortLinkDTO;
import tech.flycat.shortlink.admin.repository.RedirectLinkRelationRepository;
import tech.flycat.shortlink.admin.utils.NumberScaleUtil;
import tech.flycat.shortlink.bean.Result;
import tech.flycat.shortlink.entity.RedirectLinkInfo;
import tech.flycat.shortlink.exception.BaseException;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/6
 */
@Service
public class ShortLinkHandler {
    @Value("${short-link.base-url:}")
    private String baseUrl;

    @Autowired
    private RedirectLinkRelationRepository redirectLinkRelationRepository;

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(CreateShortLinkDTO.class)
                .switchIfEmpty(Mono.error(new BaseException("参数不能为空")))
                .flatMap(this::transferToLinkRelation)
                .flatMap(redirectLinkRelationRepository::save)
                .flatMap(d -> ServerResponse.ok().bodyValue(Result.ok(baseUrl + "/" + d.getCode())));
    }

    private Mono<RedirectLinkInfo> transferToLinkRelation(CreateShortLinkDTO createShortLinkDTO) {
        String url = Optional.ofNullable(createShortLinkDTO.getUrl())
                .map(String::trim)
                .orElseThrow(() -> new BaseException("url不能为空"));

        checkUrl(url);

        String urlHash = hash(url);

        return findUnusedCode(urlHash)
                .map(code -> RedirectLinkInfo.builder()
                        .code(code)
                        .name(createShortLinkDTO.getName())
                        .url(url)
                        .urlMd5(urlHash)
                        .valid(true)
                        .visitTimes(0)
                        .operatorId(0L)
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now())
                        .build());
    }

    private Mono<String> findUnusedCode(String code) {
        return Mono.just(code)
                .expand(this::checkIfCodeExists)
                .takeUntil(""::equals)
                .collectList()
                .flatMap(list -> Mono.just(list.get(list.size() - 2)));
    }

    private Mono<String> checkIfCodeExists(String code) {
        return redirectLinkRelationRepository.findByCode(code)
                .map(r -> hash(r.getCode() + "h"))
                .defaultIfEmpty("");
    }

    private static String hash(String url) {
        long hashInt = Hashing.murmur3_32_fixed().hashUnencodedChars(url).padToLong();
        return NumberScaleUtil.convertTo64(hashInt);
    }

    private void checkUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new BaseException("url有误");
        }
    }
}
