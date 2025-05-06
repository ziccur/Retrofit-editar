import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityRegistreViewModel : ViewModel() {
    // LiveData para errores
    val missatgeErrorUsuari = MutableLiveData<String>()
    val missatgeErrorEmail = MutableLiveData<String>()
    val missatgeErrorContrasenya = MutableLiveData<String>()
    val missatgeErrorConfirmacio = MutableLiveData<String>()

    // Variables para comparar contraseñas
    private var contrasenyaActual = ""
    private var confirmacioActual = ""

    fun validarNomUsuari(nom: String): Boolean {
        return when {
            nom.isBlank() -> {
                missatgeErrorUsuari.value = "El camp no pot estar buit"
                false
            }
            nom.length < 3 -> {
                missatgeErrorUsuari.value = "Mínim 3 caràcters"
                false
            }
            nom.length > 20 -> {
                missatgeErrorUsuari.value = "Màxim 20 caràcters"
                false
            }
            !nom.matches(Regex("^[a-zA-Z0-9_]+$")) -> {
                missatgeErrorUsuari.value = "Només lletres, números i guions baixos"
                false
            }
            else -> {
                missatgeErrorUsuari.value = ""
                true
            }
        }
    }

    fun validarEmail(email: String): Boolean {
        return when {
            email.isBlank() -> {
                missatgeErrorEmail.value = "Correu obligatori"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                missatgeErrorEmail.value = "Format de correu invàlid"
                false
            }
            else -> {
                missatgeErrorEmail.value = ""
                true
            }
        }
    }

    fun validarContrasenya(pwd: String): Boolean {
        contrasenyaActual = pwd
        return when {
            pwd.isBlank() -> {
                missatgeErrorContrasenya.value = "Contrasenya obligatòria"
                false
            }
            pwd.length < 6 -> {
                missatgeErrorContrasenya.value = "Mínim 6 caràcters"
                false
            }
            !pwd.any { it.isUpperCase() } -> {
                missatgeErrorContrasenya.value = "Inclou almenys una majúscula"
                false
            }
            !pwd.any { it.isLowerCase() } -> {
                missatgeErrorContrasenya.value = "Inclou almenys una minúscula"
                false
            }
            else -> {
                missatgeErrorContrasenya.value = ""
                true
            }
        }.also {
            if (confirmacioActual.isNotEmpty()) validarConfirmacio(confirmacioActual)
        }
    }

    fun validarConfirmacio(confirmacio: String): Boolean {
        confirmacioActual = confirmacio
        return when {
            confirmacio != contrasenyaActual -> {
                missatgeErrorConfirmacio.value = "Les contrasenyes no coincideixen"
                false
            }
            else -> {
                missatgeErrorConfirmacio.value = ""
                true
            }
        }
    }

    fun validarRegistreComplet(): Boolean {
        return (missatgeErrorUsuari.value?.isEmpty() == true &&
                missatgeErrorEmail.value?.isEmpty() == true &&
                missatgeErrorContrasenya.value?.isEmpty() == true &&
                missatgeErrorConfirmacio.value?.isEmpty() == true)
    }
}