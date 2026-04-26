package com.tfg.concesionario.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "moto_cliente")
data class MotoCliente(
    @Id
    val matricula: String = "",

    @Column(nullable = false)
    val marca: String = "",

    @Column(nullable = false)
    val modelo: String = "",

    @Column(nullable = false)
    val km: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnore
    val cliente: Cliente? = null
)