package com.tfg.concesionario.repository

import com.tfg.concesionario.model.Venta
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface VentaRepository : JpaRepository<Venta, Long> {
    fun findByClienteId(clienteId: Long): List<Venta>
    fun findByVendedorId(vendedorId: Long): List<Venta>
    fun findByFecha(fecha: LocalDate): List<Venta>
}