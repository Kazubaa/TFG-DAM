package com.tfg.concesionario.controller

import com.tfg.concesionario.model.Moto
import com.tfg.concesionario.model.TipoCarnet
import com.tfg.concesionario.service.MotoService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/motos")
class MotoController(private val service: MotoService) {

    @GetMapping
    fun filtrar(
        @RequestParam(required = false) marca: String?,
        @RequestParam(required = false) carnet: TipoCarnet?,
        @RequestParam(required = false) cvMax: Int?
    ): List<Moto> = service.filtrar(marca, carnet, cvMax)

    @GetMapping("/marcas")
    fun getMarcas() = service.getMarcas()

    @GetMapping("/marca/{marca}/carnets")
    fun getCarnets(@PathVariable marca: String) = service.getCarnetsByMarca(marca)

    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long) = service.getMoto(id)

    @PostMapping
    fun create(@RequestBody moto: Moto) = service.saveMoto(moto)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody moto: Moto): Moto {
        val existing = service.getMoto(id).orElseThrow { RuntimeException("Moto no encontrada") }
        return service.saveMoto(
            existing.copy(
                marca = moto.marca,
                modelo = moto.modelo,
                precio = moto.precio,
                cilindrada = moto.cilindrada,
                cv = moto.cv,
                carnet = moto.carnet,
                imagenUrl = moto.imagenUrl
            )
        )
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.deleteMoto(id)
}