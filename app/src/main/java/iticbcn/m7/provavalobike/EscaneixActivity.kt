package iticbcn.m7.provavalobike

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class EscaneixActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private var isScanning = true
    private lateinit var previewView: PreviewView
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private lateinit var cameraProvider: ProcessCameraProvider
    private var currentDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escaneix)

        previewView = findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setupNavigation()
        checkCameraPermission()
    }

    private fun setupNavigation() {
        val btnSettings = findViewById<ImageView>(R.id.btnSettings)
        val btnQR = findViewById<ImageView>(R.id.btnQR)
        val btnProfile = findViewById<ImageView>(R.id.btnProfile)

        btnQR.setColorFilter(Color.parseColor("#4CAF50"))

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
        }

        btnQR.setOnClickListener {
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
        } else {
            startCamera()
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    Toast.makeText(
                        this,
                        "Se requiere permiso de cámara para escanear QR",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        val rotation = imageProxy.imageInfo.rotationDegrees
                        try {
                            if (mediaImage != null && isScanning) {
                                val image = InputImage.fromMediaImage(mediaImage, rotation)
                                processImage(image, imageProxy)
                            }
                        } catch (e: Exception) {
                            Log.e("Camera", "Error procesando imagen", e)
                        } finally {
                            imageProxy.close()
                        }
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analyzer
                )
            } catch (e: Exception) {
                Log.e("Camera", "Error al configurar cámara", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImage(image: InputImage, imageProxy: ImageProxy) {
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { qrValue ->
                        if (isValidBikeQR(qrValue) && isScanning) {
                            isScanning = false
                            runOnUiThread {
                                mostrarDialogoValoracion(qrValue)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("QRScanner", "Error en escaneo", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun isValidBikeQR(qrValue: String): Boolean {
        val pattern = Regex("bicing\\d{4}")
        return pattern.matches(qrValue)
    }

    private fun mostrarDialogoValoracion(matricula: String) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_review, null)

        val tvMatricula = view.findViewById<TextView>(R.id.tvMatricula)
        val etComentario = view.findViewById<EditText>(R.id.etResena)
        val ratingBar = view.findViewById<RatingBar>(R.id.rbValoracion)
        val btnEnviar = view.findViewById<Button>(R.id.btnEnviar)

        tvMatricula.text = "Matrícula: ${matricula.replace("bicing", "")}"
        ratingBar.numStars = 5
        ratingBar.rating = 3f

        btnEnviar.setOnClickListener {
            val comentario = etComentario.text.toString()
            val puntuacion = ratingBar.rating.toInt()
            val idUsuario = obtenerIdUsuario()

            when {
                comentario.isEmpty() -> showToast("Escribe un comentario")
                puntuacion !in 1..5 -> showToast("La puntuación debe estar entre 1 y 5")
                else -> {

                    enviarValoracion(matricula, comentario, puntuacion, idUsuario)
                    currentDialog?.dismiss()
                }
            }
        }

        // Configurar el diálogo
        currentDialog = builder.setView(view)
            .setCancelable(false)
            .setNegativeButton("Cancelar") { dialog, _ ->
                isScanning = true
                dialog.dismiss()
            }
            .setOnDismissListener {
                isScanning = true
            }
            .show()
    }

    private fun obtenerIdUsuario(): Int {
        // Pendent, configurar els Usuaris.
        return 1
    }

    private fun enviarValoracion(matricula: String, comentario: String, puntuacion: Int, idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val valoracionRequest = ValoracioRequest.create(matricula, puntuacion, comentario, idUsuario)

                val response = RetrofitClient.instance.enviarValoracio(valoracionRequest)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        showToast("Valoración enviada!")
                    } else {
                        showToast("Error: ${response.errorBody()?.string() ?: "Desconocido"}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error de conexión: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        currentDialog?.dismiss()
        if (::cameraProvider.isInitialized) {
            cameraProvider.unbindAll()
        }
    }
}