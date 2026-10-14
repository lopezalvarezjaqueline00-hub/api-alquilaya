package com.api.alquilaya.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "vehiculo")
public class VehiculoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vehiculo")
    private Long idVehiculo; // Identificador único del vehículo - no puede ser nulo por base de datos - generado automáticamente

    @NotBlank(message = "La marca es obligatoria")
    @Column(nullable = false, length = 50)
    private String marca; // Marca del vehículo - no puede ser nulo por base de datos - longitud máxima de 50 caracteres

    @NotBlank(message = "El modelo es obligatorio")
    @Column(nullable = false, length = 50)
    private String modelo; // Modelo del vehículo - no puede ser nulo por base de datos - longitud máxima de 50 caracteres

    @NotNull(message = "El año es obligatorio")
    @Column(nullable = false)
    private Integer anio; // Año del vehículo - no puede ser nulo por base de datos

    @NotBlank(message = "La matrícula es obligatoria")
    @Column(nullable = false, unique = true, length = 20)
    private String matricula; // Matrícula del vehículo - no puede ser nulo por base de datos - unico por base de datos - longitud máxima de 20 caracteres

    @NotNull(message = "La tarifa diaria es obligatoria")
    @Positive(message = "La tarifa diaria debe ser mayor que cero")
    @Column(name = "tarifa_diaria", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaDiaria; // Tarifa diaria del vehículo - no puede ser nulo por base de datos - precisión 10, escala 2

    @Column(nullable = false, length = 20)
    private String estado = "DISPONIBLE"; // Estado del vehículo (DISPONIBLE, ALQUILADO, MANTENIMIENTO) - no puede ser nulo por base de datos - longitud máxima de 20 caracteres

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_oficina") // Clave foránea a la oficina donde se encuentra el vehículo - opcional
    private OficinaAlquilerModel oficina; // Oficina de alquiler a la que pertenece el vehículo

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Se consulta con /api/alquileres/... ; evita referencias circulares en el JSON
    private List<AlquilerModel> alquileres; // Lista de alquileres asociados al vehículo

    // Constructores, getters y setters para la clase VehiculoModel
    // Constructor por defecto (permite crear instancias sin inicializar atributos)
    public VehiculoModel() {
    }

    // Constructor con parámetros (permite crear instancias inicializando atributos)
    public VehiculoModel(String marca, String modelo, Integer anio, String matricula, BigDecimal tarifaDiaria, String estado) {
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.matricula = matricula;
        this.tarifaDiaria = tarifaDiaria;
        this.estado = estado;
    }

    // Getters y Setters
    public Long getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Long idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public BigDecimal getTarifaDiaria() {
        return tarifaDiaria;
    }

    public void setTarifaDiaria(BigDecimal tarifaDiaria) {
        this.tarifaDiaria = tarifaDiaria;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public OficinaAlquilerModel getOficina() {
        return oficina;
    }

    public void setOficina(OficinaAlquilerModel oficina) {
        this.oficina = oficina;
    }

    public List<AlquilerModel> getAlquileres() {
        return alquileres;
    }

    public void setAlquileres(List<AlquilerModel> alquileres) {
        this.alquileres = alquileres;
    }
}