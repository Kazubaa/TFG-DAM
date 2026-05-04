package com.tfg.concesionario.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "imagen_moto_nueva")
data class ImagenMotoNueva(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val motoNuevaId: Long = 0,

    @Column(nullable = false)
    val url: String = "",

    @Column(nullable = false)
    val tipo: String = "GALERIA",  // GALERIA o R360

    @Column(nullable = false)
    val orden: Int = 0
)