package iticbcn.m7.provavalobike

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ValoracioRequest(
    val matricula: String,
    val puntuacio: Int,
    val comentario: String,
    val data: String,
    val idUsuari: Int
) {
    companion object {
        fun create(matricula: String, puntuacion: Int, comentario: String, idUsuario: Int): ValoracioRequest {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechaActual = dateFormat.format(Date())
            return ValoracioRequest(
                matricula = matricula,
                puntuacio = puntuacion,
                comentario = comentario,
                data = fechaActual,
                idUsuari = idUsuario
            )
        }
    }
}