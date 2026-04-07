package com.tfg.concesionario.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Cita(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val fecha: LocalDate,

    @Column(nullable = false)
    val hora: LocalTime,

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    val cliente: Cliente,

    @ManyToOne
    @JoinColumn(name = "mecanico_id")
    var mecanico: Mecanico? = null,

    @Column(nullable = false)
    var estado: String = "PENDIENTE"  // PENDIENTE, CONFIRMADA, CANCELADA
)