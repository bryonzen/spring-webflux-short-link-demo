package tech.flycat.shortlink.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import tech.flycat.shortlink.bean.Result;
import tech.flycat.shortlink.exception.BaseException;
import tech.flycat.shortlink.exception.NotFoundException;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/6
 */
@Slf4j
@Component
@Order(-99)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties resources, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resources.getResources(), applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(
            ServerRequest request) {
        Throwable ex = this.getError(request);

        if (!(ex instanceof BaseException)) {
            log.error("系统错误", ex);
        }

        HttpStatusCode httpStatusCode = HttpStatus.OK;
        if (ex instanceof NotFoundException) {
            httpStatusCode = HttpStatus.NOT_FOUND;
        }

        return ServerResponse.status(httpStatusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Result.error(ex instanceof BaseException ? ex.getMessage() : "系统错误")));
    }
}
