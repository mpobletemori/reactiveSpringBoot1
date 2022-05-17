package com.example.springbootwebflux.controller;

import com.example.springbootwebflux.models.documents.ProductoDocument;
import com.example.springbootwebflux.models.repository.ProductoRepository;
import org.hibernate.validator.constraints.CodePointLength;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

@Controller
public class ProductoController {
    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping({"/listar","/"})
    public String listar(Model model){
        Flux<ProductoDocument> productos = productoRepository.findAll();
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }
}
