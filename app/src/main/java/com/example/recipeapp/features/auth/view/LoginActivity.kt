package com.example.recipeapp.features.auth.view

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.recipeapp.core.base.AuthField
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.databinding.ActivityLoginBinding
import com.example.recipeapp.features.auth.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.view.View
import androidx.core.content.ContextCompat
import com.example.recipeapp.MainActivity
import com.example.recipeapp.R
import kotlinx.coroutines.launch

class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configureOnClicks()
        observeUiState()
    }

    private fun configureOnClicks() {
        binding.btnSignIn.setOnClickListener {
            attemptLogin()
        }
        
        binding.etUserName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                attemptLogin()
                true
            } else {
                false
            }
        }
        
        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin()
                true
            } else {
                false
            }
        }

        binding.etUserName.doOnTextChanged { _, _, _, _ ->
            binding.tilUserName.error = null
        }

        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            binding.tilPassword.error = null
        }

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val username = binding.etUserName.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        binding.tilUserName.error = null
        binding.tilPassword.error = null

        hideKeyboard()
        viewModel.login(username, password)
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Idle -> {
                        updateButtonLoadingState(isLoading = false)
                    }
                    is UiState.Loading -> {
                        hideKeyboard()
                        updateButtonLoadingState(isLoading = true)
                    }
                    is UiState.Success -> {
                        updateButtonLoadingState(isLoading = false)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                    is UiState.Error -> {
                        updateButtonLoadingState(isLoading = false)
                        binding.tilUserName.error = null
                        binding.tilPassword.error = null

                        if (state.fieldErrors.isNotEmpty()) {
                            state.fieldErrors.forEach { (field, errorMsg) ->
                                when (field) {
                                    AuthField.UserName -> binding.tilUserName.error = errorMsg
                                    AuthField.Password -> binding.tilPassword.error = errorMsg
                                    else -> {}
                                }
                            }
                            
                            when (state.fieldErrors.keys.firstOrNull()) {
                                AuthField.UserName -> binding.etUserName
                                AuthField.Password -> binding.etPassword
                                else -> null
                            }?.let { errorView ->
                                errorView.requestFocus()
                                showKeyboard(errorView)
                            }
                        } else {
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        val view = currentFocus ?: binding.root
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(view, 0)
    }

    private fun updateButtonLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnSignIn.isEnabled = false
            binding.btnSignIn.text = ""
            binding.btnSignIn.icon = null
            binding.pcLoading.visibility = View.VISIBLE
        } else {
            binding.btnSignIn.isEnabled = true
            binding.btnSignIn.text = getString(R.string.login_btn)
            binding.btnSignIn.icon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_forward)
            binding.pcLoading.visibility = View.GONE
        }
    }
}