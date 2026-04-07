package com.tfg.concesionario.dto

data class VentaDTO(
    val clienteId: Long,
    val vendedorId: Long,
    val motoId: Long? = null,
    val motoSegundaManoId: Long? = null,
    val fecha: String? = null,
    val estado: String? = null
)