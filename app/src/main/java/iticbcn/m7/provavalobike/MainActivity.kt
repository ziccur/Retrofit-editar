package iticbcn.m7.provavalobike

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnTutorial = findViewById<Button>(R.id.button3)
        val btnLogin = findViewById<Button>(R.id.button4)
        val btnRegister = findViewById<Button>(R.id.button5)
        val btnScan = findViewById<Button>(R.id.button6)
        val btnProfile = findViewById<Button>(R.id.button7)
        val btnTutorial2 = findViewById<Button>(R.id.button8)
        val btnTutorial3 = findViewById<Button>(R.id.button9)
        val btnLogo = findViewById<Button>(R.id.button10)
        val btnSettings = findViewById<Button>(R.id.button11)


        btnTutorial.setOnClickListener {
            val intent = Intent(this@MainActivity, Tutorial1Activity::class.java)
            startActivity(intent)
        }

        btnTutorial2.setOnClickListener {
            val intent = Intent(this@MainActivity, Tutorial2Activity::class.java)
            startActivity(intent)
        }

        btnTutorial3.setOnClickListener {
            val intent = Intent(this@MainActivity, Tutorial3Activity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnScan.setOnClickListener {
            val intent = Intent(this@MainActivity, EscaneixActivity::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        btnLogo.setOnClickListener {
            val intent = Intent(this@MainActivity, ChoseActivity::class.java)
            startActivity(intent)
        }
        btnSettings.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}