package com.example.springbootwebflux.controller;

import com.example.springbootwebflux.models.documents.ProductoDocument;
import com.example.springbootwebflux.models.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Controller
public class ProductoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
    @Autowired
    private ProductoService productoService;

    @GetMapping({"/listar","/"})
    public String listar(Model model){
        Flux<ProductoDocument> productos = productoService.findAllConNombreUpperCase();

        productos.subscribe(producto->LOGGER.info(producto.getNombre()));
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    @GetMapping("/form")
    public Mono<String> crear(Model model){
        model.addAttribute("producto",new ProductoDocument());
        model.addAttribute("titulo", "Listado de productos");
        return Mono.just("form");
    }

    @PostMapping("/form")
    public Mono<String> guardar(ProductoDocument producto){
            return this.productoService.save(producto).doOnNext(p->{
                LOGGER.info("Producto guardado {} , id: {}",p.getNombre(),p.getId());
            }).thenReturn("redirect:/listar");
    }

    @GetMapping("/listar-datadriver")
    public String listarDataDriver(Model model){
        Flux<ProductoDocument> productos = productoService.findAllConNombreUpperCase()
                .delayElements(Duration.ofSeconds(1));

        productos.subscribe(producto->LOGGER.info(producto.getNombre()));
        model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos,1));
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    @GetMapping("/listar-full")
    public String listarFull(Model model){
        Flux<ProductoDocument> productos = productoService.findAllConNombreUpperCaseRepeat();

        productos.subscribe(producto->LOGGER.info(producto.getNombre()));
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    @GetMapping("/listar-chunked")
    public String listarChunked(Model model){
        Flux<ProductoDocument> productos = productoService.findAllConNombreUpperCaseRepeat();

        productos.subscribe(producto->LOGGER.info(producto.getNombre()));
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return "listar-chunked-form";
    }
}
