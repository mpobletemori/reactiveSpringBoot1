package com.example.springbootwebflux.models.services;

import com.example.springbootwebflux.models.documents.ProductoDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

    Flux<ProductoDocument> findAll();

    Flux<ProductoDocument> findAllConNombreUpperCase();

    Flux<ProductoDocument> findAllConNombreUpperCaseRepeat();

    Mono<ProductoDocument> findById(String id);

    Mono<ProductoDocument> save(ProductoDocument producto);

    Mono<Void> delete(ProductoDocument producto);

}
