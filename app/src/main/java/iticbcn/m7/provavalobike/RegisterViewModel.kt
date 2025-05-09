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
        _usernameError.value = null
        _emailError.value = null
        _passwordError.value = null
        _confirmPasswordError.value = null

        var isValid = true

        when {
            username.isEmpty() -> {
                _usernameError.value = "El camp no pot estar buit"
                isValid = false
            }
            username.length < 3 -> {
                _usernameError.value = "Mínim 3 caràcters"
                isValid = false
            }
            username.length > 20 -> {
                _usernameError.value = "Màxim 20 caràcters"
                isValid = false
            }
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> {
                _usernameError.value = "Només lletres, números i guions baixos"
                isValid = false
            }
        }

        when {
            email.isEmpty() -> {
                _emailError.value = "Correu obligatori"
                isValid = false
            }
            !email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) -> {
                _emailError.value = "Format de correu invàlid"
                isValid = false
            }
        }

        when {
            password.isEmpty() -> {
                _passwordError.value = "Contrasenya obligatòria"
                isValid = false
            }
            password.length < 6 -> {
                _passwordError.value = "Mínim 6 caràcters"
                isValid = false
            }
            !password.matches(Regex(".*[A-Z].*")) -> {
                _passwordError.value = "Inclou almenys una majúscula"
                isValid = false
            }
            !password.matches(Regex(".*[a-z].*")) -> {
                _passwordError.value = "Inclou almenys una minúscula"
                isValid = false
            }
        }

        if (password != confirmPassword) {
            _confirmPasswordError.value = "Les contrasenyes no coincideixen"
            isValid = false
        }

        return isValid
    }
}