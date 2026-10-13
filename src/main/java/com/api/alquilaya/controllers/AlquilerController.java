package com.api.alquilaya.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.alquilaya.models.AlquilerModel;
import com.api.alquilaya.services.AlquilerService;

@RestController
@RequestMapping("/api/alquileres")
public class AlquilerController {

    private final AlquilerService alquilerService;

    public AlquilerController(AlquilerService alquilerService) {
        this.alquilerService = alquilerService;
    }

    // Obtener todos los alquileres
    @GetMapping
    public List<AlquilerModel> getAllAlquileres() {
        return alquilerService.getAllAlquileres();
    }

    // Obtener un alquiler por ID
    @GetMapping("/{id}")
    public ResponseEntity<AlquilerModel> getAlquilerById(@PathVariable Long id) {
        return alquilerService.getAlquilerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Alquileres de un cliente
    @GetMapping("/cliente/{idCliente}")
    public List<AlquilerModel> getAlquileresByCliente(@PathVariable Long idCliente) {
        return alquilerService.getAlquileresByCliente(idCliente);
    }

    // Alquileres de un vehículo
    @GetMapping("/vehiculo/{idVehiculo}")
    public List<AlquilerModel> getAlquileresByVehiculo(@PathVariable Long idVehiculo) {
        return alquilerService.getAlquileresByVehiculo(idVehiculo);
    }

    // Alquileres por estado (ACTIVO, FINALIZADO, CANCELADO)
    @GetMapping("/estado/{estado}")
    public List<AlquilerModel> getAlquileresByEstado(@PathVariable String estado) {
        return alquilerService.getAlquileresByEstado(estado);
    }

    // Registrar un alquiler (el precio total se calcula automáticamente)
    @PostMapping
    public ResponseEntity<AlquilerModel> createAlquiler(@RequestBody AlquilerModel alquiler) {
        alquiler.setIdAlquiler(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(alquilerService.saveAlquiler(alquiler));
    }

    // Actualizar un alquiler
    @PutMapping("/{id}")
    public ResponseEntity<AlquilerModel> updateAlquiler(@PathVariable Long id, @RequestBody AlquilerModel alquiler) {
        if (alquilerService.getAlquilerById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        alquiler.setIdAlquiler(id);
        return ResponseEntity.ok(alquilerService.saveAlquiler(alquiler));
    }

    // Finalizar un alquiler (devolución del vehículo)
    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<AlquilerModel> finalizarAlquiler(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.finalizarAlquiler(id));
    }

    // Cancelar un alquiler
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AlquilerModel> cancelarAlquiler(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.cancelarAlquiler(id));
    }

    // Eliminar un alquiler
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlquiler(@PathVariable Long id) {
        if (alquilerService.getAlquilerById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        alquilerService.deleteAlquiler(id);
        return ResponseEntity.noContent().build();
    }
}
