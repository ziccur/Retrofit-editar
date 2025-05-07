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
    fun testEmptyFields() {
        viewModel.validate("", "", "", "")
        assertEquals("El camp no pot estar buit", viewModel.usernameError.value)
        assertEquals("Correu obligatori", viewModel.emailError.value)
        assertEquals("Contrasenya obligatòria", viewModel.passwordError.value)
        assertEquals("Les contrasenyes no coincideixen", viewModel.confirmPasswordError.value)
    }

    @Test
    fun testValidInput() {
        val result = viewModel.validate("usuari", "email@correu.com", "123456A", "123456A")
        assertTrue(result)
        assertNull(viewModel.usernameError.value)
        assertNull(viewModel.emailError.value)
        assertNull(viewModel.passwordError.value)
        assertNull(viewModel.confirmPasswordError.value)
    }

    @Test
    fun testMismatchedPasswords() {
        viewModel.validate("usuari", "email@correu.com", "123456A", "abcdef")
        assertEquals("Les contrasenyes no coincideixen", viewModel.confirmPasswordError.value)
    }
}