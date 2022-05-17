package com.example.springbootwebflux.controller;

import com.example.springbootwebflux.models.documents.ProductoDocument;
import com.example.springbootwebflux.models.repository.ProductoRepository;
import org.hibernate.validator.constraints.CodePointLength;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Controller
public class ProductoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping({"/listar","/"})
    public String listar(Model model){
        Flux<ProductoDocument> productos = productoRepository.findAll().map(producto->{
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        });

        productos.subscribe(producto->LOGGER.info(producto.getNombre()));
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    @GetMapping("/listar-datadriver")
    public String listarDataDriver(Model model){
        Flux<ProductoDocument> productos = productoRepository.findAll().map(producto->{
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        }).delayElements(Duration.ofSeconds(1));

        productos.subscribe(producto->LOGGER.info(producto.getNombre()));
        model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos,1));
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    @GetMapping("/listar-full")
    public String listarFull(Model model){
        Flux<ProductoDocument> productos = productoRepository.findAll().map(producto->{
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        }).repeat(5000);

        productos.subscribe(producto->LOGGER.info(producto.getNombre()));
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    @GetMapping("/listar-chunked")
    public String listarChunked(Model model){
        Flux<ProductoDocument> productos = productoRepository.findAll().map(producto->{
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        }).repeat(5000);

        productos.subscribe(producto->LOGGER.info(producto.getNombre()));
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return "listar-chunked-form";
    }
}
