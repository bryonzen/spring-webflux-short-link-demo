package tech.flycat.shortlink.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/5
 */
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    private final ObjectMapper objectMapper;

    private final ApplicationContext applicationContext;

    public WebFluxConfig(ObjectMapper objectMapper,
                         ApplicationContext applicationContext) {
        this.objectMapper = objectMapper;
        this.applicationContext = applicationContext;
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        // we need to customize the Jackson2Json[Decoder][Encoder] here to serialize and
        // deserialize special types, e.g.: Instant, LocalDateTime. So we use ObjectMapper
        // created by outside.
        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
    }
}
