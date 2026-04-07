package com.tfg.concesionario.repository

import com.tfg.concesionario.model.Moto
import com.tfg.concesionario.model.TipoCarnet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MotoRepository : JpaRepository<Moto, Long> {

    fun findByMarca(marca: String): List<Moto>

    fun findByMarcaAndCarnet(marca: String, carnet: TipoCarnet): List<Moto>

    fun findByCarnet(carnet: TipoCarnet): List<Moto>

    fun findByCvLessThanEqual(cv: Int): List<Moto>

    fun findByCvBetween(min: Int, max: Int): List<Moto>

    fun findByCvGreaterThan(cv: Int): List<Moto>

    @Query("SELECT DISTINCT m.marca FROM Moto m")
    fun findDistinctMarcas(): List<String>

    @Query("SELECT DISTINCT m.carnet FROM Moto m WHERE m.marca = :marca")
    fun findCarnetsByMarca(@Param("marca") marca: String): List<TipoCarnet>

    @Query("""
        SELECT m FROM Moto m
        WHERE (:marca IS NULL OR m.marca = :marca)
          AND (:carnet IS NULL OR m.carnet = :carnet)
          AND (:cvMax IS NULL OR m.cv <= :cvMax)
    """)
    fun filtrar(
        @Param("marca") marca: String?,
        @Param("carnet") carnet: TipoCarnet?,
        @Param("cvMax") cvMax: Int?
    ): List<Moto>
}