package com.api.alquilaya.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.alquilaya.models.OficinaAlquilerModel;

@Repository
public interface IOficinaAlquilerRepository extends JpaRepository<OficinaAlquilerModel, Long> {

    // Listar las oficinas de una ciudad (sin importar mayúsculas o minúsculas)
    List<OficinaAlquilerModel> findByCiudadIgnoreCase(String ciudad);
}
