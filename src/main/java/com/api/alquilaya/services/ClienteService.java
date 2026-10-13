package com.api.alquilaya.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.alquilaya.models.ClienteModel;
import com.api.alquilaya.repositories.IClienteRepository;

@Service 
public class ClienteService {

    private final IClienteRepository clienteRepository;

    public ClienteService(IClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // Obtener todos los clientes
    public List<ClienteModel> getAllClientes() {
        return clienteRepository.findAll();
    }

    // Obtener un cliente por su ID
    public Optional<ClienteModel> getClienteById(Long id) {
        return clienteRepository.findById(id);
    }

    // Crear o actualizar un cliente validando que el email y la licencia no estén repetidos
    @Transactional
    public ClienteModel saveCliente(ClienteModel cliente) {
        clienteRepository.findByEmail(cliente.getEmail())
                .filter(otro -> !otro.getIdCliente().equals(cliente.getIdCliente()))
                .ifPresent(otro -> { throw new IllegalArgumentException("Ya existe un cliente con ese email"); });

        clienteRepository.findByLicenciaConducir(cliente.getLicenciaConducir())
                .filter(otro -> !otro.getIdCliente().equals(cliente.getIdCliente()))
                .ifPresent(otro -> { throw new IllegalArgumentException("Ya existe un cliente con esa licencia de conducir"); });

        return clienteRepository.save(cliente);
    }

    // Buscar un cliente por email
    public Optional<ClienteModel> getClienteByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    // Buscar un cliente por licencia de conducir
    public Optional<ClienteModel> getClienteByLicencia(String licencia) {
        return clienteRepository.findByLicenciaConducir(licencia);
    }

    // Eliminar un cliente (también se eliminan sus alquileres por el CascadeType.ALL)
    public void deleteCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}
