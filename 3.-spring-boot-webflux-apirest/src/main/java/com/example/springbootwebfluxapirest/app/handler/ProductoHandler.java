package com.example.springbootwebfluxapirest.app.handler;

import com.example.springbootwebfluxapirest.app.models.documents.CategoriaDocument;
import com.example.springbootwebfluxapirest.app.models.documents.ProductoDocument;
import com.example.springbootwebfluxapirest.app.models.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

@Component
public class ProductoHandler {

    @Autowired
    private ProductoService productoService;

    @Value("${config.uploads.path}")
    private String path;

    public Mono<ServerResponse> crearConFoto(ServerRequest request){

        Mono<ProductoDocument> productoDocumentMono = request.multipartData().map(multipart->{
            FormFieldPart nombre = (FormFieldPart) multipart.toSingleValueMap().get("nombre");
            FormFieldPart precio = (FormFieldPart) multipart.toSingleValueMap().get("precio");
            FormFieldPart categoriaId = (FormFieldPart) multipart.toSingleValueMap().get("categoria.id");
            FormFieldPart categoriaNombre = (FormFieldPart) multipart.toSingleValueMap().get("categoria.nombre");

            return new ProductoDocument(nombre.value(),Double.parseDouble(precio.value()),new CategoriaDocument(categoriaId.value(),categoriaNombre.value()));
        });

        return request.multipartData().map(multipart->{
                    //obtener archivo subido
                    return multipart.toSingleValueMap().get("archivo");
                })
                //transformar al tipo FilePart por que devuelve
                .cast(FilePart.class)
                .flatMap(archivo->{
                    //buscar registro de producto a editar
                    return productoDocumentMono.flatMap(p->{
                        //agregar campos a crear de imagen
                        p.setFoto(UUID.randomUUID().toString()+"-"+archivo.filename()
                                .replace(" ", "-")
                                .replace(":", "")
                                .replace("\\", ""));
                        p.setCreateAt(new Date());
                        //guarda archivo en filesystem
                        return archivo.transferTo(new File(path+p.getFoto()))
                                //ejecuta modificar producto en mongodb
                                .then(productoService.save(p));
                    });
                })//Generamos valor de respuesta
                .flatMap(p->ServerResponse.created(URI.create("/api/v2/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(BodyInserters.fromObject(p)));
    }

    public Mono<ServerResponse> upload(ServerRequest request){
        String id = request.pathVariable("id");
        return request.multipartData().map(multipart->{
                    //obtener archivo subido
                    return multipart.toSingleValueMap().get("archivo");
        })
        //transformar al tipo FilePart por que devuelve
        .cast(FilePart.class)
        .flatMap(archivo->{
            //buscar registro de producto a editar
            return productoService.findById(id).flatMap(p->{
                 //agregar campos a editar de imagen
                 p.setFoto(UUID.randomUUID().toString()+"-"+archivo.filename()
                         .replace(" ", "-")
                         .replace(":", "")
                         .replace("\\", ""));
                 //guarda archivo en filesystem
                 return archivo.transferTo(new File(path+p.getFoto()))
                         //ejecuta modificar producto en mongodb
                         .then(productoService.save(p));
            });
        })//Generamos valor de respuesta
           .flatMap(p->ServerResponse.created(URI.create("/api/v2/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(BodyInserters.fromObject(p)))
           //en caso el mono este vacio lanzar notFound
           .switchIfEmpty(ServerResponse.notFound().build());
    }

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
