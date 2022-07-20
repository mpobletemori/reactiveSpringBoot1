package com.example.springbootwebfluxapirest.app;

import com.example.springbootwebfluxapirest.app.handler.ProductoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.server.RouterFunction;

import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ProductoHandler productoHandler){
        return route(GET("/api/v2/productos").or(GET("/api/v3/productos")),
                request->productoHandler.listar(request));
    }

}
