package com.api.alquilaya.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.alquilaya.models.AlquilerModel;
import com.api.alquilaya.models.ClienteModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.repositories.IAlquilerRepository;
import com.api.alquilaya.repositories.IClienteRepository;
import com.api.alquilaya.repositories.IVehiculoRepository;

// Pruebas unitarias de las reglas de negocio del alquiler (sin base de datos, usando Mockito)
@ExtendWith(MockitoExtension.class)
class AlquilerServiceTest {

    @Mock private IAlquilerRepository alquilerRepository;
    @Mock private IVehiculoRepository vehiculoRepository;
    @Mock private IClienteRepository clienteRepository;

    private AlquilerService service;
    private VehiculoModel vehiculo;
    private ClienteModel cliente;

    @BeforeEach
    void setUp() {
        service = new AlquilerService(alquilerRepository, vehiculoRepository, clienteRepository);

        vehiculo = new VehiculoModel("Nissan", "Versa", 2024, "ABC-123", new BigDecimal("650.00"), "DISPONIBLE");
        vehiculo.setIdVehiculo(1L);

        cliente = new ClienteModel("Ana", "Pérez", "ana@correo.com", "LIC-001", "2221234567");
        cliente.setIdCliente(1L);
    }

    @Test
    void calculaElPrecioYMarcaElVehiculoComoAlquilado() {
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(alquilerRepository.existeSolapamiento(eq(1L), any(), any(), any())).thenReturn(false);
        when(alquilerRepository.save(any(AlquilerModel.class))).thenAnswer(inv -> inv.getArgument(0));

        AlquilerModel resultado = service.saveAlquiler(alquiler(LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 18)));

        assertEquals(new BigDecimal("1950.00"), resultado.getPrecioTotal()); // 3 días x 650
        assertEquals("ACTIVO", resultado.getEstado());
        assertEquals("ALQUILADO", vehiculo.getEstado());
    }

    @Test
    void rechazaFechasInvalidas() {
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.saveAlquiler(alquiler(LocalDate.of(2026, 10, 18), LocalDate.of(2026, 10, 15))));

        assertEquals("La fecha de devolución debe ser posterior a la fecha de alquiler", ex.getMessage());
    }

    @Test
    void rechazaUnVehiculoYaAlquiladoEnEsasFechas() {
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(alquilerRepository.existeSolapamiento(eq(1L), any(), any(), any())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.saveAlquiler(alquiler(LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 18))));

        assertEquals("El vehículo ya está alquilado en esas fechas", ex.getMessage());
    }

    @Test
    void rechazaUnVehiculoEnMantenimiento() {
        vehiculo.setEstado("MANTENIMIENTO");
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.saveAlquiler(alquiler(LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 18))));

        assertEquals("El vehículo está en mantenimiento y no se puede alquilar", ex.getMessage());
    }

    @Test
    void finalizarLiberaElVehiculo() {
        vehiculo.setEstado("ALQUILADO");
        AlquilerModel activo = alquiler(LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 18));
        activo.setIdAlquiler(5L);
        activo.setVehiculo(vehiculo);
        when(alquilerRepository.findById(anyLong())).thenReturn(Optional.of(activo));
        when(alquilerRepository.save(any(AlquilerModel.class))).thenAnswer(inv -> inv.getArgument(0));

        AlquilerModel resultado = service.finalizarAlquiler(5L);

        assertEquals("FINALIZADO", resultado.getEstado());
        assertEquals("DISPONIBLE", vehiculo.getEstado());
    }

    private AlquilerModel alquiler(LocalDate inicio, LocalDate fin) {
        VehiculoModel refVehiculo = new VehiculoModel();
        refVehiculo.setIdVehiculo(1L);
        ClienteModel refCliente = new ClienteModel();
        refCliente.setIdCliente(1L);
        return new AlquilerModel(refVehiculo, refCliente, inicio, fin, null);
    }
}
