package com.tfg.concesionario.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id


@Entity
data class Moto(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val marca: String,

    @Column(nullable = false)
    val modelo: String,

    @Column(nullable = false)
    val precio: Double,

    @Column(nullable = false)
    val cilindrada: Int,

    @Column(nullable = false)
    val cv: Int,

    @Column(length = 255)
    val imagenUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val carnet: TipoCarnet
)