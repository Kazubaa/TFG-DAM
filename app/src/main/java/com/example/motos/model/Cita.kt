package com.example.motos.model

data class Cita(
    val id: Long,
    val cliente: ClienteSimple,
    val mecanico: MecanicoSimple?,
    val motoCliente: MotoCliente?,
    val fecha: String,
    val hora: String,
    val tipo: String,
    val descripcion: String? = null,
    val estado: String
)

data class MecanicoSimple(
    val id: Long,
    val nombre: String,
    val email: String,
    val telefono: String
)

data class CitaRequest(
    val clienteId: Long,
    val mecanicoId: Long? = null,
    val motoMatricula: String? = null,
    val fecha: String,
    val hora: String,
    val tipo: String,
    val descripcion: String? = null,
    val estado: String = "PENDIENTE"
)