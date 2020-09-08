package com.example.meadote.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.example.meadote.R
import com.google.android.material.textfield.TextInputLayout

object Utilitarios {

    fun limpaErroCampo(etCampo: EditText?, tiCampo: TextInputLayout?) {
        etCampo?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (!etCampo.text.toString().isEmpty()) {
                    tiCampo?.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    fun validaEmail(ctx: Context, etCampo: EditText?, tiCampo: TextInputLayout?) {
        limpaErroCampo(etCampo, tiCampo)

        if (etCampo?.text.toString().isEmpty()) {
            tiCampo?.error = ctx.getString(R.string.erro_email)
            etCampo?.clearFocus()
        } else if (!etCampo?.text.toString().contains("@")
            || !etCampo?.text.toString().contains(".")
        ) {
            tiCampo?.error = ctx.getString(R.string.email_invalido)
            etCampo?.clearFocus()
        }
    }
}