package com.tfg.concesionario.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany


@Entity
data class MotoSegundaMano(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var disponible: Boolean = true,

    @Column(nullable = false)
    val marca: String,

    @Column(nullable = false)
    val modelo: String,

    @Column(nullable = false)
    val precio: Double,

    @Column(nullable = false)
    val cilindrada: Int,

    @Column(nullable = false)
    val km: Int,

    @Column(nullable = false)
    val cv: Int,

    @Column(nullable = false, unique = true)
    val matricula: String,


    @OneToMany(
        mappedBy = "motoSegundaMano",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY //Carga las imagenes cuando sea necesario
    )
    val imagenes: MutableList<Imagen> = mutableListOf(),


    @Column
    var imagenPrincipal: String? = null
)