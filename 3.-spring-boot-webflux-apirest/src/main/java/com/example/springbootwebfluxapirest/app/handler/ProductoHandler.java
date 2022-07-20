package com.example.springbootwebfluxapirest.app.handler;

import com.example.springbootwebfluxapirest.app.models.documents.ProductoDocument;
import com.example.springbootwebfluxapirest.app.models.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ProductoHandler {

    @Autowired
    private ProductoService productoService;

    public Mono<ServerResponse> listar(ServerRequest request){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(this.productoService.findAll(), ProductoDocument.class);
    }


}
