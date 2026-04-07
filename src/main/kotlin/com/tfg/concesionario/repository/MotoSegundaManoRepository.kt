package com.tfg.concesionario.repository

import com.tfg.concesionario.model.MotoSegundaMano
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MotoSegundaManoRepository : JpaRepository<MotoSegundaMano, Long> {

    fun findByMarca(marca: String): List<MotoSegundaMano>
    fun findByMarcaAndModelo(marca: String, modelo: String): List<MotoSegundaMano>
    fun findByCvLessThanEqual(cv: Int): List<MotoSegundaMano>
    fun findByCilindradaLessThanEqual(cilindrada: Int): List<MotoSegundaMano>
    fun findByPrecioLessThanEqual(precio: Double): List<MotoSegundaMano>
    fun findByMatricula(matricula: String): List<MotoSegundaMano>

    @Query("SELECT DISTINCT m.marca FROM MotoSegundaMano m")
    fun findDistinctMarcas(): List<String>

    @Query("""
        SELECT m FROM MotoSegundaMano m
        WHERE (:marca IS NULL OR m.marca = :marca)
          AND (:modelo IS NULL OR m.modelo LIKE %:modelo%)
          AND (:cvMax IS NULL OR m.cv <= :cvMax)
          AND (:cilindradaMax IS NULL OR m.cilindrada <= :cilindradaMax)
          AND (:precioMax IS NULL OR m.precio <= :precioMax)
          AND (:matricula IS NULL OR m.matricula = :matricula)
    """)
    fun filtrar(
        @Param("marca") marca: String?,
        @Param("modelo") modelo: String?,
        @Param("cvMax") cvMax: Int?,
        @Param("cilindradaMax") cilindradaMax: Int?,
        @Param("precioMax") precioMax: Double?,
        @Param("matricula") matricula: String?
    ): List<MotoSegundaMano>
}