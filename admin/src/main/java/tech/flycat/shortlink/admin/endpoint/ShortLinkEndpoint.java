package tech.flycat.shortlink.admin.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import tech.flycat.shortlink.admin.handler.ShortLinkHandler;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/6
 */
@Component
public class ShortLinkEndpoint {

    @Autowired
    private ShortLinkHandler shortLinkHandler;

    @Bean
    public RouterFunction<ServerResponse> endpoint() {
        return RouterFunctions.route()
                .POST("/shortlink/create", shortLinkHandler::create)
                .build();
    }
}
