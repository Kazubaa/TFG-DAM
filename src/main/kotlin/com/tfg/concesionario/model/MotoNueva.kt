package com.tfg.concesionario.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "moto_nueva")
data class MotoNueva(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val marca: String = "",

    @Column(nullable = false)
    val modelo: String = "",

    @Column(nullable = false)
    val categoria: String = "",

    @Column(nullable = false)
    val anio: Int = 0,

    @Column(nullable = false)
    val precio: Double = 0.0,

    @Column(nullable = false)
    val cilindrada: Int = 0,

    @Column(nullable = false)
    val cv: Int = 0,

    @Column(nullable = false)
    val peso: Int = 0,

    @Column(columnDefinition = "TEXT")
    val descripcion: String = "",

    @Column(length = 500)
    val videoFile: String? = null
)