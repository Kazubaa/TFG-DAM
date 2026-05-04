package com.tfg.concesionario.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class Imagen(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val url: String = "",

    @ManyToOne
    @JoinColumn(name = "moto_segunda_id")
    @JsonIgnore
    val motoSegundaMano: MotoSegundaMano? = null
)