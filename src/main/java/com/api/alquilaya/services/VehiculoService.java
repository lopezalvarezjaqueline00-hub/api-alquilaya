package com.api.alquilaya.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.alquilaya.models.OficinaAlquilerModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.repositories.IOficinaAlquilerRepository;
import com.api.alquilaya.repositories.IVehiculoRepository;

@Service
public class VehiculoService {

    // Estados válidos para un vehículo
    public static final String DISPONIBLE = "DISPONIBLE";
    public static final String ALQUILADO = "ALQUILADO";
    public static final String MANTENIMIENTO = "MANTENIMIENTO";
    private static final Set<String> ESTADOS = Set.of(DISPONIBLE, ALQUILADO, MANTENIMIENTO);

    private final IVehiculoRepository vehiculoRepository;
    private final IOficinaAlquilerRepository oficinaRepository;

    public VehiculoService(IVehiculoRepository vehiculoRepository, IOficinaAlquilerRepository oficinaRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.oficinaRepository = oficinaRepository;
    }

    // Obtener todos los vehículos
    public List<VehiculoModel> getAllVehiculos() {
        return vehiculoRepository.findAll();
    }

    // Obtener un vehículo por su ID
    public Optional<VehiculoModel> getVehiculoById(Long id) {
        return vehiculoRepository.findById(id);
    }

    // Buscar un vehículo por matrícula
    public Optional<VehiculoModel> getVehiculoByMatricula(String matricula) {
        return vehiculoRepository.findByMatricula(matricula);
    }

    // Listar vehículos por estado
    public List<VehiculoModel> getVehiculosByEstado(String estado) {
        return vehiculoRepository.findByEstado(estado.toUpperCase());
    }

    // Listar vehículos de una oficina
    public List<VehiculoModel> getVehiculosByOficina(Long idOficina) {
        return vehiculoRepository.findByOficina_IdOficina(idOficina);
    }

    // Crear o actualizar un vehículo con sus validaciones
    @Transactional
    public VehiculoModel saveVehiculo(VehiculoModel vehiculo) {
        // Estado: si no se envía queda DISPONIBLE; si se envía debe ser uno de los válidos
        if (vehiculo.getEstado() == null || vehiculo.getEstado().isBlank()) {
            vehiculo.setEstado(DISPONIBLE);
        }
        vehiculo.setEstado(vehiculo.getEstado().toUpperCase());
        if (!ESTADOS.contains(vehiculo.getEstado())) {
            throw new IllegalArgumentException("Estado no válido. Usa DISPONIBLE, ALQUILADO o MANTENIMIENTO");
        }

        // La matrícula no se puede repetir
        vehiculoRepository.findByMatricula(vehiculo.getMatricula())
                .filter(otro -> !otro.getIdVehiculo().equals(vehiculo.getIdVehiculo()))
                .ifPresent(otro -> { throw new IllegalArgumentException("Ya existe un vehículo con esa matrícula"); });

        // Si se indica una oficina, se busca la oficina real en la base de datos
        if (vehiculo.getOficina() != null) {
            Long idOficina = vehiculo.getOficina().getIdOficina();
            if (idOficina == null) {
                throw new IllegalArgumentException("La oficina debe indicar su idOficina");
            }
            OficinaAlquilerModel oficina = oficinaRepository.findById(idOficina)
                    .orElseThrow(() -> new IllegalArgumentException("La oficina no existe"));
            vehiculo.setOficina(oficina);
        }

        return vehiculoRepository.save(vehiculo);
    }

    // Eliminar un vehículo
    public void deleteVehiculo(Long id) {
        vehiculoRepository.deleteById(id);
    }
}
