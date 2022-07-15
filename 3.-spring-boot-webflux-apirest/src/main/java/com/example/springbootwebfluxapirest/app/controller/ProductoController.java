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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @Value("${config.uploads.path}")
    private String path;

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
    public Mono<ResponseEntity<ProductoDocument>> crear(@RequestBody ProductoDocument producto){
        if(producto.getCreateAt() == null){
            producto.setCreateAt(new Date());
        }

        return productoService.save(producto).map(p->
                    ResponseEntity
                            .created(URI.create("/api/productos/".concat(p.getId())))
                            .contentType(MediaType.APPLICATION_JSON_UTF8).body(p));
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
