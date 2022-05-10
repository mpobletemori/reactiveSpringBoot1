package ccom.example.springboot.reactor.app;
import ccom.example.springboot.reactor.app.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import java.util.Objects;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Flux<Usuario> nombres = Flux.just("Manuel","Santi","Betty","Shobi","Eli","Antonio")
				.map(nombre->{
					var nombreUpperCase =nombre.toUpperCase();
					log.info("map="+nombreUpperCase);
					return new Usuario(nombreUpperCase,null);
				})
				.doOnNext(usuario->{
					if(Objects.isNull(usuario)){
						log.error("campo vacio");
						throw new RuntimeException("Nombres no puede ser validado");
					}
					log.info("doOnNext="+usuario.getNombre());
				}).map(usuario->{
					var nombreLowerCase =usuario.getNombre().toLowerCase();
					log.info("map="+nombreLowerCase);
					usuario.setNombre(nombreLowerCase);
					return usuario;
				});

		nombres.subscribe((usuario)->log.info(usuario.toString()), error -> log.error(error.getMessage()), new Runnable() {
			@Override
			public void run() {
				//se lanza cuando se procesa el total de lista
				log.info("Ha finalizado la ejecucion del observable con exito!");
			}
		});
	}
}
