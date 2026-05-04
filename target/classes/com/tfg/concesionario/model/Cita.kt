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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    val cliente: Cliente,

    @ManyToOne
    @JoinColumn(name = "mecanico_id")
    val mecanico: Mecanico? = null,

    @ManyToOne
    @JoinColumn(name = "moto_matricula")
    val motoCliente: MotoCliente? = null,

    @Column(nullable = false)
    val fecha: LocalDate,

    @Column(nullable = false)
    val hora: LocalTime,

    @Column(nullable = false)
    val tipo: String = "REVISION",  // REVISION / MANTENIMIENTO

    @Column(columnDefinition = "TEXT")
    val descripcion: String? = null,

    @Column(nullable = false)
    var estado: String = "PENDIENTE"  // PENDIENTE, ACEPTADA, RECHAZADA, COMPLETADA
)