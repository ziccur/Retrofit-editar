package iticbcn.m7.provavalobike

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Bici(
    var id: Int,
    var matricula: String,
    var puntuacio: Int,
    var comentario: String,
    var data: String,
    var idUsuari: Int
)

class BiciAdapter(
    private val context: Context,
    private val listaBicis: MutableList<Bici>,
    private val listener: OnBiciUpdatedListener // Listener para notificar actualizaciones
) : RecyclerView.Adapter<BiciAdapter.BiciViewHolder>() {

    interface OnBiciUpdatedListener {
        fun onBiciUpdated()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiciViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_bici, parent, false)
        return BiciViewHolder(view)
    }

    override fun onBindViewHolder(holder: BiciViewHolder, position: Int) {
        val bici = listaBicis[position]

        holder.tvMatricula.text = "Matrícula: ${bici.matricula}"
        holder.tvDataValoracio.text = "Data: ${bici.data}"
        holder.rbValoracio.rating = bici.puntuacio.toFloat()

        holder.itemView.setOnClickListener {
            mostrarDetalleBici(bici)
        }

        holder.itemView.setOnLongClickListener {
            mostrarDialogoEliminar(bici.id, position)
            true
        }
    }

    override fun getItemCount(): Int = listaBicis.size

    private fun mostrarDetalleBici(bici: Bici) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_detalle_bici, null)

        val tvMatriculaDetalle = dialogView.findViewById<TextView>(R.id.tvMatriculaDetalle)
        val tvFechaDetalle = dialogView.findViewById<TextView>(R.id.tvFechaDetalle)
        val rbValoracionDetalle = dialogView.findViewById<RatingBar>(R.id.rbValoracionDetalle)
        val tvComentarioDetalle = dialogView.findViewById<TextView>(R.id.tvComentarioDetalle)
        val btnCerrar = dialogView.findViewById<Button>(R.id.btnCerrar)
        val btnEditar = dialogView.findViewById<Button>(R.id.btnEditar)

        tvMatriculaDetalle.text = "Matrícula: ${bici.matricula}"
        tvFechaDetalle.text = "Data: ${bici.data}"
        rbValoracionDetalle.rating = bici.puntuacio.toFloat()
        tvComentarioDetalle.text = "Comentario: ${bici.comentario}"

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()

        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        btnEditar.setOnClickListener {
            editarBici(bici)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun editarBici(bici: Bici) {
        val editDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_editar_bici, null)
        val ratingBar = editDialogView.findViewById<RatingBar>(R.id.ratingBar)
        val editTextComentario = editDialogView.findViewById<EditText>(R.id.editTextComentario)

        ratingBar.rating = bici.puntuacio.toFloat()
        editTextComentario.setText(bici.comentario)

        val editDialogBuilder = AlertDialog.Builder(context)
        editDialogBuilder.setView(editDialogView)
        editDialogBuilder.setTitle("Editar Bici")
        editDialogBuilder.setPositiveButton("Guardar") { dialog, _ ->
            val nuevaPuntuacion = ratingBar.rating.toInt()
            val nuevoComentario = editTextComentario.text.toString()

            bici.puntuacio = nuevaPuntuacion
            bici.comentario = nuevoComentario

            actualizarBiciEnApi(bici)
            dialog.dismiss()
        }
        editDialogBuilder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val editDialog = editDialogBuilder.create()
        editDialog.show()
    }

    private fun actualizarBiciEnApi(bici: Bici) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = UpdateBiciRequest(
                    matricula = bici.matricula,
                    puntuacio = bici.puntuacio,
                    comentario = bici.comentario,
                    data = bici.data,
                    idUsuari = bici.idUsuari
                )

                val response = RetrofitClient.instance.actualizarBici(bici.id, requestBody)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Bici actualizada en la API", Toast.LENGTH_SHORT).show()
                        listener.onBiciUpdated() // Notificar que la bici ha sido actualizada
                    } else {
                        Toast.makeText(context, "Error al actualizar la bici", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    data class UpdateBiciRequest(
        val matricula: String,
        val puntuacio: Int,
        val comentario: String,
        val data: String,
        val idUsuari: Int
    )

    private fun mostrarDialogoEliminar(idValoracio: Int, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Eliminar valoración")
        builder.setMessage("¿Estás seguro de que quieres eliminar esta valoración?")
        builder.setPositiveButton("Sí") { _, _ ->
            eliminarValoracion(idValoracio, position)
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }

    private fun eliminarValoracion(idValoracio: Int, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.eliminarValoracio(idValoracio)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        listaBicis.removeAt(position)
                        notifyItemRemoved(position)
                        Toast.makeText(context, "Valoración eliminada", Toast.LENGTH_SHORT).show()
                        listener.onBiciUpdated() // Notificar que la lista ha cambiado
                    } else {
                        Toast.makeText(context, "Error al eliminar la valoración", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    class BiciViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMatricula: TextView = itemView.findViewById(R.id.tvMatricula)
        val tvDataValoracio: TextView = itemView.findViewById(R.id.tvDataValoracio)
        val rbValoracio: RatingBar = itemView.findViewById(R.id.rbValoracio)
    }
}