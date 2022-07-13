package com.example.springbootwebfluxapirest.app.models.service.impl;

import com.example.springbootwebfluxapirest.app.models.documents.CategoriaDocument;
import com.example.springbootwebfluxapirest.app.models.documents.ProductoDocument;
import com.example.springbootwebfluxapirest.app.models.repository.CategoriaRepository;
import com.example.springbootwebfluxapirest.app.models.repository.ProductoRepository;
import com.example.springbootwebfluxapirest.app.models.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

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

    @Override
    public Flux<CategoriaDocument> findAllCategoria() {
        return categoriaRepository.findAll();
    }

    @Override
    public Mono<CategoriaDocument> findCategoriaById(String id) {
        return categoriaRepository.findById(id);
    }

    @Override
    public Mono<CategoriaDocument> saveCategoria(CategoriaDocument categoria) {
        return categoriaRepository.save(categoria);
    }
}
