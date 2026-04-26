package com.example.motos.repository

import com.example.motos.model.Cita
import com.example.motos.model.CitaRequest
import com.example.motos.model.MecanicoSimple
import com.example.motos.model.MotoCliente
import com.example.motos.model.MotoClienteRequest
import com.example.motos.model.Reparacion
import com.example.motos.model.ReparacionRequest
import com.example.motos.network.ApiService

class TallerRepository(private val api: ApiService) {

    // Motos Cliente
    suspend fun getMotosCliente(clienteId: Long): List<MotoCliente> {
        val r = api.getMotosCliente(clienteId)
        return if (r.isSuccessful) r.body() ?: emptyList() else emptyList()
    }

    suspend fun crearMotoCliente(request: MotoClienteRequest): MotoCliente? {
        val r = api.crearMotoCliente(request)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun actualizarMotoCliente(matricula: String, request: MotoClienteRequest): MotoCliente? {
        val r = api.actualizarMotoCliente(matricula, request)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun eliminarMotoCliente(matricula: String): Boolean {
        return api.eliminarMotoCliente(matricula).isSuccessful
    }

    // Citas
    suspend fun getCitas(): List<Cita> {
        val r = api.getCitas()
        return if (r.isSuccessful) r.body() ?: emptyList() else emptyList()
    }

    suspend fun getCitasCliente(clienteId: Long): List<Cita> {
        val r = api.getCitasCliente(clienteId)
        return if (r.isSuccessful) r.body() ?: emptyList() else emptyList()
    }

    suspend fun crearCita(request: CitaRequest): Cita? {
        val r = api.crearCita(request)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun actualizarEstadoCita(id: Long, estado: String, mecanicoId: Long? = null): Cita? {
        val r = api.actualizarEstadoCita(id, estado, mecanicoId)
        return if (r.isSuccessful) r.body() else null
    }






    // Reparaciones
    suspend fun getMecanicos(): List<MecanicoSimple> {
        val r = api.getMecanicos()
        return if (r.isSuccessful) r.body() ?: emptyList() else emptyList()
    }
    suspend fun getReparaciones(): List<Reparacion> {
        val r = api.getReparaciones()
        return if (r.isSuccessful) r.body() ?: emptyList() else emptyList()
    }

    suspend fun getReparacion(id: Long): Reparacion? {
        val r = api.getReparacion(id)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun getReparacionesByMoto(matricula: String): List<Reparacion> {
        val r = api.getReparacionesByMoto(matricula)
        return if (r.isSuccessful) r.body() ?: emptyList() else emptyList()
    }

    suspend fun getReparacionesByCliente(clienteId: Long): List<Reparacion> {
        val r = api.getReparacionesByCliente(clienteId)
        return if (r.isSuccessful) r.body() ?: emptyList() else emptyList()
    }

    suspend fun getReparacionesByMecanico(mecanicoId: Long): List<Reparacion> {
        val r = api.getReparacionesByMecanico(mecanicoId)
        return if (r.isSuccessful) r.body() ?: emptyList() else emptyList()
    }

    suspend fun crearReparacion(request: ReparacionRequest): Reparacion? {
        val r = api.crearReparacion(request)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun actualizarReparacion(id: Long, request: ReparacionRequest): Reparacion? {
        val r = api.actualizarReparacion(id, request)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun actualizarEstadoReparacion(id: Long, estado: String): Reparacion? {
        val r = api.actualizarEstadoReparacion(id, estado)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun getMecanicosDisponibles(fecha: String, hora: String): List<Long> {
        val r = api.getMecanicosDisponibles(fecha, hora)
        return if (r.isSuccessful) r.body() ?: emptyList() else emptyList()
    }

    suspend fun actualizarCita(id: Long, request: CitaRequest): Cita? {
        val r = api.actualizarCita(id, request)
        return if (r.isSuccessful) r.body() else null
    }

    suspend fun eliminarCita(id: Long): Boolean = api.eliminarCita(id).isSuccessful
}