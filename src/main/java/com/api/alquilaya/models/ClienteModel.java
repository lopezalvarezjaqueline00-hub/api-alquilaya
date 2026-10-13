package com.api.alquilaya.models;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "cliente")
public class ClienteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente") // Identificador único del cliente - no puede ser nulo por base de datos - generado automáticamente
    private Long idCliente;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre; // Nombre del cliente - no puede ser nulo por base de datos - longitud máxima de 50 caracteres

    @NotBlank(message = "El apellido es obligatorio")
    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido; // Apellido del cliente - no puede ser nulo por base de datos - longitud máxima de 50 caracteres

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    @Column(nullable = false, unique = true, length = 100)
    private String email; // Correo electrónico del cliente - no puede ser nulo por base de datos - unico por base de datos - longitud máxima de 100 caracteres

    @NotBlank(message = "La licencia de conducir es obligatoria")
    @Column(name = "licencia_conducir", nullable = false, unique = true, length = 50)
    private String licenciaConducir; // Número de licencia de conducir del cliente - no puede ser nulo por base de datos - unico por base de datos - longitud máxima de 50 caracteres

    @Column(length = 20)
    private String telefono; // Número de teléfono del cliente - longitud máxima de 20 caracteres

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("cliente") 
    private List<AlquilerModel> alquileres; // Lista de alquileres asociados al cliente

    // Constructores, getters y setters para la clase ClienteModel
    // Constructor por defecto (permite crear instancias sin inicializar atributos)
    public ClienteModel() {
    }

    // Constructor con parámetros (permite crear instancias inicializando atributos)
    public ClienteModel(String nombre, String apellido, String email, String licenciaConducir, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.licenciaConducir = licenciaConducir;
        this.telefono = telefono;
    }

    // Getters y Setters
    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicenciaConducir() {
        return licenciaConducir;
    }

    public void setLicenciaConducir(String licenciaConducir) {
        this.licenciaConducir = licenciaConducir;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<AlquilerModel> getAlquileres() {
        return alquileres;
    }

    public void setAlquileres(List<AlquilerModel> alquileres) {
        this.alquileres = alquileres;
    }
}