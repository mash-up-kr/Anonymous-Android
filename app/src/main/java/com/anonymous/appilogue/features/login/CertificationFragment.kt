package com.anonymous.appilogue.features.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.anonymous.appilogue.R
import com.anonymous.appilogue.databinding.FragmentCertificationBinding
import com.anonymous.appilogue.features.base.BaseFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class CertificationFragment :
    BaseFragment<FragmentCertificationBinding, LoginViewModel>(R.layout.fragment_certification) {
    override val viewModel: LoginViewModel by activityViewModels()
    private lateinit var certificationNumberList: List<EditText>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        certificationNumberList = listOf(
            binding.certificationNumber1, binding.certificationNumber2,
            binding.certificationNumber3, binding.certificationNumber4,
            binding.certificationNumber5, binding.certificationNumber6
        )

        with(binding) {
            certificationBackButton.setOnClickListener {
                viewModel.stopTimer()
                activity?.onBackPressed()
            }

            certificationCloseButton.setOnClickListener {
                findNavController().navigate(R.id.action_certificationFragment_to_loginFragment)
            }

            with(certificationMoveNextButton) {
                isEnabled = false
                // 처음 Fragment 시작시에는 비활성화
                context?.let { ctx ->
                    setTextColor(ContextCompat.getColor(ctx, R.color.gray_01))
                    setBackgroundColor(ContextCompat.getColor(ctx, R.color.black_01))
                }
            }
            // 포커스 자동 넘김
            setAddTextChangeListener()
        }
        // 어디 이메일로 보냈는지
        initWhereToSendEmail()
        // 인증 번호 확인
        certificationNumberVerify()
        // 재전송 버튼 눌렀을 때
        initResendClickListener()
    }

    @SuppressLint("SetTextI18n")
    private fun initWhereToSendEmail() {
        binding.certificationWhereToSend.text = "${viewModel.emailAddress.value}로 전송되었습니다."
    }

    private fun initResendClickListener() {
        binding.resendCertificationNumber.setOnClickListener {
            with(viewModel) {
                stopTimer()
                timerReset()
                sendCertificationNumber(viewModel.lostPassword)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        if (it.isSend) {
                            Timber.d("재전송 성공")
                        } else {
                            Timber.d("재전송 실패(안보냄)")
                        }
                    }) {
                        Timber.d("{${it.message}} 에러")
                    }
            }
            allButtonColorTurnsFirstState()
        }
    }

    private fun certificationNumberVerify() {
        binding.certificationMoveNextButton.setOnClickListener {
            viewModel.verifyCertificationNumber()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isVerify) {
                        Timber.d("Verify 성공")
                        viewModel.stopTimer()
                        binding.certificationMoveNextButton
                            .findNavController().navigate(R.id.action_certificationFragment_to_passwordFragment)
                    } else {
                        clearAllCertificationNumber()
                        buttonClickUnEnable()
                        allButtonColorTurnsRed()
                    }
                }) {
                    Timber.d("Verify 오류, ${it.message}")
                }
        }
    }

    private fun setAddTextChangeListener() {
        certificationNumberList.setFocusAndChangeButtonState(focusNext(), certificationCheck())
    }

    private fun <S> Iterable<S>.setFocusAndChangeButtonState(operation: (S, S) -> Unit, lastOperation: (S) -> Unit) {
        val iterator = this.iterator()
        if (!iterator.hasNext()) throw UnsupportedOperationException("EmptyList can't be reduced")

        var value: S = iterator.next()
        var nextValue: S = value

        while (iterator.hasNext()) {
            nextValue = iterator.next()
            operation(value, nextValue)
            value = nextValue
        }
        lastOperation(nextValue)
    }

    private fun focusNext(): (EditText, EditText) -> Unit = { editText1, editText2 ->
        editText1.addTextChangedListener { newText ->
            if (!newText.isNullOrEmpty()) {
                editText1.background = ContextCompat.getDrawable(editText1.context, R.drawable.border_radius_08_purple)
                editText2.requestFocus()
                if (checkAllData()) {
                    buttonClickEnable()
                }
            } else {
                editText1.background = ContextCompat.getDrawable(editText1.context, R.drawable.border_radius_10)
                buttonClickUnEnable()
            }
        }
    }

    private fun certificationCheck(): (EditText) -> Unit = { editText ->
        editText.addTextChangedListener {
            if (!it.isNullOrEmpty()) {
                editText.background = ContextCompat.getDrawable(editText.context, R.drawable.border_radius_08_purple)
                if (checkAllData()) {
                    buttonClickEnable()
                }
            } else {
                editText.background = ContextCompat.getDrawable(editText.context, R.drawable.border_radius_10)
                buttonClickUnEnable()
            }
        }
    }

    private fun checkAllData(): Boolean {
        certificationNumberList.forEach { if (it.text.toString().isEmpty()) return false }
        return true
    }

    private fun buttonClickEnable() {
        with(binding.certificationMoveNextButton) {
            isEnabled = true
            context?.let { ctx ->
                setTextColor(ContextCompat.getColor(ctx, R.color.white))
                background = ContextCompat.getDrawable(ctx, R.drawable.border_radius_12_purple)
                background.setTint(ContextCompat.getColor(ctx, R.color.purple_01))
            }
        }

        var certificationNumber = ""
        certificationNumberList.forEach { certificationNumber += it.text.toString() }
        viewModel.certificationNumber.value = certificationNumber
    }

    private fun buttonClickUnEnable() {
        with(binding.certificationMoveNextButton) {
            isEnabled = false
            context?.let { ctx ->
                setTextColor(ContextCompat.getColor(ctx, R.color.gray_01))
                background.setTint(ContextCompat.getColor(ctx, R.color.gray_02))
            }
        }
    }

    private fun clearAllCertificationNumber() {
        certificationNumberList.forEach {
            it.text.clear()
        }
    }

    private fun allButtonColorTurnsRed() {
        val iterator = certificationNumberList.iterator()
        while (iterator.hasNext()) {
            iterator.next().background = ContextCompat.getDrawable(requireContext(), R.drawable.border_radius_16_red)
        }
        // 처음 인증번호로 포커스 이동
        certificationNumberList[0].requestFocus()
        // 인증번호 텍스트 변경
        with(binding.certificationExplainText2) {
            text = getString(R.string.wrong_certification)
            setTextColor(ContextCompat.getColor(context, R.color.red))
        }
    }

    private fun allButtonColorTurnsFirstState() {
        val iterator = certificationNumberList.iterator()
        while (iterator.hasNext()) {
            with(iterator.next()) {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.border_radius_10)
                text.clear()
            }
        }
        certificationNumberList[0].requestFocus()
        // 재전송 버튼 클릭시에 텍스트 변경
        with(binding.certificationExplainText2) {
            text = getString(R.string.resend_certification_notify)
            setTextColor(ContextCompat.getColor(context, R.color.gray_02))
        }
    }
}
