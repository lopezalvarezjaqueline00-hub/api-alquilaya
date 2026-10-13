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

import com.api.alquilaya.models.OficinaAlquilerModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.services.OficinaAlquilerService;
import com.api.alquilaya.services.VehiculoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/oficinas")
public class OficinaAlquilerController {

    private final OficinaAlquilerService oficinaService;
    private final VehiculoService vehiculoService;

    public OficinaAlquilerController(OficinaAlquilerService oficinaService, VehiculoService vehiculoService) {
        this.oficinaService = oficinaService;
        this.vehiculoService = vehiculoService;
    }

    // Obtener todas las oficinas
    @GetMapping
    public List<OficinaAlquilerModel> getAllOficinas() {
        return oficinaService.getAllOficinas();
    }

    // Obtener una oficina por ID
    @GetMapping("/{id}")
    public ResponseEntity<OficinaAlquilerModel> getOficinaById(@PathVariable Long id) {
        return oficinaService.getOficinaById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Buscar oficinas por ciudad
    @GetMapping("/ciudad/{ciudad}")
    public List<OficinaAlquilerModel> getOficinasByCiudad(@PathVariable String ciudad) {
        return oficinaService.getOficinasByCiudad(ciudad);
    }

    // Vehículos que pertenecen a una oficina
    @GetMapping("/{id}/vehiculos")
    public ResponseEntity<List<VehiculoModel>> getVehiculosDeOficina(@PathVariable Long id) {
        if (oficinaService.getOficinaById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehiculoService.getVehiculosByOficina(id));
    }

    // Crear una oficina
    @PostMapping
    public ResponseEntity<OficinaAlquilerModel> createOficina(@Valid @RequestBody OficinaAlquilerModel oficina) {
        oficina.setIdOficina(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(oficinaService.saveOficina(oficina));
    }

    // Actualizar una oficina
    @PutMapping("/{id}")
    public ResponseEntity<OficinaAlquilerModel> updateOficina(@PathVariable Long id, @Valid @RequestBody OficinaAlquilerModel oficina) {
        if (oficinaService.getOficinaById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        oficina.setIdOficina(id);
        return ResponseEntity.ok(oficinaService.saveOficina(oficina));
    }

    // Eliminar una oficina
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOficina(@PathVariable Long id) {
        if (oficinaService.getOficinaById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        oficinaService.deleteOficina(id);
        return ResponseEntity.noContent().build();
    }
}
