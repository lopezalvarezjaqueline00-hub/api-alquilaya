package com.api.alquilaya.services;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.alquilaya.models.AlquilerModel;
import com.api.alquilaya.models.ClienteModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.repositories.IAlquilerRepository;
import com.api.alquilaya.repositories.IClienteRepository;
import com.api.alquilaya.repositories.IVehiculoRepository;

@Service
public class AlquilerService {

    // Estados válidos para un alquiler
    public static final String ACTIVO = "ACTIVO";
    public static final String FINALIZADO = "FINALIZADO";
    public static final String CANCELADO = "CANCELADO";

    private final IAlquilerRepository alquilerRepository;
    private final IVehiculoRepository vehiculoRepository;
    private final IClienteRepository clienteRepository;

    public AlquilerService(IAlquilerRepository alquilerRepository,
                           IVehiculoRepository vehiculoRepository,
                           IClienteRepository clienteRepository) {
        this.alquilerRepository = alquilerRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    // Obtener todos los alquileres
    public List<AlquilerModel> getAllAlquileres() {
        return alquilerRepository.findAll();
    }

    // Obtener un alquiler por su ID
    public Optional<AlquilerModel> getAlquilerById(Long id) {
        return alquilerRepository.findById(id);
    }

    // Alquileres de un cliente
    public List<AlquilerModel> getAlquileresByCliente(Long idCliente) {
        return alquilerRepository.findByCliente_IdCliente(idCliente);
    }

    // Alquileres de un vehículo
    public List<AlquilerModel> getAlquileresByVehiculo(Long idVehiculo) {
        return alquilerRepository.findByVehiculo_IdVehiculo(idVehiculo);
    }

    // Alquileres por estado
    public List<AlquilerModel> getAlquileresByEstado(String estado) {
        return alquilerRepository.findByEstado(estado.toUpperCase());
    }

    // Registrar o actualizar un alquiler.
    // Reglas de negocio:
    //  1. El vehículo y el cliente deben existir.
    //  2. La fecha de devolución debe ser posterior a la fecha de alquiler.
    //  3. Un vehículo en MANTENIMIENTO no se puede alquilar.
    //  4. El vehículo no puede tener otro alquiler ACTIVO en las mismas fechas.
    //  5. El precio total se calcula: tarifa diaria x número de días.
    //  6. Al registrar el alquiler, el vehículo pasa a ALQUILADO.
    @Transactional
    public AlquilerModel saveAlquiler(AlquilerModel alquiler) {
        VehiculoModel vehiculo = resolverVehiculo(alquiler);
        ClienteModel cliente = resolverCliente(alquiler);

        if (alquiler.getFechaAlquiler() == null || alquiler.getFechaDevolucion() == null) {
            throw new IllegalArgumentException("Las fechas de alquiler y devolución son obligatorias");
        }
        if (!alquiler.getFechaDevolucion().isAfter(alquiler.getFechaAlquiler())) {
            throw new IllegalArgumentException("La fecha de devolución debe ser posterior a la fecha de alquiler");
        }

        if (alquiler.getEstado() == null || alquiler.getEstado().isBlank()) {
            alquiler.setEstado(ACTIVO);
        }
        alquiler.setEstado(alquiler.getEstado().toUpperCase());
        if (!List.of(ACTIVO, FINALIZADO, CANCELADO).contains(alquiler.getEstado())) {
            throw new IllegalArgumentException("Estado no válido. Usa ACTIVO, FINALIZADO o CANCELADO");
        }

        if (ACTIVO.equals(alquiler.getEstado())) {
            if (VehiculoService.MANTENIMIENTO.equals(vehiculo.getEstado())) {
                throw new IllegalArgumentException("El vehículo está en mantenimiento y no se puede alquilar");
            }
            if (alquilerRepository.existeSolapamiento(vehiculo.getIdVehiculo(), alquiler.getIdAlquiler(),
                    alquiler.getFechaAlquiler(), alquiler.getFechaDevolucion())) {
                throw new IllegalArgumentException("El vehículo ya está alquilado en esas fechas");
            }
        }

        long dias = ChronoUnit.DAYS.between(alquiler.getFechaAlquiler(), alquiler.getFechaDevolucion());
        alquiler.setPrecioTotal(vehiculo.getTarifaDiaria().multiply(BigDecimal.valueOf(dias)));

        // Si se está actualizando un alquiler que estaba ACTIVO y cambia de vehículo o deja de estar activo,
        // el vehículo anterior se libera
        if (alquiler.getIdAlquiler() != null) {
            alquilerRepository.findById(alquiler.getIdAlquiler())
                    .filter(anterior -> ACTIVO.equals(anterior.getEstado()))
                    .ifPresent(anterior -> liberarVehiculo(anterior.getVehiculo()));
        }

        alquiler.setVehiculo(vehiculo);
        alquiler.setCliente(cliente);

        if (ACTIVO.equals(alquiler.getEstado())) {
            vehiculo.setEstado(VehiculoService.ALQUILADO);
            vehiculoRepository.save(vehiculo);
        }
        return alquilerRepository.save(alquiler);
    }

    // Finalizar un alquiler: el cliente devolvió el vehículo y este vuelve a estar DISPONIBLE
    @Transactional
    public AlquilerModel finalizarAlquiler(Long id) {
        return cerrarAlquiler(id, FINALIZADO);
    }

    // Cancelar un alquiler: el vehículo vuelve a estar DISPONIBLE
    @Transactional
    public AlquilerModel cancelarAlquiler(Long id) {
        return cerrarAlquiler(id, CANCELADO);
    }

    // Eliminar un alquiler; si estaba ACTIVO se libera el vehículo
    @Transactional
    public void deleteAlquiler(Long id) {
        AlquilerModel alquiler = obtener(id);
        if (ACTIVO.equals(alquiler.getEstado())) {
            liberarVehiculo(alquiler.getVehiculo());
        }
        alquilerRepository.deleteById(id);
    }

    private AlquilerModel cerrarAlquiler(Long id, String nuevoEstado) {
        AlquilerModel alquiler = obtener(id);
        if (!ACTIVO.equals(alquiler.getEstado())) {
            throw new IllegalArgumentException("Solo se puede cerrar un alquiler ACTIVO");
        }
        alquiler.setEstado(nuevoEstado);
        liberarVehiculo(alquiler.getVehiculo());
        return alquilerRepository.save(alquiler);
    }

    private void liberarVehiculo(VehiculoModel vehiculo) {
        if (VehiculoService.ALQUILADO.equals(vehiculo.getEstado())) {
            vehiculo.setEstado(VehiculoService.DISPONIBLE);
            vehiculoRepository.save(vehiculo);
        }
    }

    private AlquilerModel obtener(Long id) {
        return alquilerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El alquiler no existe"));
    }

    private VehiculoModel resolverVehiculo(AlquilerModel alquiler) {
        if (alquiler.getVehiculo() == null || alquiler.getVehiculo().getIdVehiculo() == null) {
            throw new IllegalArgumentException("El vehículo es obligatorio");
        }
        return vehiculoRepository.findById(alquiler.getVehiculo().getIdVehiculo())
                .orElseThrow(() -> new IllegalArgumentException("El vehículo no existe"));
    }

    private ClienteModel resolverCliente(AlquilerModel alquiler) {
        if (alquiler.getCliente() == null || alquiler.getCliente().getIdCliente() == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        return clienteRepository.findById(alquiler.getCliente().getIdCliente())
                .orElseThrow(() -> new IllegalArgumentException("El cliente no existe"));
    }
}
