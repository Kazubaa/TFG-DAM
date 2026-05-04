package com.tfg.concesionario.repository

import com.tfg.concesionario.model.MotoNueva
import org.springframework.data.jpa.repository.JpaRepository

interface MotoNuevaRepository : JpaRepository<MotoNueva, Long> {
    fun findByMarca(marca: String): List<MotoNueva>
    fun findByMarcaAndCategoria(marca: String, categoria: String): List<MotoNueva>
}