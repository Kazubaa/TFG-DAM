package com.tfg.concesionario.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import java.time.LocalDate

@Entity
data class Reparacion(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "cita_id", nullable = false)
    val cita: Cita,

    @ManyToOne
    @JoinColumn(name = "mecanico_id", nullable = false)
    val mecanico: Mecanico,

    @Column(columnDefinition = "TEXT")
    val descripcion: String = "",

    @Column(nullable = false)
    val fecha: LocalDate = LocalDate.now(),

    @OneToMany(mappedBy = "reparacion", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    val items: MutableList<ItemReparacion> = mutableListOf(),

    @Column(nullable = false)
    var subtotal: Double = 0.0,

    @Column(nullable = false)
    var iva: Double = 0.0,

    @Column(nullable = false)
    var total: Double = 0.0,

    @Column(nullable = false)
    var estado: String = "BORRADOR"  // BORRADOR, ENVIADO, APROBADO, RECHAZADO, COMPLETADO
)