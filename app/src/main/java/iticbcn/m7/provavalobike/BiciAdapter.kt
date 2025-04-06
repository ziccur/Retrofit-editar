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
    var comentari: String,
    var data: String,
    var idUsuari: Int
)

class BiciAdapter(
    private val context: Context,
    private val llistaBicis: MutableList<Bici>,
    private val listener: OnBiciActualitzadaListener
) : RecyclerView.Adapter<BiciAdapter.BiciViewHolder>() {

    interface OnBiciActualitzadaListener {
        fun onBiciActualitzada()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiciViewHolder {
        val vista = LayoutInflater.from(context).inflate(R.layout.item_bici, parent, false)
        return BiciViewHolder(vista)
    }

    override fun onBindViewHolder(holder: BiciViewHolder, posicio: Int) {
        val bici = llistaBicis[posicio]
        holder.tvMatricula.text = "Matrícula: ${bici.matricula}"
        holder.tvDataValoracio.text = "Data: ${bici.data}"
        holder.rbValoracio.rating = bici.puntuacio.toFloat()

        holder.itemView.setOnClickListener { mostrarDetallBici(bici) }
        holder.itemView.setOnLongClickListener {
            mostrarDialegEliminar(bici.id, posicio)
            true
        }
    }

    override fun getItemCount(): Int = llistaBicis.size

    fun actualitzarLlista(novaLlista: List<Bici>) {
        llistaBicis.clear()
        llistaBicis.addAll(novaLlista)
        notifyDataSetChanged()
    }

    private fun mostrarDetallBici(bici: Bici) {
        val dialegView = LayoutInflater.from(context).inflate(R.layout.dialog_detalle_bici, null)
        val tvMatriculaDetall = dialegView.findViewById<TextView>(R.id.tvMatriculaDetalle)
        val tvDataDetall = dialegView.findViewById<TextView>(R.id.tvFechaDetalle)
        val rbValoracioDetall = dialegView.findViewById<RatingBar>(R.id.rbValoracionDetalle)
        val tvComentariDetall = dialegView.findViewById<TextView>(R.id.tvComentarioDetalle)
        val btnTancar = dialegView.findViewById<Button>(R.id.btnCerrar)
        val btnEditar = dialegView.findViewById<Button>(R.id.btnEditar)

        tvMatriculaDetall.text = "Matrícula: ${bici.matricula}"
        tvDataDetall.text = "Data: ${bici.data}"
        rbValoracioDetall.rating = bici.puntuacio.toFloat()
        tvComentariDetall.text = "Comentari: ${bici.comentari}"

        val dialeg = AlertDialog.Builder(context).setView(dialegView).create()
        btnTancar.setOnClickListener { dialeg.dismiss() }
        btnEditar.setOnClickListener {
            editarBici(bici)
            dialeg.dismiss()
        }
        dialeg.show()
    }

    private fun editarBici(bici: Bici) {
        val dialegEditarView = LayoutInflater.from(context).inflate(R.layout.dialog_editar_bici, null)
        val ratingBar = dialegEditarView.findViewById<RatingBar>(R.id.ratingBar)
        val etComentari = dialegEditarView.findViewById<EditText>(R.id.editTextComentario)

        ratingBar.rating = bici.puntuacio.toFloat()
        etComentari.setText(bici.comentari)

        AlertDialog.Builder(context)
            .setView(dialegEditarView)
            .setTitle("Editar Bici")
            .setPositiveButton("Desar") { _, _ ->
                bici.puntuacio = ratingBar.rating.toInt()
                bici.comentari = etComentari.text.toString()
                actualitzarBiciApi(bici)
            }
            .setNegativeButton("Cancel·lar") { dialeg, _ -> dialeg.dismiss() }
            .show()
    }

    private fun actualitzarBiciApi(bici: Bici) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resposta = RetrofitClient.instance.actualizarBici(
                    bici.id,
                    UpdateBiciRequest(
                        bici.matricula,
                        bici.puntuacio,
                        bici.comentari,
                        bici.data,
                        bici.idUsuari
                    )
                )
                withContext(Dispatchers.Main) {
                    if (resposta.isSuccessful) {
                        listener.onBiciActualitzada()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun mostrarDialegEliminar(idValoracio: Int, posicio: Int) {
        AlertDialog.Builder(context)
            .setTitle("Eliminar valoració")
            .setMessage("Segur que vols eliminar-la?")
            .setPositiveButton("Sí") { _, _ -> eliminarValoracio(idValoracio, posicio) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun eliminarValoracio(idValoracio: Int, posicio: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resposta = RetrofitClient.instance.eliminarValoracio(idValoracio)
                withContext(Dispatchers.Main) {
                    if (resposta.isSuccessful) {
                        llistaBicis.removeAt(posicio)
                        notifyItemRemoved(posicio)
                        listener.onBiciActualitzada()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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