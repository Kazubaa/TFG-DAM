package com.tfg.concesionario.repository

import com.tfg.concesionario.model.ImagenMotoNueva
import org.springframework.data.jpa.repository.JpaRepository

interface ImagenMotoNuevaRepository : JpaRepository<ImagenMotoNueva, Long> {
    fun findByMotoNuevaIdAndTipoOrderByOrdenAsc(motoNuevaId: Long, tipo: String): List<ImagenMotoNueva>
    fun findByMotoNuevaIdOrderByOrdenAsc(motoNuevaId: Long): List<ImagenMotoNueva>
}