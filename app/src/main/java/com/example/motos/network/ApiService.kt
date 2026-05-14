package com.example.motos.network

import com.example.motos.model.Cita
import com.example.motos.model.CitaRequest
import com.example.motos.model.Cliente
import com.example.motos.model.ClienteRequest
import com.example.motos.model.ForgotPasswordRequest
import com.example.motos.model.ImagenMoto
import com.example.motos.model.ImagenMotoNueva
import com.example.motos.model.LoginRequest
import com.example.motos.model.LoginResponse
import com.example.motos.model.Mecanico
import com.example.motos.model.MecanicoRequest
import com.example.motos.model.MecanicoSimple
import com.example.motos.model.MotoCliente
import com.example.motos.model.MotoClienteRequest
import com.example.motos.model.MotoNueva
import com.example.motos.model.MotoNuevaRequest
import com.example.motos.model.MotoSegundaMano
import com.example.motos.model.MotoSegundaManoRequest
import com.example.motos.model.RegisterRequest
import com.example.motos.model.Reparacion
import com.example.motos.model.ReparacionRequest
import com.example.motos.model.Reserva
import com.example.motos.model.ReservaRequest
import com.example.motos.model.Vendedor
import com.example.motos.model.VendedorRequest
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

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body req: ForgotPasswordRequest): Response<Map<String, String>>


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




    @GET("motoCliente/cliente/{clienteId}")
    suspend fun getMotosCliente(@Path("clienteId") clienteId: Long): Response<List<MotoCliente>>

    @POST("motoCliente")
    suspend fun crearMotoCliente(@Body request: MotoClienteRequest): Response<MotoCliente>

    @PUT("motoCliente/{matricula}")
    suspend fun actualizarMotoCliente(
        @Path("matricula") matricula: String,
        @Body request: MotoClienteRequest
    ): Response<MotoCliente>

    @DELETE("motoCliente/{matricula}")
    suspend fun eliminarMotoCliente(@Path("matricula") matricula: String): Response<Unit>

    // Citas
    @GET("citas/cliente/{clienteId}")
    suspend fun getCitasCliente(@Path("clienteId") clienteId: Long): Response<List<Cita>>

    @GET("citas")
    suspend fun getCitas(): Response<List<Cita>>

    @POST("citas")
    suspend fun crearCita(@Body request: CitaRequest): Response<Cita>

    @PUT("citas/{id}/estado")
    suspend fun actualizarEstadoCita(
        @Path("id") id: Long,
        @Query("estado") estado: String,
        @Query("mecanicoId") mecanicoId: Long? = null
    ): Response<Cita>





    @GET("reparaciones")
    suspend fun getReparaciones(): Response<List<Reparacion>>

    @GET("reparaciones/{id}")
    suspend fun getReparacion(@Path("id") id: Long): Response<Reparacion>

    @GET("reparaciones/moto/{matricula}")
    suspend fun getReparacionesByMoto(@Path("matricula") matricula: String): Response<List<Reparacion>>

    @GET("reparaciones/cliente/{clienteId}")
    suspend fun getReparacionesByCliente(@Path("clienteId") clienteId: Long): Response<List<Reparacion>>

    @POST("reparaciones")
    suspend fun crearReparacion(@Body request: ReparacionRequest): Response<Reparacion>

    @PUT("reparaciones/{id}")
    suspend fun actualizarReparacion(
        @Path("id") id: Long,
        @Body request: ReparacionRequest
    ): Response<Reparacion>

    @PUT("reparaciones/{id}/estado")
    suspend fun actualizarEstadoReparacion(
        @Path("id") id: Long,
        @Query("estado") estado: String
    ): Response<Reparacion>


    @GET("reservas/moto/{motoId}")
    suspend fun getReservaActivaByMoto(@Path("motoId") motoId: Long): Response<Reserva?>



    @GET("citas/disponibilidad")
    suspend fun getMecanicosDisponibles(
        @Query("fecha") fecha: String,
        @Query("hora") hora: String
    ): Response<List<Long>>

    @PUT("citas/{id}")
    suspend fun actualizarCita(
        @Path("id") id: Long,
        @Body request: CitaRequest
    ): Response<Cita>

    @DELETE("citas/{id}")
    suspend fun eliminarCita(@Path("id") id: Long): Response<Unit>

    @GET("mecanicos")
    suspend fun getMecanicos(): Response<List<MecanicoSimple>>

    @GET("reparaciones/mecanico/{mecanicoId}")
    suspend fun getReparacionesByMecanico(@Path("mecanicoId") mecanicoId: Long): Response<List<Reparacion>>

    @GET("mecanicos/{id}")
    suspend fun getMecanico(@Path("id") id: Long): Response<Mecanico>

    @PUT("mecanicos/{id}")
    suspend fun actualizarMecanico(
        @Path("id") id: Long,
        @Body request: MecanicoRequest
    ): Response<Mecanico>

    @GET("vendedores/{id}")
    suspend fun getVendedor(@Path("id") id: Long): Response<Vendedor>

    @PUT("vendedores/{id}")
    suspend fun actualizarVendedor(
        @Path("id") id: Long,
        @Body request: VendedorRequest
    ): Response<Vendedor>



    //Motos nuevas


    @GET("motosNuevas")
    suspend fun getMotosNuevas(): Response<List<MotoNueva>>

    @GET("motosNuevas/{id}")
    suspend fun getMotoNueva(@Path("id") id: Long): Response<MotoNueva>

    @GET("motosNuevas/marca/{marca}")
    suspend fun getMotosNuevasByMarca(@Path("marca") marca: String): Response<List<MotoNueva>>

    @GET("motosNuevas/marca/{marca}/categoria/{categoria}")
    suspend fun getMotosNuevasByMarcaCategoria(
        @Path("marca") marca: String,
        @Path("categoria") categoria: String
    ): Response<List<MotoNueva>>

    @POST("motosNuevas")
    suspend fun crearMotoNueva(@Body request: MotoNuevaRequest): Response<MotoNueva>

    @PUT("motosNuevas/{id}")
    suspend fun actualizarMotoNueva(@Path("id") id: Long, @Body request: MotoNuevaRequest): Response<MotoNueva>

    @DELETE("motosNuevas/{id}")
    suspend fun eliminarMotoNueva(@Path("id") id: Long): Response<Unit>

    @GET("imagenesMotoNueva/moto/{motoId}")
    suspend fun getImagenesMotoNueva(@Path("motoId") motoId: Long): Response<List<ImagenMotoNueva>>

    @GET("imagenesMotoNueva/moto/{motoId}/tipo/{tipo}")
    suspend fun getImagenesMotoNuevaPorTipo(
        @Path("motoId") motoId: Long,
        @Path("tipo") tipo: String
    ): Response<List<ImagenMotoNueva>>

    @Multipart
    @POST("imagenesMotoNueva/upload")
    suspend fun subirImagenMotoNueva(
        @Part("motoId") motoId: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("tipo") tipo: RequestBody
    ): Response<ImagenMotoNueva>

    @DELETE("imagenesMotoNueva/{id}")
    suspend fun eliminarImagenMotoNueva(@Path("id") id: Long): Response<Unit>


    //Video

    @Multipart
    @POST("videosMotoNueva/upload")
    suspend fun subirVideoMotoNueva(
        @Part("motoId") motoId: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Map<String, String>>

    @DELETE("videosMotoNueva/{motoId}")
    suspend fun eliminarVideoMotoNueva(@Path("motoId") motoId: Long): Response<Unit>
}