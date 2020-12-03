package com.alazar.authfire

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.alazar.authfire.databinding.FragmentPhoneBinding
import com.alazar.authfire.di.AuthApp
import com.alazar.authfire.model.PhoneAuthState
import com.alazar.authfire.util.Validator
import com.alazar.authfire.viewmodel.PhoneViewModel
import com.alazar.base.BaseFragment
import javax.inject.Inject


class PhoneFragment : BaseFragment(), View.OnClickListener {

    private var _binding: FragmentPhoneBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: PhoneViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AuthApp().getComponent().inject(this)

        updateUI(PhoneAuthState.STATE_INITIALIZED)

        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(PhoneViewModel::class.java)

        viewModel.getStatus().observe(requireActivity(), {
            if (it.status == PhoneAuthState.STATE_SIGNIN_SUCCESS) {
                requireActivity().setResult(Activity.RESULT_OK)
                requireActivity().finish()
            } else it.status?.let { status -> updateUI(status) }
        })

        binding.btnSwitchAuth.setOnClickListener(this)
        binding.btnStartVerification.setOnClickListener(this)
        binding.btnResend.setOnClickListener(this)
        binding.btnVerifyPhone.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.btnStartVerification.id -> {
                if (!validatePhoneNumber()) {
                    showToast(getString(R.string.form_incorrect))
                    return
                }
                binding.progressBar.visibility = View.VISIBLE
                viewModel.startVerification(
                    binding.fieldPhoneNumber.text.toString().trim(),
                    requireActivity()
                )
            }
            binding.btnResend.id -> {
                if (!validatePhoneNumber()) {
                    showToast(getString(R.string.form_incorrect))
                    return
                }
                binding.progressBar.visibility = View.VISIBLE
                viewModel.resendVerification(
                    binding.fieldPhoneNumber.text.toString().trim(),
                    requireActivity()
                )
            }
            binding.btnVerifyPhone.id -> {
                val code: String = binding.fieldVerificationCode.text.toString().trim()
                if (TextUtils.isEmpty(code)) {
                    binding.fieldVerificationCode.error = getString(R.string.field_required)
                    return
                }

                binding.progressBar.visibility = View.VISIBLE
                viewModel.verify(code)
            }
            binding.btnSwitchAuth.id -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                    )
                    .replace(this.id, EmailFragment()).commit()
            }
        }
    }

    private fun updateUI(state: Int) {
        binding.progressBar.visibility = View.INVISIBLE

        when (state) {
            PhoneAuthState.STATE_INITIALIZED -> {
                enableViews(binding.btnStartVerification, binding.fieldPhoneNumber)
                disableViews(
                    binding.btnVerifyPhone,
                    binding.btnResend,
                    binding.fieldVerificationCode
                )
            }
            PhoneAuthState.STATE_CODE_SENT -> {
                enableViews(
                    binding.btnVerifyPhone,
                    binding.btnResend,
                    binding.fieldPhoneNumber,
                    binding.fieldVerificationCode
                )
                disableViews(binding.btnStartVerification)
                showToast(getString(R.string.status_code_sent))
            }
            PhoneAuthState.STATE_VERIFY_FAILED -> {
                enableViews(
                    binding.btnStartVerification,
                    binding.btnVerifyPhone,
                    binding.btnResend,
                    binding.fieldPhoneNumber,
                    binding.fieldVerificationCode
                )
                showToast(getString(R.string.status_verification_failed))
            }
            PhoneAuthState.STATE_VERIFY_SUCCESS -> {
                disableViews(
                    binding.btnStartVerification,
                    binding.btnVerifyPhone,
                    binding.btnResend,
                    binding.fieldPhoneNumber,
                    binding.fieldVerificationCode
                )
                showToast(getString(R.string.status_verification_succeeded))
            }
            PhoneAuthState.STATE_SIGNIN_FAILED ->
                showToast(getString(R.string.status_sign_in_failed))

        }
    }


    private fun validatePhoneNumber(): Boolean {
        val phoneNumber: String = binding.fieldPhoneNumber.text.toString()
        if (!Validator.validateRequired(phoneNumber) || !Validator.validatePhone(phoneNumber)) {
            binding.fieldPhoneNumber.error = getString(R.string.phone_incorrect)
            return false
        }
        return true
    }
}