package iticbcn.m7.provavalobike

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import iticbcn.m7.provavalobike.viewmodel.RegisterViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        viewModel = RegisterViewModel()
    }

    @Test
    fun testEmptyUsername() {
        viewModel.validate("", "email@correu.com", "123456A", "123456A")
        assertEquals("El camp no pot estar buit", viewModel.usernameError.value)
    }

    @Test
    fun testUsernameTooShort() {
        viewModel.validate("ps", "email@correu.com", "123456A", "123456A")
        assertEquals("Mínim 3 caràcters", viewModel.usernameError.value)
    }

    @Test
    fun testUsernameTooLong() {
        viewModel.validate("usuari_amb_nom_molt_llarg_12345", "email@correu.com", "123456A", "123456A")
        assertEquals("Màxim 20 caràcters", viewModel.usernameError.value)
    }

    @Test
    fun testUsernameSpecialChars() {
        viewModel.validate("usuari@123", "email@correu.com", "123456A", "123456A")
        assertEquals("Només lletres, números i guions baixos", viewModel.usernameError.value)
    }

    @Test
    fun testValidUsername() {
        viewModel.validate("Pau123", "email@correu.com", "123456A", "123456A")
        assertNull(viewModel.usernameError.value)
    }

    @Test
    fun testEmptyEmail() {
        viewModel.validate("usuari", "", "123456A", "123456A")
        assertEquals("Correu obligatori", viewModel.emailError.value)
    }

    @Test
    fun testInvalidEmailFormat() {
        viewModel.validate("usuari", "pau.example.com", "123456A", "123456A")
        assertEquals("Format de correu invàlid", viewModel.emailError.value)
    }

    @Test
    fun testValidEmail() {
        viewModel.validate("usuari", "pau@example.com", "123456A", "123456A")
        assertNull(viewModel.emailError.value)
    }

    @Test
    fun testEmptyPassword() {
        viewModel.validate("usuari", "pau@example.com", "", "")
        assertEquals("Contrasenya obligatòria", viewModel.passwordError.value)
    }

    @Test
    fun testPasswordTooShort() {
        viewModel.validate("usuari", "pau@example.com", "abc12", "abc12")
        assertEquals("Mínim 6 caràcters", viewModel.passwordError.value)
    }

    @Test
    fun testPasswordNoUppercase() {
        viewModel.validate("usuari", "pau@example.com", "contrasenya1!", "contrasenya1!")
        assertEquals("Inclou almenys una majúscula", viewModel.passwordError.value)
    }

    @Test
    fun testPasswordNoLowercase() {
        viewModel.validate("usuari", "pau@example.com", "CONTRASENYA1!", "CONTRASENYA1!")
        assertEquals("Inclou almenys una minúscula", viewModel.passwordError.value)
    }

    @Test
    fun testValidPassword() {
        viewModel.validate("usuari", "pau@example.com", "Pau123!", "Pau123!")
        assertNull(viewModel.passwordError.value)
    }

    @Test
    fun testMismatchedPasswords() {
        viewModel.validate("usuari", "pau@example.com", "Pau123!", "Pau124!")
        assertEquals("Les contrasenyes no coincideixen", viewModel.confirmPasswordError.value)
    }

    @Test
    fun testMatchingPasswords() {
        viewModel.validate("usuari", "pau@example.com", "Pau123!", "Pau123!")
        assertNull(viewModel.confirmPasswordError.value)
    }

    @Test
    fun testAllValid() {
        val result = viewModel.validate("usuari", "pau@example.com", "Pau123!", "Pau123!")
        assertTrue(result)
        assertNull(viewModel.usernameError.value)
        assertNull(viewModel.emailError.value)
        assertNull(viewModel.passwordError.value)
        assertNull(viewModel.confirmPasswordError.value)
    }
}