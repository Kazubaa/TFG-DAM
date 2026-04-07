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
data class Venta(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val fecha: LocalDate = LocalDate.now(),

    @ManyToOne
    @JoinColumn(name = "moto_id")
    val moto: Moto? = null,  // Nullable: puede ser una moto nueva

    @ManyToOne
    @JoinColumn(name = "moto_segunda_mano_id")
    val motoSegundaMano: MotoSegundaMano? = null, // Nullable: puede ser una moto segunda mano

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    val cliente: Cliente,

    @ManyToOne
    @JoinColumn(name = "vendedor_id", nullable = false)
    val vendedor: Vendedor,

    @Column(nullable = false)
    var estado: String = "PENDIENTE"
)