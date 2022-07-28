package com.example.springbootwebfluxapirest.app;

import com.example.springbootwebfluxapirest.app.models.documents.CategoriaDocument;
import com.example.springbootwebfluxapirest.app.models.documents.ProductoDocument;
import com.example.springbootwebfluxapirest.app.models.service.ProductoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApiRestApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApiRestApplication.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Autowired
	private ProductoService productoService;



	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApiRestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		reactiveMongoTemplate.dropCollection("productos").subscribe();
		reactiveMongoTemplate.dropCollection("categorias").subscribe();

		CategoriaDocument electronica = new CategoriaDocument("Electronica");
		CategoriaDocument deporte = new CategoriaDocument("Deporte");
		CategoriaDocument computacion = new CategoriaDocument("Computacion");
		CategoriaDocument muebles = new CategoriaDocument("Muebles");

		Flux.just(electronica,deporte,computacion,muebles)
				.flatMap(productoService::saveCategoria)
				.doOnNext(p-> log.info("Categoria creada :{} , Id:{}",p.getNombre(),p.getId()))
				.thenMany(
						Flux.just(new ProductoDocument("TV Panasonic Pantalla LCD",456.89,electronica)
										,new ProductoDocument("Sony Camara HD Digital",177.89,electronica)
										,new ProductoDocument("Apple iPod",46.89,electronica)
										,new ProductoDocument("Sony Notebook",846.89,computacion)
										,new ProductoDocument("Bianchi Bicicleta",946.89,deporte)
										,new ProductoDocument("Mica comoda 5 cajones",1046.89,muebles))
								.flatMap(prod->{
									prod.setCreateAt(new Date());
									return productoService.save(prod);
								})
				).subscribe(productoDocument -> log.info("Insert:"+productoDocument.getId()+" "+productoDocument.getNombre()));
	}
}
