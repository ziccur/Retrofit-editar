package iticbcn.m7.provavalobike.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    private val _usernameError = MutableLiveData<String?>()
    val usernameError: LiveData<String?> = _usernameError

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _confirmPasswordError = MutableLiveData<String?>()
    val confirmPasswordError: LiveData<String?> = _confirmPasswordError

    fun validate(username: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true

        if (username.isBlank()) {
            _usernameError.value = "El camp no pot estar buit"
            isValid = false
        } else {
            _usernameError.value = null
        }

        if (email.isBlank()) {
            _emailError.value = "Correu obligatori"
            isValid = false
        } else {
            _emailError.value = null
        }

        if (password.isBlank()) {
            _passwordError.value = "Contrasenya obligatòria"
            isValid = false
        } else {
            _passwordError.value = null
        }

        if (password != confirmPassword) {
            _confirmPasswordError.value = "Les contrasenyes no coincideixen"
            isValid = false
        } else {
            _confirmPasswordError.value = null
        }

        return isValid
    }
}
