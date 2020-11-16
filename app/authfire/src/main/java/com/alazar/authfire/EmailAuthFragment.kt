package com.alazar.authfire

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.lifecycle.ViewModelProvider
import com.alazar.authfire.databinding.FragmentEmailBinding
import com.alazar.authfire.di.AuthApp
import com.alazar.authfire.util.Validator
import com.alazar.authfire.viewmodel.EmailAuthViewModel
import javax.inject.Inject


class EmailAuthFragment : BaseAuthFragment(), View.OnClickListener {

    private var _binding: FragmentEmailBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: EmailAuthViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AuthApp().getComponent().inject(this)

        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(EmailAuthViewModel::class.java)

        viewModel.getIsAuthenticated().observe(requireActivity(), {
            binding.progressBar.visibility = View.INVISIBLE

            if (it)
                makeText(context, "AUTH SUCCESS", Toast.LENGTH_SHORT).show()
            else
                makeText(context, "AUTH FAILED", Toast.LENGTH_SHORT).show()
        })

        binding.btnSwitchAuth.setOnClickListener(this)
        binding.btnSignIn.setOnClickListener(this)
        binding.btnCreateAccount.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.btnSignIn.id -> {
                if (!validateForm()) {
                    showToast(getString(R.string.form_incorrect))
                    return
                }
                binding.progressBar.visibility = View.VISIBLE
                viewModel.signIn(
                    binding.fieldEmail.text.toString().trim(),
                    binding.fieldPassword.text.toString().trim()
                )
            }
            binding.btnCreateAccount.id -> {
                if (!validateForm()) {
                    showToast(getString(R.string.form_incorrect))
                    return
                }
                binding.progressBar.visibility = View.VISIBLE
                viewModel.createAccount(
                    binding.fieldEmail.text.toString().trim(),
                    binding.fieldPassword.text.toString().trim()
                )
            }
            binding.btnSwitchAuth.id -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
                    .replace(this.id, PhoneAuthFragment()).commit()
            }
        }

    }

    private fun validateForm(): Boolean {
        var valid = true
        val email: String = binding.fieldEmail.text.toString().trim()
        val password: String = binding.fieldPassword.text.toString().trim()

        if (!Validator.validateRequired(email)) {
            binding.fieldEmail.error = getString(R.string.field_required)
            valid = false
        } else if (Validator.validateRequired(email) && !Validator.validateEmail(email)) {
            binding.fieldEmail.error = getString(R.string.form_incorrect)
            valid = false
        }

        if (!Validator.validateRequired(password)) {
            binding.fieldPassword.error = getString(R.string.field_required)
            valid = false
        } else if (Validator.validateRequired(password) && !Validator.validatePass(password)) {
            binding.fieldPassword.error = getString(R.string.form_incorrect)
            valid = false
        }
        return valid
    }

}