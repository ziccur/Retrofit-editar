package iticbcn.m7.provavalobike

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity(), BiciAdapter.OnBiciActualitzadaListener {

    private lateinit var rvBicisValorades: androidx.recyclerview.widget.RecyclerView
    private lateinit var biciAdapter: BiciAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        configurarNavegacio()
        configurarRecyclerView()
        carregarDades()
    }

    private fun configurarNavegacio() {
        val btnGraficos = findViewById<Button>(R.id.btnGraficos)
        btnGraficos.setOnClickListener {
            startActivity(Intent(this, GraficosActivity::class.java))
        }
        val btnSettings = findViewById<ImageView>(R.id.btnSettings)
        val btnQR = findViewById<ImageView>(R.id.btnQR)
        val btnProfile = findViewById<ImageView>(R.id.btnProfile)


        btnSettings.setOnClickListener {
            navegarASettings()
        }

        btnQR.setOnClickListener {
            navegarAQR()
        }

        btnProfile.setOnClickListener {
            navegarAPerfil()
        }
    }

    private fun configurarRecyclerView() {
        rvBicisValorades = findViewById(R.id.rvBicisValorades)
        rvBicisValorades.layoutManager = LinearLayoutManager(this)
        biciAdapter = BiciAdapter(this, mutableListOf(), this)
        rvBicisValorades.adapter = biciAdapter
    }

    private fun carregarDades() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bicis = RetrofitClient.instance.getBicisValorades()
                runOnUiThread {
                    biciAdapter.actualitzarLlista(bicis)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@ProfileActivity,
                        "Error carregant dades: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onBiciActualitzada() {
        carregarDades()
    }

    private fun navegarAGraficos() {
        startActivity(Intent(this, GraficosActivity::class.java))
    }

    private fun navegarASettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun navegarAQR() {
        startActivity(Intent(this, EscaneixActivity::class.java))
    }

    private fun navegarAPerfil() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }
}