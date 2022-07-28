package com.example.springbootwebfluxapirest.app;

import com.example.springbootwebfluxapirest.app.models.documents.CategoriaDocument;
import com.example.springbootwebfluxapirest.app.models.documents.ProductoDocument;
import com.example.springbootwebfluxapirest.app.models.service.ProductoService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import java.util.Collections;
import java.util.List;

//Activar mock
@AutoConfigureWebTestClient
@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//Activar mock
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SpringBootWebfluxApiRestApplicationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductoService productoService;

    @Value("${config.base.endpoint.v2}")
    private String baseUrlv2;
    private String baseUrlv2Id = baseUrlv2 + "/{id}";

    @Test
    public void listarTest(){
        webTestClient.get()
                .uri(baseUrlv2)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(ProductoDocument.class)
                //.hasSize(6);
                .consumeWith(response->{
                    List<ProductoDocument> productos = response.getResponseBody();
                    productos.forEach(p->System.out.println(p.getNombre()));
                    Assertions.assertThat(productos.size()>0).isTrue();
                });
    }

    //@Test
    public void verTest(){
        ProductoDocument productoDocument =productoService.findByNombre("TV Panasonic Pantalla LCD").block();


        webTestClient.get()
                .uri(baseUrlv2 + "/{id}", Collections.singletonMap("id", productoDocument.getId()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ProductoDocument.class)
                .consumeWith(response->{
                    ProductoDocument p = response.getResponseBody();
                    Assertions.assertThat(p.getId()).isNotEmpty();
                    Assertions.assertThat(p.getId().length()>0).isTrue();
                    Assertions.assertThat(p.getNombre()).isEqualTo("TV Panasonic Pantalla LCD");
                });
                /*.expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo("TV Panasonic Pantalla LCD");*/
    }

    @Test
    public void crearTest(){
        CategoriaDocument categoriaDocument = productoService.findCategoriaByNombre("Muebles").block();
        ProductoDocument productoDocument = new ProductoDocument("Mesa comedor fuck yea", 100.00,categoriaDocument);

        webTestClient.post().uri(baseUrlv2)
                //mediatype del request
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                //mediatype del response
                .accept(MediaType.APPLICATION_JSON_UTF8)
                //body de request
                .body(Mono.just(productoDocument),ProductoDocument.class)
                //ejecuta peticion
                .exchange()
                //evaluar codigo http devuelto
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                //Obtener body de response
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo("Mesa comedor fuck yea")
                .jsonPath("$.categoria.nombre").isEqualTo("Muebles");
    }

    @Test
    public void editarTest(){
        ProductoDocument productoDocument =productoService.findByNombre("Apple iPod").block();
        CategoriaDocument categoriaDocument = productoService.findCategoriaByNombre("Computacion").block();

        ProductoDocument productoDocumentEdit = new ProductoDocument("Apple MAC M1", 10000.00,categoriaDocument);
        webTestClient.put().uri(baseUrlv2 + "/{id}", Collections.singletonMap("id", productoDocument.getId()))
                //mediatype del request
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                //mediatype del response
                .accept(MediaType.APPLICATION_JSON_UTF8)
                //body de request
                .body(Mono.just(productoDocumentEdit),ProductoDocument.class)
                //ejecuta peticion
                .exchange()
                //evaluar codigo http devuelto
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                //Obtener body de response
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.nombre").isEqualTo("Apple MAC M1")
                .jsonPath("$.categoria.nombre").isEqualTo("Computacion");
    }

    //@Test
    public void eliminarTest(){
        ProductoDocument productoDocument =productoService.findByNombre("Mica comoda 5 cajones").block();
        webTestClient.delete().uri(baseUrlv2 + "/{id}", Collections.singletonMap("id", productoDocument.getId()))
                //ejecuta peticion
                .exchange()
                //evaluar codigo http devuelto
                .expectStatus().isNoContent()
                //Obtener body de response
                .expectBody()
                .isEmpty();

        //comprobar que se borro registro
        webTestClient.get().uri(baseUrlv2 + "/{id}", Collections.singletonMap("id", productoDocument.getId()))
                //ejecuta peticion
                .exchange()
                //evaluar codigo http devuelto
                .expectStatus().isNotFound()
                //Obtener body de response
                .expectBody()
                .isEmpty();


    }


}
