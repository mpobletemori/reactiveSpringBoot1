package com.example.springbootwebflux.models.services.impl;

import com.example.springbootwebflux.models.documents.ProductoDocument;
import com.example.springbootwebflux.models.repository.ProductoRepository;
import com.example.springbootwebflux.models.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Flux<ProductoDocument> findAll() {
        return this.productoRepository.findAll();
    }

    @Override
    public Flux<ProductoDocument> findAllConNombreUpperCase() {
        return productoRepository.findAll().map(producto->{
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        });
    }

    @Override
    public Flux<ProductoDocument> findAllConNombreUpperCaseRepeat() {
        return this.findAllConNombreUpperCase().repeat(5000);
    }

    @Override
    public Mono<ProductoDocument> findById(String id) {
        return this.productoRepository.findById(id);
    }

    @Override
    public Mono<ProductoDocument> save(ProductoDocument producto) {
        return this.productoRepository.save(producto);
    }

    @Override
    public Mono<Void> delete(ProductoDocument producto) {
        return this.productoRepository.delete(producto);
    }
}
