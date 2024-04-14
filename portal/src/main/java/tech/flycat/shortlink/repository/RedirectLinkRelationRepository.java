package tech.flycat.shortlink.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.flycat.shortlink.entity.RedirectLinkInfo;

import java.util.Collection;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/5
 */
@Repository
public interface RedirectLinkRelationRepository extends R2dbcRepository<RedirectLinkInfo, Long> {
    Mono<RedirectLinkInfo> findByCodeAndValid(String code, Boolean valid);

    Flux<RedirectLinkInfo> findByCodeIn(Collection<String> codes);
}
