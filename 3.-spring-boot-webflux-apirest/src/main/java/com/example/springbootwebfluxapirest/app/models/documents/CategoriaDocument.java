package com.example.springbootwebfluxapirest.app.models.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;

@Document(collection = "categorias")
public class CategoriaDocument {
    
    @Id
    @NotEmpty
    private String id;
    private String nombre;

    public CategoriaDocument() {
    }

    public CategoriaDocument(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaDocument(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
