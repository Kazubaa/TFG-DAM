package com.tfg.concesionario.service

import com.tfg.concesionario.dto.VentaDTO
import com.tfg.concesionario.model.Venta
import com.tfg.concesionario.repository.ClienteRepository
import com.tfg.concesionario.repository.MotoRepository
import com.tfg.concesionario.repository.MotoSegundaManoRepository
import com.tfg.concesionario.repository.VendedorRepository
import com.tfg.concesionario.repository.VentaRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.Optional

@Service
class VentaService(
    private val ventaRepository: VentaRepository,
    private val clienteRepository: ClienteRepository,
    private val vendedorRepository: VendedorRepository,
    private val motoRepository: MotoRepository,
    private val motoSegundaRepository: MotoSegundaManoRepository
) {

    fun getAll(): List<Venta> = ventaRepository.findAll()

    fun get(id: Long): Optional<Venta> = ventaRepository.findById(id)

    fun save(dto: VentaDTO): Venta {
        val cliente = clienteRepository.findById(dto.clienteId)
            .orElseThrow { RuntimeException("Cliente no encontrado") }
        val vendedor = vendedorRepository.findById(dto.vendedorId)
            .orElseThrow { RuntimeException("Vendedor no encontrado") }

        val moto = dto.motoId?.let {
            motoRepository.findById(it).orElseThrow { RuntimeException("Moto no encontrada") }
        }
        val motoSegunda = dto.motoSegundaManoId?.let {
            val m = motoSegundaRepository.findById(it)
                .orElseThrow { RuntimeException("Moto segunda mano no encontrada") }
            if (!m.disponible) throw RuntimeException("La moto no está disponible")
            m
        }

        if (moto == null && motoSegunda == null) throw RuntimeException("Debe asignar una moto o moto de segunda mano")

        motoSegunda?.let { motoSegundaRepository.save(it.copy(disponible = false)) }

        return ventaRepository.save(
            Venta(
                cliente = cliente,
                vendedor = vendedor,
                moto = moto,
                motoSegundaMano = motoSegunda,
                fecha = dto.fecha?.let { LocalDate.parse(it) } ?: LocalDate.now(),
                estado = dto.estado ?: "PENDIENTE"
            )
        )
    }

    fun update(id: Long, dto: VentaDTO): Venta {
        val existing = ventaRepository.findById(id).orElseThrow { RuntimeException("Venta no encontrada") }
        val cliente = clienteRepository.findById(dto.clienteId)
            .orElseThrow { RuntimeException("Cliente no encontrado") }
        val vendedor = vendedorRepository.findById(dto.vendedorId)
            .orElseThrow { RuntimeException("Vendedor no encontrado") }

        val moto = dto.motoId?.let {
            motoRepository.findById(it).orElseThrow { RuntimeException("Moto no encontrada") }
        }
        val motoSegunda = dto.motoSegundaManoId?.let {
            motoSegundaRepository.findById(it).orElseThrow { RuntimeException("Moto segunda mano no encontrada") }
        }

        if (moto == null && motoSegunda == null) throw RuntimeException("Debe asignar una moto o moto de segunda mano")

        if (dto.estado == "CANCELADA") {
            existing.motoSegundaMano?.let { motoSegundaRepository.save(it.copy(disponible = true)) }
        }

        return ventaRepository.save(
            existing.copy(
                cliente = cliente,
                vendedor = vendedor,
                moto = moto,
                motoSegundaMano = motoSegunda,
                fecha = dto.fecha?.let { LocalDate.parse(it) } ?: existing.fecha,
                estado = dto.estado ?: existing.estado
            )
        )
    }

    fun delete(id: Long) = ventaRepository.deleteById(id)
}