package com.example.springbootwebflux.models.repository;

import com.example.springbootwebflux.models.documents.CategoriaDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoriaRepository extends ReactiveMongoRepository<CategoriaDocument,String> {

}
