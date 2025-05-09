package iticbcn.m7.provavalobike

import iticbcn.m7.provavalobike.BiciAdapter.UpdateBiciRequest
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("/valoracions")
    suspend fun getBicisValorades(): List<Bici>

    @POST("/valoracions")
    suspend fun enviarValoracio(@Body valoracio: ValoracioRequest): Response<Unit>

    @DELETE("/valoracions/{id}")
    suspend fun eliminarValoracio(@Path("id") id: Int): Response<Unit>

    @PUT("valoracions/{id}")
    suspend fun actualizarBici(
        @Path("id") id: Int,
        @Body requestBody: UpdateBiciRequest
    ): Response<Unit>
}