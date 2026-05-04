package com.tfg.concesionario.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "item_reparacion")
data class ItemReparacion(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val descripcion: String = "",

    @Column(nullable = false)
    val tipo: String = "",  // MANO_OBRA, PIEZA, TAREA

    @Column(nullable = false)
    val cantidad: Double = 0.0,  // horas para mano de obra, unidades para piezas

    @Column(nullable = false)
    val precioUnitario: Double = 0.0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reparacion_id")
    @JsonIgnore
    val reparacion: Reparacion? = null
)