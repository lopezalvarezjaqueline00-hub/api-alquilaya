package com.api.alquilaya.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.api.alquilaya.models.OficinaAlquilerModel;
import com.api.alquilaya.repositories.IOficinaAlquilerRepository;

@Service
public class OficinaAlquilerService {

    private final IOficinaAlquilerRepository oficinaRepository;

    public OficinaAlquilerService(IOficinaAlquilerRepository oficinaRepository) {
        this.oficinaRepository = oficinaRepository;
    }

    // Obtener todas las oficinas
    public List<OficinaAlquilerModel> getAllOficinas() {
        return oficinaRepository.findAll();
    }

    // Obtener una oficina por su ID
    public Optional<OficinaAlquilerModel> getOficinaById(Long id) {
        return oficinaRepository.findById(id);
    }

    // Buscar oficinas por ciudad
    public List<OficinaAlquilerModel> getOficinasByCiudad(String ciudad) {
        return oficinaRepository.findByCiudadIgnoreCase(ciudad);
    }

    // Crear o actualizar una oficina
    public OficinaAlquilerModel saveOficina(OficinaAlquilerModel oficina) {
        return oficinaRepository.save(oficina);
    }

    // Eliminar una oficina
    public void deleteOficina(Long id) {
        oficinaRepository.deleteById(id);
    }
}
