package io.axoniq.demo.hotel.inventory.config;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.Collections;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
@Slf4j
@EnableSwagger2WebFlux
public class AxonConfig {

    // Axon

    @Autowired
    public void reactiveCommandGatewayConfiguration(ReactorCommandGateway reactorCommandGateway) {
        reactorCommandGateway.registerDispatchInterceptor(monoMessage -> monoMessage.doOnNext(message -> log.info("Dispatched command message: [{}]", message.getPayloadType().getSimpleName())));
    }

    @Autowired
    public void reactiveCommandGatewayConfiguration(ReactorQueryGateway reactorQueryGateway) {
        reactorQueryGateway.registerDispatchInterceptor(monoMessage -> monoMessage.doOnNext(message -> log.info("Dispatched query message: [{}]", message.getPayloadType().getSimpleName())));
    }

    // CORS

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("*");
        corsConfig.setMaxAge(8000L);
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");

        org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    // Swagger

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.axoniq.demo.hotel.inventory"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "AxonIQ Hotel",
                "Demo Project - Inventory",
                "0.0.1-SNAPSHOT",
                "",
                new Contact("AxonIQ", "https://axoniq.io", "questions@axoniq.io"),
                "",
                "",
                Collections.emptyList());
    }

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(
            @Value("classpath:/static/index.html") Resource html) {
        return route(GET("/"), request
                -> ok().contentType(MediaType.TEXT_HTML).bodyValue(html)
        );
    }

}
