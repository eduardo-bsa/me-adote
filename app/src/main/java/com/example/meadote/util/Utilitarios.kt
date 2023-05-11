package com.example.meadote.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.meadote.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*

@SuppressLint("StaticFieldLeak")
object Utilitarios {

    private const val PREF = "com.example.meadote.util.PREF"
    var progressBar: AlertDialog? = null

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

    fun isConnected(
        context: Context?
    ): Boolean {
        val cm = context?.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val ni = cm.activeNetworkInfo
        if (ni != null && ni.isConnected) {
            return ni.isConnected
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.erro_conexao),
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    fun salvaString(context: Context, key: String?, value: String?) {
        val sp: SharedPreferences =
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit().putString(key, value).apply()
    }

    fun consultaString(context: Context?, key: String?): String? {
        val sp: SharedPreferences? =
            context?.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return sp?.getString(key, "")
    }

    fun limpaString(context: Context) {
        val sp: SharedPreferences =
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit().clear().apply()
    }
}