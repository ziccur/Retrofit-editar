package iticbcn.m7.provavalobike

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        val btnSettings = findViewById<ImageView>(R.id.btnSettings)
        val btnQR = findViewById<ImageView>(R.id.btnQR)
        val btnProfile = findViewById<ImageView>(R.id.btnProfile)

        if (btnSettings != null) {
            btnSettings.setOnClickListener {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        if (btnQR != null) {
            btnQR.setOnClickListener {
                val intent = Intent(this, EscaneixActivity::class.java)
                startActivity(intent)
            }
        }

        if (btnProfile != null) {
            btnProfile.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }
}