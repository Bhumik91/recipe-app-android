package com.example.recipeapp.features.auth.activity

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.recipeapp.R
import com.example.recipeapp.core.base.AuthField
import com.example.recipeapp.core.base.UiState
import com.example.recipeapp.databinding.ActivitySignupBinding
import com.example.recipeapp.features.auth.viewmodel.SignupViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val viewModel: SignupViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
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
        binding.btnSignup.setOnClickListener {
            attemptSignup()
        }

        val editorActionListener = android.widget.TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                attemptSignup()
                true
            } else {
                false
            }
        }
        
        binding.etName.setOnEditorActionListener(editorActionListener)
        binding.etEmail.setOnEditorActionListener(editorActionListener)
        binding.etPassword.setOnEditorActionListener(editorActionListener)
        binding.etConfirmPassword.setOnEditorActionListener(editorActionListener)

        binding.etName.doOnTextChanged { _, _, _, _ -> binding.tilName.error = null }
        binding.etEmail.doOnTextChanged { _, _, _, _ -> binding.tilEmail.error = null }
        binding.etPassword.doOnTextChanged { _, _, _, _ -> binding.tilPassword.error = null }
        binding.etConfirmPassword.doOnTextChanged { _, _, _, _ -> binding.tilConfirmPassword.error = null }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun attemptSignup() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val isTermsChecked = binding.cbAcceptTerms.isChecked

        binding.tilName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        hideKeyboard()
        viewModel.signup(name, email, password, confirmPassword, isTermsChecked)
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
                        Toast.makeText(this@SignupActivity, "Signup successful!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is UiState.Error -> {
                        updateButtonLoadingState(isLoading = false)
                        binding.tilName.error = null
                        binding.tilEmail.error = null
                        binding.tilPassword.error = null
                        binding.tilConfirmPassword.error = null

                        if (state.fieldErrors.isNotEmpty()) {
                            state.fieldErrors.forEach { (field, errorMsg) ->
                                when (field) {
                                    AuthField.Name -> binding.tilName.error = errorMsg
                                    AuthField.Email -> binding.tilEmail.error = errorMsg
                                    AuthField.Password -> binding.tilPassword.error = errorMsg
                                    AuthField.ConfirmPassword -> binding.tilConfirmPassword.error = errorMsg
                                    AuthField.Terms -> Toast.makeText(this@SignupActivity, errorMsg, Toast.LENGTH_SHORT).show()
                                    else -> {}
                                }
                            }
                            
                            when (state.fieldErrors.keys.firstOrNull { it != AuthField.Terms }) {
                                AuthField.Name -> binding.etName
                                AuthField.Email -> binding.etEmail
                                AuthField.Password -> binding.etPassword
                                AuthField.ConfirmPassword -> binding.etConfirmPassword
                                else -> null
                            }?.let { errorView ->
                                errorView.requestFocus()
                                showKeyboard(errorView)
                            }
                        } else {
                            Toast.makeText(this@SignupActivity, state.message, Toast.LENGTH_SHORT).show()
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
            binding.btnSignup.isEnabled = false
            binding.btnSignup.text = ""
            binding.btnSignup.icon = null
            binding.pbLoading.visibility = View.VISIBLE
        } else {
            binding.btnSignup.isEnabled = true
            binding.btnSignup.text = getString(R.string.signup_btn)
            binding.btnSignup.icon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_forward)
            binding.pbLoading.visibility = View.GONE
        }
    }
}
