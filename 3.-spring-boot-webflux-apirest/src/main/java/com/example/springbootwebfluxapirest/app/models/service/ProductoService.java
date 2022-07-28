package com.example.springbootwebfluxapirest.app.models.service;

import com.example.springbootwebfluxapirest.app.models.documents.CategoriaDocument;
import com.example.springbootwebfluxapirest.app.models.documents.ProductoDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

    Flux<ProductoDocument> findAll();

    Flux<ProductoDocument> findAllConNombreUpperCase();

    Flux<ProductoDocument> findAllConNombreUpperCaseRepeat();

    Mono<ProductoDocument> findById(String id);

    Mono<ProductoDocument> save(ProductoDocument producto);

    Mono<Void> delete(ProductoDocument producto);

    Flux<CategoriaDocument> findAllCategoria();

    Mono<CategoriaDocument> findCategoriaById(String id);

    Mono<CategoriaDocument> saveCategoria(CategoriaDocument categoria);

    Mono<ProductoDocument> findByNombre(String nombre);

    Mono<CategoriaDocument> findCategoriaByNombre(String nombre);


}
