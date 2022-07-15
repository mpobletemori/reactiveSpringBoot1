package com.example.springbootwebfluxapirest.app.controller;

import com.example.springbootwebfluxapirest.app.models.documents.ProductoDocument;
import com.example.springbootwebfluxapirest.app.models.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @Value("${config.uploads.path}")
    private String path;

    @PostMapping("/v2")
    public Mono<ResponseEntity<ProductoDocument>> crearConFoto(ProductoDocument producto, @RequestPart FilePart file){
        if(producto.getCreateAt() == null){
            producto.setCreateAt(new Date());
        }

        producto.setFoto(UUID.randomUUID().toString() +"-"+file.filename()
                .replace(" ", "")
                .replace(":", "")
                .replace("\\", ""));

        return file.transferTo(
                new File(path+producto.getFoto())
        ).then(productoService.save(producto)).map(p->
                ResponseEntity
                        .created(URI.create("/api/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8).body(p)
                );
    }

    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<ProductoDocument>> upload(@PathVariable String id, @RequestPart FilePart file){
            return productoService.findById(id).flatMap(p->{
                p.setFoto(UUID.randomUUID().toString() +"-"+file.filename()
                        .replace(" ", "")
                        .replace(":", "")
                        .replace("\\", ""));

                return file.transferTo(
                        new File(path+p.getFoto())
                        ).then(productoService.save(p));
            }).map(p->ResponseEntity.ok(p))
              .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<ProductoDocument>>>  listar(){
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(productoService.findAll()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductoDocument>> ver(@PathVariable String id){
        return this.productoService.findById(id).map(p->
                ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(p)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String,Object>>> crear(@Valid @RequestBody Mono<ProductoDocument> producto){

        return producto.flatMap(p->{
            if(p.getCreateAt() == null){
                p.setCreateAt(new Date());
            }
            return productoService.save(p).map(prod->
                    ResponseEntity
                            .created(URI.create("/api/productos/".concat(prod.getId())))
                            .contentType(MediaType.APPLICATION_JSON_UTF8).body(
                                    Map.of( "producto",prod,
                                            "mensaje","Producto creado con exito",
                                            "timestamp",new Date()
                                           )
                            )
            );
        }).onErrorResume(t->
                Mono.just(t).cast(WebExchangeBindException.class)
                        .flatMap(e->Mono.just(e.getFieldErrors()))
                        .flatMapMany(Flux::fromIterable)
                        .map(fieldError -> "El campo "+fieldError.getField()+" "+fieldError.getDefaultMessage())
                        .collectList()
                        .flatMap(list->Mono.just(ResponseEntity.badRequest().body(
                                Map.of( "errros",list,
                                "status",HttpStatus.BAD_REQUEST.toString(),
                                "timestamp",new Date())
                        )))
                );


    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductoDocument>> editar(@RequestBody ProductoDocument producto,@PathVariable String id){
        return productoService.findById(id).flatMap(p->{
            p.setNombre(producto.getNombre());
            p.setPrecio(producto.getPrecio());
            p.setCategoria(producto.getCategoria());
            return productoService.save(p);
        }).map(p->
                ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8).body(p)
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminar(@PathVariable String id){
        return this.productoService.findById(id).flatMap(p->
                productoService.delete(p).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                ).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }


}
