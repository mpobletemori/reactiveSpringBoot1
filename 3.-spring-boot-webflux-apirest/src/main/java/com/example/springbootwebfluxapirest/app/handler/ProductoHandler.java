package com.example.springbootwebfluxapirest.app.handler;

import com.example.springbootwebfluxapirest.app.models.documents.ProductoDocument;
import com.example.springbootwebfluxapirest.app.models.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

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

    public Mono<ServerResponse> ver(ServerRequest request){
        String id = request.pathVariable("id");
        return productoService.findById(id).flatMap(p->
                ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(BodyInserters.fromObject(p)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> crear(ServerRequest request){
        Mono<ProductoDocument> productoMono = request.bodyToMono(ProductoDocument.class);

        return productoMono.flatMap(p->{
           if(p.getCreateAt() == null){
               p.setCreateAt(new Date());
           }
           return this.productoService.save(p);
        }).flatMap(p-> ServerResponse.created(URI.create("/api/v2/productos/".concat(p.getId())))
                                     .contentType(MediaType.APPLICATION_JSON_UTF8)
                                     .body(BodyInserters.fromObject(p))
        );
    }

    public Mono<ServerResponse> editar(ServerRequest request){
        String id = request.pathVariable("id");
        Mono<ProductoDocument> productoMonoDb = productoService.findById(id);
        Mono<ProductoDocument> productoMono = request.bodyToMono(ProductoDocument.class);
        return productoMonoDb.zipWith(productoMono,(prodDb,req)->{
              prodDb.setNombre(req.getNombre());
              prodDb.setPrecio(req.getPrecio());
              prodDb.setCategoria(req.getCategoria());
              return prodDb;
        }).flatMap(p-> ServerResponse.created(URI.create("/api/v2/productos/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(productoService.save(p),ProductoDocument.class)
        ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> eliminar(ServerRequest request){
        String id = request.pathVariable("id");
        Mono<ProductoDocument> productoMonoDb = productoService.findById(id);
        return productoMonoDb.flatMap(p->productoService.delete(p)
                                       .then(ServerResponse.noContent().build())
                 ).switchIfEmpty(ServerResponse.notFound().build());
    }




}
