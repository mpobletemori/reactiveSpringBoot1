package com.example.springbootwebflux;

import com.example.springbootwebflux.models.documents.ProductoDocument;
import com.example.springbootwebflux.models.repository.ProductoRepository;
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
public class SpringBootWebfluxApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Autowired
	private ProductoRepository productoRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		reactiveMongoTemplate.dropCollection("productos").subscribe();
		Flux.just(new ProductoDocument("TV Panasonic Pantalla LCD",456.89)
				,new ProductoDocument("Sony Camara HD Digital",177.89)
		        ,new ProductoDocument("Apple iPod",46.89)
				,new ProductoDocument("Sony Notebook",846.89))
				.flatMap(prod->{
					prod.setCreateAt(new Date());
					return productoRepository.save(prod);})
				.subscribe(productoDocument -> log.info("Insert:"+productoDocument.getId()+" "+productoDocument.getNombre()));


	}
}
