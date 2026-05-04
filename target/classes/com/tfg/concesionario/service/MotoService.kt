package com.tfg.concesionario.service

import com.tfg.concesionario.model.Moto
import com.tfg.concesionario.model.TipoCarnet
import com.tfg.concesionario.repository.MotoRepository
import org.springframework.stereotype.Service
import java.util.Optional


@Service
class MotoService(private val repo: MotoRepository) {

    fun getAllMotos(): List<Moto> = repo.findAll()
    fun getMoto(id: Long): Optional<Moto> = repo.findById(id)
    fun saveMoto(moto: Moto): Moto = repo.save(moto)
    fun deleteMoto(id: Long) = repo.deleteById(id)

    fun filtrar(marca: String?, carnet: TipoCarnet?, cvMax: Int?) = repo.filtrar(marca, carnet, cvMax)
    fun getMarcas() = repo.findDistinctMarcas()
    fun getCarnetsByMarca(marca: String) = repo.findCarnetsByMarca(marca)
    fun findByCarnet(carnet: TipoCarnet) = repo.findByCarnet(carnet)
    fun findByMarcaAndCarnet(marca: String, carnet: TipoCarnet) = repo.findByMarcaAndCarnet(marca, carnet)
}