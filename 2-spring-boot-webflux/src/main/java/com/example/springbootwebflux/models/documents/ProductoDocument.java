package com.example.springbootwebflux.models.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Document(collection = "productos")
public class ProductoDocument {
    @Id
    private String id;

    @NotEmpty
    private String nombre;

    @NotNull
    private Double precio;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;

    @Valid
    private CategoriaDocument categoria;

    public ProductoDocument() {
    }

    public ProductoDocument(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public ProductoDocument(String nombre, Double precio,CategoriaDocument categoria) {
        this(nombre,precio);
        this.categoria = categoria;
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

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public CategoriaDocument getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDocument categoria) {
        this.categoria = categoria;
    }
}
