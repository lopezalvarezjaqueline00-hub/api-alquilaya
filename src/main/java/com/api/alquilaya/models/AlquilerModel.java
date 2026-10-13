package com.api.alquilaya.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "alquiler")
public class AlquilerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alquiler") // Identificador único del alquiler - no puede ser nulo por base de datos - generado automáticamente
    private Long idAlquiler;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vehiculo", nullable = false) // Clave foránea que referencia al vehículo asociado al alquiler - no puede ser nulo por base de datos
    @JsonIgnoreProperties("alquileres")
    private VehiculoModel vehiculo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false) // Clave foránea que referencia al cliente asociado al alquiler - no puede ser nulo por base de datos
    @JsonIgnoreProperties("alquileres")
    private ClienteModel cliente;

    @Column(name = "fecha_alquiler", nullable = false) // Fecha en que se realizó el alquiler - no puede ser nulo por base de datos
    private LocalDate fechaAlquiler;

    @Column(name = "fecha_devolucion", nullable = false) // Fecha en que se debe devolver el vehículo - no puede ser nulo por base de datos
    private LocalDate fechaDevolucion;

    @Column(name = "precio_total", precision = 10, scale = 2) // Precio total del alquiler - precisión 10, escala 2
    private BigDecimal precioTotal; // Se calcula automáticamente: tarifa diaria del vehículo x número de días

    @Column(nullable = false, length = 20)
    private String estado = "ACTIVO"; // Estado del alquiler (ACTIVO, FINALIZADO, CANCELADO)

    // Constructores, getters y setters para la clase AlquilerModel
    // Constructor por defecto (permite crear instancias sin inicializar atributos)
    public AlquilerModel() {
    }

    // Constructor con parámetros (permite crear instancias inicializando atributos)
    public AlquilerModel(VehiculoModel vehiculo, ClienteModel cliente, LocalDate fechaAlquiler, LocalDate fechaDevolucion, BigDecimal precioTotal) {
        this.vehiculo = vehiculo;
        this.cliente = cliente;
        this.fechaAlquiler = fechaAlquiler;
        this.fechaDevolucion = fechaDevolucion;
        this.precioTotal = precioTotal;
    }

    // Getters y Setters
    public Long getIdAlquiler() {
        return idAlquiler;
    }

    public void setIdAlquiler(Long idAlquiler) {
        this.idAlquiler = idAlquiler;
    }

    public VehiculoModel getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(VehiculoModel vehiculo) {
        this.vehiculo = vehiculo;
    }

    public ClienteModel getCliente() {
        return cliente;
    }

    public void setCliente(ClienteModel cliente) {
        this.cliente = cliente;
    }

    public LocalDate getFechaAlquiler() {
        return fechaAlquiler;
    }

    public void setFechaAlquiler(LocalDate fechaAlquiler) {
        this.fechaAlquiler = fechaAlquiler;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }
}