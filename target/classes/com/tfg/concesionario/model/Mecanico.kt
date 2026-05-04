package com.tfg.concesionario.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Mecanico(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val nombre: String,
    @Column(nullable = false)
    val email: String,
    @Column(nullable = false)
    val telefono: String,
    @Column(nullable = false)
    val disponible: Boolean = true
)