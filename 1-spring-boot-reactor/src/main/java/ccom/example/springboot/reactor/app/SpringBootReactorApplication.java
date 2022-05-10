package ccom.example.springboot.reactor.app;
import ccom.example.springboot.reactor.app.models.Comentarios;
import ccom.example.springboot.reactor.app.models.Usuario;
import ccom.example.springboot.reactor.app.models.UsuarioComentarios;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {
	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//ejemploIterable();
		//ejemploFlatMap();
		//ejemploToString();
		//ejemploCollectToList();
		//ejemploUsuarioComentariosFlatMap();
		//ejemploUsuarioComentariosZipWith();
		//ejemploUsuarioComentariosZipWithForma2();
		//ejemploZipWithRangos();
		//ejemploInterval();
		ejemploDelayElements();
		log.info("culminacion de ejecucion de ejemplo");
	}

	public void ejemploDelayElements() throws InterruptedException {
		Flux<Integer> rango = Flux.range(1, 12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(i-> log.info(i.toString()));
		rango.blockLast();//bloquea el flujo no recomendable usar en prod
		//rango.subscribe();//al usar subscribe se ejecuta en 2do plano
		Thread.sleep(13000);
	}

	public void ejemploInterval(){
		Flux<Integer> rango = Flux.range(1, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));

		rango.zipWith(retraso,(ra,re)->ra)
				.doOnNext(i-> log.info(i.toString()))
				.blockLast();//bloquea el flujo no recomendable usar en prod
				//.subscribe();//al usar subscribe se ejecuta en 2do plano
	}
	public void ejemploZipWithRangos(){
		Flux<Integer> rangosIntFlux = Flux.range(0, 4);
		Flux.just(1,2,3,4)
				.map(i-> (i*2))
				.zipWith(rangosIntFlux,(uno,dos)->String.format("Primer Flux: %d, Segundo Flux: %d",uno,dos))
				.subscribe(texto->log.info(texto));
	}

	public void ejemploUsuarioComentariosZipWithForma2(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("John","Doe"));
		Mono<Comentarios> comentariosMono = Mono.just(new Comentarios()
				.addComentario("Hola pepe,que tal!")
				.addComentario("Mañana voy a la playa!")
				.addComentario("Estoy tomando el curso de spring con reactor"));
		usuarioMono.zipWith(comentariosMono)
				.map(tuple->{
					Usuario u = tuple.getT1();
					Comentarios c = tuple.getT2();
					return new UsuarioComentarios(u,c);
				}).subscribe(uc->log.info(uc.toString()));
	}


	public void ejemploUsuarioComentariosZipWith(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("John","Doe"));
		Mono<Comentarios> comentariosMono = Mono.just(new Comentarios()
				.addComentario("Hola pepe,que tal!")
				.addComentario("Mañana voy a la playa!")
				.addComentario("Estoy tomando el curso de spring con reactor"));
		usuarioMono.zipWith(comentariosMono,(usuario,comentarios)->new UsuarioComentarios(usuario,comentarios)).subscribe(uc->log.info(uc.toString()));
	}

	public void ejemploUsuarioComentariosFlatMap(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("John","Doe"));
		Mono<Comentarios> comentariosMono = Mono.just(new Comentarios()
				.addComentario("Hola pepe,que tal!")
				.addComentario("Mañana voy a la playa!")
				.addComentario("Estoy tomando el curso de spring con reactor"));

		usuarioMono.flatMap(u-> comentariosMono.map(c-> new UsuarioComentarios(u,c))).subscribe(uc->log.info(uc.toString()));
	}

	public void ejemploCollectToList() throws Exception {
		log.info("Ejemplo convertir Flux a Mono");
		List<Usuario> listNombres = List.of(new Usuario("Manuel","Poblete")
				,new Usuario("Santi","Poblete")
				,new Usuario("Betty","Cuenca")
				,new Usuario("Shobi","Ribeiro")
				,new Usuario("Eli","Mori")
				,new Usuario("Antonio","Sanchez")
				,new Usuario("Bruce","Lee")
				,new Usuario("Bruce","Willis"));

		Flux.fromIterable(listNombres)
				.collectList()
			.subscribe(listaUsuarios->{
				listaUsuarios.forEach(value->log.info(value.toString()));
		});
	}


	public void ejemploToString() throws Exception {
		log.info("Ejemplo convertir obj a string");
		List<Usuario> listNombres = List.of(new Usuario("Manuel","Poblete")
				                           ,new Usuario("Santi","Poblete")
				                           ,new Usuario("Betty","Cuenca")
				                           ,new Usuario("Shobi","Ribeiro")
				                           ,new Usuario("Eli","Mori")
				                           ,new Usuario("Antonio","Sanchez")
				                           ,new Usuario("Bruce","Lee")
				                           ,new Usuario("Bruce","Willis"));

		Flux.fromIterable(listNombres)
				.map(usuario->{
					String concat = new StringBuilder(usuario.getNombre().toUpperCase())
							.append(" ")
							.append(usuario.getApellido().toUpperCase())
							.toString();
					log.info("map="+concat);
					return concat;
				})
				.flatMap(nombre-> {
					if(nombre.contains("bruce".toUpperCase())){
						log.info("flatMap="+nombre+",mantener elemento en observable");
						return Mono.just(nombre);
					}
					log.info("flatMap="+nombre+", sacar elemento en observable");
					return Mono.empty();
				})
				.map(nombre->{
					var nombreLowerCase =nombre.toLowerCase();
					log.info("map="+nombreLowerCase);
					return nombreLowerCase;
				}).subscribe(log::info);
	}

	public void ejemploFlatMap() throws Exception {
		log.info("Ejemplo flatMap");
		List<String> listNombres = List.of("Manuel Poblete","Santi Poblete","Betty Cuenca","Shobi Ribeiro","Eli Mori","Antonio Sanchez","Bruce Lee","Bruce Willis");

		Flux.fromIterable(listNombres)
				.map(nombre->{
					var nombreUpperCase =nombre.toUpperCase();
					log.info("map="+nombreUpperCase);
					return new Usuario(nombreUpperCase.split(" ")[0],nombreUpperCase.split(" ")[1]);
				})
				.flatMap(usuario-> {
					if(usuario.getNombre().equalsIgnoreCase("bruce")){
						log.info("flatMap="+usuario.toString()+",mantener elemento en observable");
						return Mono.just(usuario);
					}
					log.info("flatMap="+usuario.toString()+", sacar elemento en observable");
					return Mono.empty();
				})
				.map(usuario->{
					var nombreLowerCase =usuario.getNombre().toLowerCase();
					log.info("map="+nombreLowerCase);
					usuario.setNombre(nombreLowerCase);
					return usuario;
				}).subscribe(usuario->log.info(usuario.toString()));
	}


	public void ejemploIterable() throws Exception {
		List<String> listNombres = List.of("Manuel Poblete","Santi Poblete","Betty Cuenca","Shobi Ribeiro","Eli Mori","Antonio Sanchez","Bruce Lee","Bruce Willis");
		Flux<String> nombres = Flux.fromIterable(listNombres);
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
