package iticbcn.m7.provavalobike


import iticbcn.m7.provavalobike.viewmodel.RegisterViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        viewModel = RegisterViewModel()
    }

    @Test
    fun usernameEmpty_showsError() {
        viewModel.validate("", "test@example.com", "Pau123!", "Pau123!")
        assertEquals("El camp no pot estar buit", viewModel.usernameError.value)
    }

    @Test
    fun usernameTooShort_showsError() {
        viewModel.validate("ps", "test@example.com", "Pau123!", "Pau123!")
        assertEquals("Mínim 3 caràcters", viewModel.usernameError.value)
    }

    @Test
    fun usernameTooLong_showsError() {
        viewModel.validate("usuari_amb_nom_molt_llarg_12345", "test@example.com", "Pau123!", "Pau123!")
        assertEquals("Màxim 20 caràcters", viewModel.usernameError.value)
    }

    @Test
    fun usernameWithSpecialChars_showsError() {
        viewModel.validate("usuari@123", "test@example.com", "Pau123!", "Pau123!")
        assertEquals("Només lletres, números i guions baixos", viewModel.usernameError.value)
    }

    @Test
    fun validUsername_noError() {
        viewModel.validate("Pau123", "test@example.com", "Pau123!", "Pau123!")
        assertNull(viewModel.usernameError.value)
    }

    @Test
    fun emailMissingAtSymbol_showsError() {
        viewModel.validate("Pau123", "pau.example.com", "Pau123!", "Pau123!")
        assertEquals("Format de correu invàlid", viewModel.emailError.value)
    }

    @Test
    fun emailEmpty_showsError() {
        viewModel.validate("Pau123", "", "Pau123!", "Pau123!")
        assertEquals("Correu obligatori", viewModel.emailError.value)
    }

    @Test
    fun passwordTooShort_showsError() {
        viewModel.validate("Pau123", "test@example.com", "abc12", "abc12")
        assertEquals("Mínim 6 caràcters", viewModel.passwordError.value)
    }

    @Test
    fun passwordNoUppercase_showsError() {
        viewModel.validate("Pau123", "test@example.com", "contrasenya1!", "contrasenya1!")
        assertEquals("Inclou almenys una majúscula", viewModel.passwordError.value)
    }

    @Test
    fun passwordNoLowercase_showsError() {
        viewModel.validate("Pau123", "test@example.com", "CONTRASENYA1!", "CONTRASENYA1!")
        assertEquals("Inclou almenys una minúscula", viewModel.passwordError.value)
    }

    @Test
    fun passwordsDoNotMatch_showsError() {
        viewModel.validate("Pau123", "test@example.com", "Pau123!", "Pau124!")
        assertEquals("Les contrasenyes no coincideixen", viewModel.confirmPasswordError.value)
    }

    @Test
    fun allValid_noErrors() {
        val result = viewModel.validate("Pau123", "pau@example.com", "Pau123!", "Pau123!")
        assertTrue(result)
        assertNull(viewModel.usernameError.value)
        assertNull(viewModel.emailError.value)
        assertNull(viewModel.passwordError.value)
        assertNull(viewModel.confirmPasswordError.value)
    }
}
