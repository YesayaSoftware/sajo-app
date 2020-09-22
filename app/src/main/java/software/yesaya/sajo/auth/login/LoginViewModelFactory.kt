package software.yesaya.sajo.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import software.yesaya.sajo.data.sources.remote.network.TokenManager

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val tokenManager: TokenManager?
)  : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (LoginViewModel(tokenManager) as T)
}