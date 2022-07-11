package com.example.springbootwebflux.controller;

import com.example.springbootwebflux.models.documents.CategoriaDocument;
import com.example.springbootwebflux.models.documents.ProductoDocument;
import com.example.springbootwebflux.models.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;

//@SessionAttributes("producto")
@Controller
public class ProductoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
    @Autowired
    private ProductoService productoService;

    @ModelAttribute("categorias")
    public Flux<CategoriaDocument> categorias(){
        return productoService.findAllCategoria();
    }

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
        model.addAttribute("boton", "Crear");
        return Mono.just("form");
    }

    @GetMapping("/form/{id}")
    public Mono<String> editar(@PathVariable String id, Model model){
        Mono<ProductoDocument>  productoMono = productoService.findById(id).doOnNext(p->{
            LOGGER.info("producto encontrado: {}",p.getNombre());
        }).defaultIfEmpty(new ProductoDocument());
        model.addAttribute("titulo", "Editar producto");
        model.addAttribute("boton", "Editar");
        model.addAttribute("producto",productoMono);

        return Mono.just("form");
    }

    @GetMapping("/form-v2/{id}")
    public Mono<String> editarV2(@PathVariable String id, Model model){
        return productoService.findById(id).doOnNext(p->{
            LOGGER.info("producto encontrado: {}",p.getNombre());
            model.addAttribute("titulo", "Editar producto");
            model.addAttribute("boton", "Editar");
            model.addAttribute("producto",p);
        }).defaultIfEmpty(new ProductoDocument())
                .flatMap(p->{
                    if(p.getId() == null){
                        return Mono.error(new InterruptedException("No existe producto"));
                    }
                    return Mono.just(p);
                }).then(Mono.just("form"))
                .onErrorResume(ex->Mono.just("redirect:/listar?error=No+existe+el+producto"));
    }

    @PostMapping("/form")
    public Mono<String> guardar(@Valid ProductoDocument producto , BindingResult result, Model model,SessionStatus sessionStatus){
            if(result.hasErrors()){
                model.addAttribute("titulo", "Errores en formulario producto");
                model.addAttribute("boton", "Guardar");
                return Mono.just("form");
            }else {
                sessionStatus.setComplete();
                if(producto.getCreateAt() ==null){
                    producto.setCreateAt(new Date());
                }

                Mono<CategoriaDocument> categoria = productoService.findCategoriaById(producto.getCategoria().getId());

                return categoria.flatMap(c->{
                    producto.setCategoria(c);
                    return this.productoService.save(producto);
                }).doOnNext(p -> {
                    LOGGER.info("Categoria asignada {} , id Categoria: {}", p.getCategoria().getNombre(), p.getCategoria().getId());
                    LOGGER.info("Producto guardado {} , id: {}", p.getNombre(), p.getId());
                }).thenReturn("redirect:/listar?success=producto+guardado+con+exito");

            }
    }

    @GetMapping("/eliminar/{id}")
    public Mono<String> eliminar(@PathVariable String id){
        return productoService.findById(id)
                .defaultIfEmpty(new ProductoDocument())
                .flatMap(p->{
                    if(Objects.isNull(p.getId())){
                        return Mono.error(new InterruptedException("No existe el producto a eliminar"));
                    }
                    return Mono.just(p);
                })
                .flatMap(p->productoService.delete(p))
                .then(Mono.just("redirect:/listar?success=producto+eliminado+con+exito"))
                .onErrorResume(ex->Mono.just("redirect:/listar?error=no+existe+el+producto"));
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
