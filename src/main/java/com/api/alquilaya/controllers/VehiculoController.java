package com.api.alquilaya.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.services.VehiculoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    // Obtener todos los vehículos
    @GetMapping
    public List<VehiculoModel> getAllVehiculos() {
        return vehiculoService.getAllVehiculos();
    }

    // Obtener un vehículo por ID
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoModel> getVehiculoById(@PathVariable Long id) {
        return vehiculoService.getVehiculoById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Buscar un vehículo por matrícula
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<VehiculoModel> getVehiculoByMatricula(@PathVariable String matricula) {
        return vehiculoService.getVehiculoByMatricula(matricula)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Listar vehículos por estado (DISPONIBLE, ALQUILADO, MANTENIMIENTO)
    @GetMapping("/estado/{estado}")
    public List<VehiculoModel> getVehiculosByEstado(@PathVariable String estado) {
        return vehiculoService.getVehiculosByEstado(estado);
    }

    // Crear un vehículo
    @PostMapping
    public ResponseEntity<VehiculoModel> createVehiculo(@Valid @RequestBody VehiculoModel vehiculo) {
        vehiculo.setIdVehiculo(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoService.saveVehiculo(vehiculo));
    }

    // Actualizar un vehículo
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoModel> updateVehiculo(@PathVariable Long id, @Valid @RequestBody VehiculoModel vehiculo) {
        if (vehiculoService.getVehiculoById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        vehiculo.setIdVehiculo(id);
        return ResponseEntity.ok(vehiculoService.saveVehiculo(vehiculo));
    }

    // Eliminar un vehículo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehiculo(@PathVariable Long id) {
        if (vehiculoService.getVehiculoById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        vehiculoService.deleteVehiculo(id);
        return ResponseEntity.noContent().build();
    }
}
