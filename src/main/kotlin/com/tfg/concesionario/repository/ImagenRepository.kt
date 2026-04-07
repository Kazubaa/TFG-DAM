package com.tfg.concesionario.repository

import com.tfg.concesionario.model.Imagen
import org.springframework.data.jpa.repository.JpaRepository

interface ImagenRepository : JpaRepository<Imagen, Long> {

    fun findByMotoSegundaManoId(motoId: Long): List<Imagen>
}