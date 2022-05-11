package com.example.springbootwebflux.models.repository;

import com.example.springbootwebflux.models.documents.ProductoDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductoRepository extends ReactiveMongoRepository<ProductoDocument,String> {

}
