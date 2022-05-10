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
		Flux<String> nombres = Flux.just("Manuel Poblete","Santi Poblete","Betty Cuenca","Shobi Ribeiro","Eli Mori","Antonio Sanchez","Bruce Lee","Bruce Willis");
		Flux<Usuario> usuarios =nombres.map(nombre->{
					var nombreUpperCase =nombre.toUpperCase();
					log.info("map="+nombreUpperCase);
					return new Usuario(nombreUpperCase.split(" ")[0],nombreUpperCase.split(" ")[1]);
				})
				.filter(usuario-> {
					 var value = usuario.getNombre().equalsIgnoreCase("bruce");
					 log.info("filter="+usuario.toString()+",result="+value);
					 return value;
				})
				.doOnNext(usuario->{
					if(Objects.isNull(usuario)){
						log.error("campo vacio");
						throw new RuntimeException("Nombres no puede ser validado");
					}
					log.info("doOnNext="+usuario.toString());
				}).map(usuario->{
					var nombreLowerCase =usuario.getNombre().toLowerCase();
					log.info("map="+nombreLowerCase);
					usuario.setNombre(nombreLowerCase);
					return usuario;
				});

		usuarios.subscribe((usuario)->log.info(usuario.toString()), error -> log.error(error.getMessage()), new Runnable() {
			@Override
			public void run() {
				//se lanza cuando se procesa el total de lista
				log.info("Ha finalizado la ejecucion del observable con exito!");
			}
		});
	}
}
