package com.tfg.concesionario.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
data class Reparacion(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val descripcion: String,

    @Column(nullable = false)
    val fecha: LocalDate = LocalDate.now(),

    @ManyToOne
    @JoinColumn(name = "cita_id", nullable = false)
    val cita: Cita,

    @ManyToOne
    @JoinColumn(name = "mecanico_id", nullable = false)
    val mecanico: Mecanico,

    @Column(nullable = false)
    var estado: String = "PENDIENTE"  // PENDIENTE, EN_PROCESO, TERMINADA
)