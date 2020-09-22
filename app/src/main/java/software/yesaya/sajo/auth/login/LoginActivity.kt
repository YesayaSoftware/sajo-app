package software.yesaya.sajo.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import software.yesaya.sajo.MainActivity
import software.yesaya.sajo.R
import software.yesaya.sajo.SajoApplication
import software.yesaya.sajo.databinding.ActivityLoginBinding
import timber.log.Timber
import software.yesaya.sajo.utils.Utils
import software.yesaya.sajo.utils.setupSnackbar
import java.util.*

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    private var validator: AwesomeValidation? = null

    private var view: View? = null

    private val viewModel by viewModels<LoginViewModel> {
        LoginViewModelFactory((this.applicationContext as SajoApplication).tokenManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        validator = AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT)

        view = window.decorView.findViewById(android.R.id.content)

        setupSnackbar()

        // Inflate view and obtain an instance of the binding class.
        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.viewmodel = viewModel

        viewModel.hasToken.observe(this, Observer { hasToken ->
            if (hasToken == true) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        })

        viewModel.isSubmitted.observe(this, Observer { isSubmitted ->
            if (isSubmitted == true) {
                setupRules()

                validator?.clear()

                if (validator?.validate()!!) {
                    viewModel.login()
                }
            }
        })

        viewModel.hasErrors.observe(this, Observer { hasErrors ->
            if (hasErrors == true) {
                handleErrors(
                    viewModel.hasResponse.value?.errorBody()!!,
                    viewModel.hasResponse.value?.code()!!
                )
            }
        })

        // Specify the current activity as the lifecycle owner.
        binding.lifecycleOwner = this
    }

    private fun setupRules() {
        validator?.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email)
        validator?.addValidation(this, R.id.til_password, "[a-zA-Z0-9]{6,}", R.string.err_password)
    }

    private fun handleErrors(response: ResponseBody, code: Int) {
        val apiError = Utils.convertErrors(response)

        when (code) {
            422 -> for (error in apiError?.errors?.entries!!) {
                when (error.key) {
                    "email" -> this.til_email?.error = error.value[0]
                    "password" -> this.til_password?.error = error.value[0]
                }
            }

            400 -> {
                this.til_email?.error = " "
                this.til_password?.error = " "
                this.til_email.requestFocus()
            }
        }
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }
}