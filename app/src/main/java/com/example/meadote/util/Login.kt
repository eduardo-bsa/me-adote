package com.example.meadote.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.example.meadote.R
import com.example.meadote.presentation.conta.ContaActivity
import kotlinx.android.synthetic.main.dialog_login.view.*

object Login {
    fun doLogin(ctx: Context) {
        val builder = AlertDialog.Builder(ctx)
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.dialog_login, null)

        val inputMethodManager: InputMethodManager =
            ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val imEmail = view.imEmail
        val ivClose = view.ivClose
        val btEmail = view.btEmail
        val menu = view.menu
        val tiEmail = view.tiEmail
        val etEmail = view.etEmail
        val btContinuar = view.btContinuar
        val tiSenha = view.tiSenha
        val etSenha = view.etSenha
        val email = view.email
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
                    Utilitarios.limpaErroCampo(etSenha, tiSenha)
                    tiSenha.error = ctx.getString(R.string.erro_senha)
                    validaSenha = false
                }

                if (etEmail.text.toString().isEmpty()
                    || !etEmail.text.toString().contains("@")
                    || !etEmail.text.toString().contains(".")) {
                    Utilitarios.validaEmail(ctx, etEmail, tiEmail)
                    validaEmail = false
                }

                if (validaSenha && validaEmail) {
                    Utilitarios.progressBar = Utilitarios.progressBar(ctx)

                    doLogin(
                        ctx,
                        etEmail.text.toString(),
                        etSenha.text.toString(),
                        alert
                    )
                }
            } else {
                if (etEmail.text.toString().isEmpty()
                    || !etEmail.text.toString().contains("@")
                    || !etEmail.text.toString().contains(".")) {
                    Utilitarios.validaEmail(ctx, etEmail, tiEmail)
                } else {
                    verificaExisteEmail(ctx, etEmail)
                }
            }
        }

        etEmail.setOnEditorActionListener { v, actionId, event ->
            if (tiSenha.visibility == View.GONE) {
                verificaExisteEmail(ctx, etEmail)
            }

            false
        }

        etSenha.setOnEditorActionListener { v, actionId, event ->
            var validaEmail = true
            var validaSenha = true

            if (etSenha.text.toString().isEmpty()) {
                Utilitarios.limpaErroCampo(etSenha, tiSenha)
                tiSenha.error = ctx.getString(R.string.erro_senha)
                validaSenha = false
            }

            if (etEmail.text.toString().isEmpty()
                || !etEmail.text.toString().contains("@")
                || !etEmail.text.toString().contains(".")) {
                Utilitarios.validaEmail(ctx, etEmail, tiEmail)
                validaEmail = false
            }

            if (validaSenha && validaEmail) {
                Utilitarios.progressBar = Utilitarios.progressBar(ctx)

                doLogin(ctx, etEmail.text.toString(), etSenha.text.toString(), alert)
            }

            false
        }
    }

    private fun verificaExisteEmail(ctx: Context, etEmail: EditText) {
        Utilitarios.progressBar = Utilitarios.progressBar(ctx)

        //validar se o e-mail existe
        var existeEmail = false

        if (existeEmail) {
            //se existir
        } else {
            Utilitarios.progressBar?.dismiss()

            val intent = Intent(ctx, ContaActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra("email", etEmail.text.toString())

            ctx.startActivity(intent)
        }
    }

    private fun doLogin(ctx: Context, email: String, senha: String, alert: AlertDialog) {
        //validar login
        if (1 == 1) {
            Toast.makeText(
                ctx,
                ctx.getString(R.string.entrou),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Utilitarios.progressBar?.dismiss()

            Toast.makeText(
                ctx,
                ctx.getString(R.string.erro_login),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}