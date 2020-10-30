package com.example.meadote.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.meadote.R
import com.example.meadote.presentation.conta.ContaActivity
import com.google.android.material.textfield.TextInputLayout

object Utilitarios {

    fun login(ctx: Context) {
        val builder = AlertDialog.Builder(ctx)
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.dialog_login, null)

        val inputMethodManager: InputMethodManager =
            ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val imEmail = view.findViewById<ImageView>(R.id.imEmail)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)
        val btEmail = view.findViewById<Button>(R.id.btEmail)
        val menu = view.findViewById<View>(R.id.menu)
        val tiEmail = view.findViewById<TextInputLayout>(R.id.tiEmail)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val btContinuar = view.findViewById<Button>(R.id.btContinuar)
        val tiSenha = view.findViewById<TextInputLayout>(R.id.tiSenha)
        val etSenha = view.findViewById<EditText>(R.id.etSenha)
        val email = view.findViewById<View>(R.id.email)
        imEmail.bringToFront()

        builder.setView(view)

        val alert = builder.create()
        alert.getWindow()!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        alert.show()

        btEmail.setOnClickListener {
            menu.visibility = View.GONE
            email.visibility = View.VISIBLE

            etSenha.text = null
            tiSenha.error = null
            tiEmail.error = null

            etEmail.post {
                inputMethodManager.showSoftInput(
                    etEmail,
                    InputMethodManager.SHOW_IMPLICIT
                )
            }
            etEmail.requestFocus()
        }

        ivClose.setOnClickListener {
            if (menu.visibility == View.GONE) {
                menu.visibility = View.VISIBLE
                email.visibility = View.GONE
                tiSenha.visibility = View.GONE

                etEmail.clearFocus()
                etSenha.clearFocus()
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            } else {
                alert.hide()
            }
        }

        btContinuar.setOnClickListener {
            var validaEmail = true
            var validaSenha = true
            if (tiSenha.visibility == View.VISIBLE) {
                if (etSenha.text.toString().isEmpty()) {
                    limpaErroCampo(etSenha, tiSenha)
                    tiSenha.error = ctx.getString(R.string.erro_senha)
                    validaSenha = false
                }

                if (etEmail.text.toString().isEmpty()
                    || !etEmail.text.toString().contains("@")
                    || !etEmail.text.toString().contains(".")) {
                    validaEmail(ctx, etEmail, tiEmail)
                    validaEmail = false
                }

                /*if (validaSenha && validaEmail) {

                }*/
            } else {
                if (etEmail.text.toString().isEmpty()
                    || !etEmail.text.toString().contains("@")
                    || !etEmail.text.toString().contains(".")) {
                    validaEmail(ctx, etEmail, tiEmail)
                } else {
                    var existe = false
                    if (existe) {
                        tiSenha.visibility = View.VISIBLE
                        etSenha.requestFocus()
                    } else {
                        val intent = Intent(ctx, ContaActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

                        ctx.startActivity(intent)
                    }
                }
            }
        }

        etEmail.setOnEditorActionListener { v, actionId, event ->
            if (etEmail.text.toString().isEmpty()
                || !etEmail.text.toString().contains("@")
                || !etEmail.text.toString().contains(".")) {
                validaEmail(ctx, etEmail, tiEmail)
            } else {
                var existe = false
                if (tiSenha.visibility != View.VISIBLE) {
                    if (existe) {
                        tiSenha.visibility = View.VISIBLE
                        etSenha.requestFocus()
                    } else {
                        val intent = Intent(ctx, ContaActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

                        ctx.startActivity(intent)
                    }
                }
            }
            false
        }

        etSenha.setOnEditorActionListener { v, actionId, event ->
            var validaEmail = true
            var validaSenha = true
            if (etSenha.text.toString().isEmpty()) {
                limpaErroCampo(etSenha, tiSenha)
                tiSenha.error = ctx.getString(R.string.erro_senha)
                validaSenha = false
            }

            if (etEmail.text.toString().isEmpty()
                || !etEmail.text.toString().contains("@")
                || !etEmail.text.toString().contains(".")) {
                validaEmail(ctx, etEmail, tiEmail)
                validaEmail = false
            }

            /*if (validaSenha && validaEmail) {

            }*/

            false
        }
    }

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

    fun validaCampo(ctx: Context, etCampo: EditText?, tiCampo: TextInputLayout?) {
        etCampo?.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    limpaErroCampo(etCampo, tiCampo)

                    if (etCampo?.text.toString().isEmpty()) {
                        tiCampo?.error = ctx.getString(R.string.campo_obrigatorio)
                    }
                }
            }
    }

    fun progressBar(ctx: Context) : AlertDialog {
        val builder = AlertDialog.Builder(ctx)

        val inflater = LayoutInflater.from(ctx)

        val view = inflater.inflate(R.layout.progress_bar, null)

        builder.setView(view)

        val alert = builder.create()

        alert.show()
        alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.setCancelable(false)
        alert.setCanceledOnTouchOutside(false)

        return alert
    }
}