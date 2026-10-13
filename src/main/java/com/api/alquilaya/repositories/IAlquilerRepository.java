package com.api.alquilaya.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.api.alquilaya.models.AlquilerModel;

@Repository
public interface IAlquilerRepository extends JpaRepository<AlquilerModel, Long> {

    // Alquileres de un cliente
    List<AlquilerModel> findByCliente_IdCliente(Long idCliente);

    // Alquileres de un vehículo
    List<AlquilerModel> findByVehiculo_IdVehiculo(Long idVehiculo);

    // Alquileres por estado (ACTIVO, FINALIZADO, CANCELADO)
    List<AlquilerModel> findByEstado(String estado);

    // Indica si el vehículo ya tiene un alquiler ACTIVO que se cruza con el rango de fechas.
    // El día de devolución queda libre para un nuevo alquiler.
    // Se excluye el propio alquiler (idAlquiler) para poder actualizarlo sin que choque consigo mismo.
    @Query("select count(a) > 0 from AlquilerModel a where a.vehiculo.idVehiculo = :idVehiculo "
            + "and (:idAlquiler is null or a.idAlquiler <> :idAlquiler) "
            + "and a.estado = 'ACTIVO' "
            + "and a.fechaAlquiler < :fin and a.fechaDevolucion > :inicio")
    boolean existeSolapamiento(@Param("idVehiculo") Long idVehiculo,
                               @Param("idAlquiler") Long idAlquiler,
                               @Param("inicio") LocalDate inicio,
                               @Param("fin") LocalDate fin);
}
