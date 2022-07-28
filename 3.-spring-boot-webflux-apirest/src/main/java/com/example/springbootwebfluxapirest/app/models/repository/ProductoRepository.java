package com.example.springbootwebfluxapirest.app.models.repository;

import com.example.springbootwebfluxapirest.app.models.documents.ProductoDocument;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProductoRepository extends ReactiveMongoRepository<ProductoDocument,String> {
    Mono<ProductoDocument> findByNombre(String nombre);

    @Query("{ 'nombre': ?0 }")
    Mono<ProductoDocument> obtenerPorNombre(String nombre);
}
