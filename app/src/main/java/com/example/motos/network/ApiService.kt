package com.example.motos.network

import com.example.motos.model.Cliente
import com.example.motos.model.ClienteRequest
import com.example.motos.model.ImagenMoto
import com.example.motos.model.LoginRequest
import com.example.motos.model.LoginResponse
import com.example.motos.model.MotoSegundaMano
import com.example.motos.model.MotoSegundaManoRequest
import com.example.motos.model.RegisterRequest
import com.example.motos.model.Reserva
import com.example.motos.model.ReservaRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>


    @GET("imagenes/promociones")
    suspend fun getPromocionesMotos(): Response<List<String>>

    @GET("imagenes/promomecanico")
    suspend fun getPromocionesMecanico(): Response<List<String>>





    @GET("motosSegundaMano")
    suspend fun getMotosSegundaMano(): Response<List<MotoSegundaMano>>

    @GET("motosSegundaMano/filtrar")
    suspend fun filtrarMotosSegundaMano(
        @Query("marca") marca: String? = null,
        @Query("modelo") modelo: String? = null,
        @Query("cvMax") cvMax: Int? = null,
        @Query("km") km: Int? = null,
        @Query("cilindradaMax") cilindradaMax: Int? = null,
        @Query("precioMax") precioMax: Double? = null,
        @Query("matricula") matricula: String? = null
    ): Response<List<MotoSegundaMano>>

    @POST("reservas")
    suspend fun crearReserva(@Body request: ReservaRequest): Response<Reserva>

    @GET("reservas")
    suspend fun getReservas(): Response<List<Reserva>>

    @PUT("reservas/{id}")
    suspend fun actualizarReserva(
        @Path("id") id: Long,
        @Body request: ReservaRequest
    ): Response<Reserva>

    // Imagenes
    @Multipart
    @POST("imagenes/upload")
    suspend fun subirImagen(
        @Part("motoId") motoId: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<ImagenMoto>

    @DELETE("imagenes/{id}")
    suspend fun eliminarImagen(@Path("id") id: Long): Response<Unit>

    @GET("imagenes/moto/{motoId}")
    suspend fun getImagenesMoto(@Path("motoId") motoId: Long): Response<List<ImagenMoto>>

    // Motos Segunda Mano
    @PUT("motosSegundaMano/{id}")
    suspend fun actualizarMoto(
        @Path("id") id: Long,
        @Body request: MotoSegundaManoRequest
    ): Response<MotoSegundaMano>

    @DELETE("motosSegundaMano/{id}")
    suspend fun eliminarMoto(@Path("id") id: Long): Response<Unit>

    @POST("motosSegundaMano")
    suspend fun crearMoto(@Body request: MotoSegundaManoRequest): Response<MotoSegundaMano>


    @GET("reservas/cliente/{clienteId}")
    suspend fun getReservasByCliente(@Path("clienteId") clienteId: Long): Response<List<Reserva>>

    @PUT("reservas/{id}/estado")
    suspend fun actualizarEstadoReserva(
        @Path("id") id: Long,
        @Query("estado") estado: String
    ): Response<Reserva>


    @GET("clientes/{id}")
    suspend fun getCliente(@Path("id") id: Long): Response<Cliente>

    @PUT("clientes/{id}")
    suspend fun actualizarCliente(
        @Path("id") id: Long,
        @Body request: ClienteRequest
    ): Response<Cliente>
}