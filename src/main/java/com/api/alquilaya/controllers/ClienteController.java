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

import com.api.alquilaya.models.ClienteModel;
import com.api.alquilaya.services.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Obtener todos los clientes
    @GetMapping
    public List<ClienteModel> getAllClientes() {
        return clienteService.getAllClientes();
    }

    // Obtener un cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteModel> getClienteById(@PathVariable Long id) {
        return clienteService.getClienteById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Obtener un cliente por email
    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteModel> getClienteByEmail(@PathVariable String email) {
        return clienteService.getClienteByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());        
    }

    // Obtener un cliente por licencia
    @GetMapping("/licencia/{licencia}")
    public ResponseEntity<ClienteModel> getClienteByLicencia(@PathVariable String licencia) {
        return clienteService.getClienteByLicencia(licencia)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());        
    }

    // Crear un cliente
    @PostMapping
    public ResponseEntity<ClienteModel> createCliente(@Valid @RequestBody ClienteModel cliente) {
        cliente.setIdCliente(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.saveCliente(cliente));
    }

    // Actualizar un cliente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteModel> updateCliente(@PathVariable Long id, @Valid @RequestBody ClienteModel cliente) {
        if (clienteService.getClienteById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        cliente.setIdCliente(id);
        return ResponseEntity.ok(clienteService.saveCliente(cliente));
    }

    // Eliminar un cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        if (clienteService.getClienteById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }
}
