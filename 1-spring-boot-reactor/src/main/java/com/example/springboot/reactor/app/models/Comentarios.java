package com.example.springboot.reactor.app.models;

import java.util.ArrayList;
import java.util.List;

public class Comentarios {
    private List<String> comentarios;

    public Comentarios() {
        this.comentarios = new ArrayList<>();
    }

    public Comentarios addComentario(String comentario){
        this.comentarios.add(comentario);
        return this;
    }

    @Override
    public String toString() {
        return "comentarios=" + comentarios;
    }
}
