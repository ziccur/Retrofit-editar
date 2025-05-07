package iticbcn.m7.provavalobike

import android.content.Intent
import android.opengl.ETC1.isValid
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import iticbcn.m7.provavalobike.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        val viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPasswordInput)
        val registerButton = findViewById<ImageButton>(R.id.registerButton)

        viewModel.usernameError.observe(this) {
            usernameInput.error = it
        }
        viewModel.emailError.observe(this) {
            emailInput.error = it
        }
        viewModel.passwordError.observe(this) {
            passwordInput.error = it
        }
        viewModel.confirmPasswordError.observe(this) {
            confirmPasswordInput.error = it
        }

        registerButton.setOnClickListener {
            val valid = viewModel.validate(
                usernameInput.text.toString(),
                emailInput.text.toString(),
                passwordInput.text.toString(),
                confirmPasswordInput.text.toString()
            )

            if (valid) {
                Toast.makeText(this, "Registre correcte!", Toast.LENGTH_SHORT).show()
                // Aquí puedes redirigir al usuario a otra pantalla, como LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Revisa els errors al formulari.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}

