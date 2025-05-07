import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityRegistreViewModel : ViewModel() {
    val missatgeErrorUsuari = MutableLiveData<String>()
    val missatgeErrorContrasenya = MutableLiveData<String>()

    fun validarNomUsuari(nom: String) {
        missatgeErrorUsuari.value = when {
            nom.isBlank() -> "El camp no pot estar buit"
            nom.length < 3 -> "El nom ha de tenir com a mínim 3 caràcters."
            nom.length > 15 -> "No es permet un nom d’usuari tan llarg"
            !nom.matches(Regex("^[a-zA-Z0-9]+$")) -> "Només es permeten lletres i números"
            else -> ""
        }
    }

    fun validarContrasenya(pwd: String) {
        missatgeErrorContrasenya.value = when {
            pwd.isBlank() -> "La contrasenya és obligatòria"
            pwd.length < 6 -> "La contrasenya és massa curta"
            pwd.length > 30 -> ""
            !pwd.any { it.isUpperCase() } -> "Inclou almenys una lletra majúscula"
            !pwd.any { it.isLowerCase() } -> "Inclou almenys una lletra minúscula"
            else -> ""
        }
    }
}
