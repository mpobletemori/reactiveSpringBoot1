package com.example.springbootwebfluxapirest.app.models.repository;

import com.example.springbootwebfluxapirest.app.models.documents.CategoriaDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CategoriaRepository extends ReactiveMongoRepository<CategoriaDocument,String> {

        Mono<CategoriaDocument> findByNombre(String nombre);

}
