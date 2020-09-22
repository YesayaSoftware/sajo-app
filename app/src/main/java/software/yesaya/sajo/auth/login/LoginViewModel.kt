package software.yesaya.sajo.auth.login

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import software.yesaya.sajo.R
import software.yesaya.sajo.data.sources.remote.ApiService
import software.yesaya.sajo.data.sources.remote.network.AccessToken
import software.yesaya.sajo.data.sources.remote.network.Network
import software.yesaya.sajo.data.sources.remote.network.TokenManager
import software.yesaya.sajo.utils.Event
import timber.log.Timber

class LoginViewModel(
    private val tokenManager: TokenManager?
) : ViewModel() {
    private val _hasToken = MutableLiveData<Boolean>()
    val hasToken: LiveData<Boolean> = _hasToken

    private var service: ApiService? = null
    private var call: Call<AccessToken>? = null

    private val _hasErrors = MutableLiveData<Boolean>()
    val hasErrors: LiveData<Boolean> = _hasErrors


    private val _hasResponse = MutableLiveData<Response<AccessToken>>()
    val hasResponse: LiveData<Response<AccessToken>> = _hasResponse

    private val _isSubmitted = MutableLiveData<Boolean>()
    val isSubmitted: LiveData<Boolean> = _isSubmitted

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    // Two-way databinding, exposing MutableLiveData
    val email = MutableLiveData<String>()

    // Two-way databinding, exposing MutableLiveData
    val password = MutableLiveData<String>()

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        if (tokenManager?.getTokens()?.access_token != null) {
            _hasToken.value = true
        }

        service = Network.createService(ApiService::class.java)

        _isSubmitted.value = false
        _hasErrors.value = false
    }

    // Called when clicking on fab.
    fun validate() {
        _isSubmitted.value = true
    }

    fun login() {
        val _email = email.value!!
        val _password = password.value!!

        call = service!!.login(_email, _password)

        call!!.enqueue(object : Callback<AccessToken> {
            override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                if (response.isSuccessful) {
                    tokenManager?.saveToken(response.body()!!)

                    _hasToken.value = true
                } else {
                    _hasResponse.value = response

                    when (response.code()) {
                        400 -> _snackbarText.value = Event(R.string.err_invalid_credentials)
                        else -> {
                            _snackbarText.value = Event(R.string.err_server)
                        }
                    }

                    _hasErrors.value = true
                }
            }

            override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                Timber.w("onFailure")
            }
        })
    }
}