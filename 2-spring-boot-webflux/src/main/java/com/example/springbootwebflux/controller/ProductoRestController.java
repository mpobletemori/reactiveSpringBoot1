package com.example.springbootwebflux.controller;

import com.example.springbootwebflux.models.documents.ProductoDocument;
import com.example.springbootwebflux.models.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductoRestController.class);
    @Autowired
    private ProductoRepository productoRepository;

    private String opcion="1";

    @GetMapping
    public Flux<ProductoDocument> index(){
        Flux<ProductoDocument> productos = productoRepository.findAll().map(producto->{
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        }).doOnNext(producto->LOGGER.info(producto.getNombre()));

        return productos;
    }

    @GetMapping("/{id}")
    public Mono<ProductoDocument> show(@PathVariable String id){
        Flux<ProductoDocument> productosDocumentFlux = productoRepository.findAll();
        Mono<ProductoDocument> productoDocumentMono = "1".equals(this.opcion)?
                productoRepository.findById(id)
                : productosDocumentFlux
                    .filter(p->id.equals(p.getId()))
                    .next()
                    .doOnNext(p->LOGGER.info(p.getNombre()));

        return productoDocumentMono;
    }
}
