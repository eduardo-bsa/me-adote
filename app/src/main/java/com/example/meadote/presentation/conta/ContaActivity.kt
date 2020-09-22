package com.example.meadote.presentation.conta

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.meadote.R
import com.example.meadote.presentation.main.MainActivity
import com.example.meadote.util.Utilitarios
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_conta.*
import kotlinx.android.synthetic.main.activity_main.drawer_layout
import kotlinx.android.synthetic.main.activity_main.nav_view
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_conta.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class ContaActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conta)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initDrawerLayoutAndNavigation()
    }

    override fun onStart() {
        super.onStart()

        validaCampos()
        salva()
    }

    private fun validaCampos() {
        Utilitarios.validaCampo(this, etNome, tiNome)
        Utilitarios.validaCampo(this, etRua, tiRua)
        Utilitarios.validaCampo(this, etNumero, tiNumero)
        Utilitarios.validaCampo(this, etSenha, tiSenha)
        Utilitarios.validaCampo(this, etBairro, tiBairro)

        etEmail.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    Utilitarios.limpaErroCampo(etEmail, tiEmail)

                    if (etEmail.text.toString().isEmpty()) {
                        tiEmail?.error = getString(R.string.campo_obrigatorio)
                    } else if (!etEmail.text.toString().contains("@") ||
                        !etEmail.text.toString().contains(".")) {
                        tiEmail?.error = getString(R.string.email_invalido)
                    }
                }
            }

        etCEP.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    Utilitarios.limpaErroCampo(etCEP, tiCEP)

                    if (etCEP.text.toString().isEmpty()) {
                        tiCEP?.error = getString(R.string.campo_obrigatorio)
                    } else if (etCEP.text.toString().replace("-", "").length < 8) {
                        tiCEP?.error = getString(R.string.cep_invalido)
                    } else {
                        val urlCEP = "https://viacep.com.br/ws/${etCEP.text.toString().replace("-", "")}/json"
                        doAsync {
                            val url = URL(urlCEP)
                            val urlConnection = url.openConnection() as HttpURLConnection
                            urlConnection.connectTimeout = 7000
                            val content = urlConnection.inputStream.bufferedReader().use(BufferedReader::readText)
                            val json = JSONObject(content)
                            uiThread {
                                if (json.has("erro")) {
                                    tiCEP?.error = "erro"
                                } else {
                                    if (json.getString("uf").isNotEmpty()
                                        && json.getString("localidade").isNotEmpty()) {
                                        etRua.setText(json.getString("logradouro"))
                                        etBairro.setText(json.getString("bairro"))
                                        tvCidEst.text = "${json.getString("localidade")}/${json.getString("uf")}"
                                        cdCidEst.visibility = View.VISIBLE
                                        etNumero.requestFocus()
                                    } else {
                                        tiCEP?.error = getString(R.string.cep_invalido)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        etSenhaConfirma.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    Utilitarios.limpaErroCampo(etSenhaConfirma, tiSenhaConfirma)

                    if (etSenhaConfirma.text.toString().isEmpty()) {
                        tiSenhaConfirma?.error = getString(R.string.campo_obrigatorio)
                    } else if (etSenhaConfirma.text.toString() != etSenha.text.toString()) {
                        tiSenhaConfirma?.error = getString(R.string.senha_erro_confirma)
                    }
                }
            }

        cepMascara()
    }

    private fun salva() {
        btSave.setOnClickListener {
            var erro = false
            erro = campoSalva(etNome, tiNome)
            erro = campoSalva(etRua, tiRua)
            erro = campoSalva(etNumero, tiNumero)
            erro = campoSalva(etSenha, tiSenha)
            erro = campoSalva(etCEP, tiCEP)
            erro = campoSalva(etBairro, tiBairro)

            if (etEmail.text.toString().isEmpty()) {
                Utilitarios.limpaErroCampo(etEmail, tiEmail)
                tiEmail?.error = getString(R.string.campo_obrigatorio)
                erro = true
            } else if (!etEmail.text.toString().contains("@") ||
                !etEmail.text.toString().contains(".")) {
                Utilitarios.limpaErroCampo(etEmail, tiEmail)
                tiEmail?.error = getString(R.string.email_invalido)
                erro = true
            }

            if (etSenhaConfirma.text.toString().isEmpty()) {
                Utilitarios.limpaErroCampo(etSenhaConfirma, tiSenhaConfirma)
                tiSenhaConfirma?.error = getString(R.string.campo_obrigatorio)
                erro = true
            } else if (etSenhaConfirma.text.toString() != etSenha.text.toString()) {
                Utilitarios.limpaErroCampo(etSenhaConfirma, tiSenhaConfirma)
                tiSenhaConfirma?.error = getString(R.string.cep_invalido)
                erro = true
            }
        }
    }

    private fun cepMascara() {
        etCEP?.addTextChangedListener(object : TextWatcher {
            var text = ""
            var oldText = ""

            override fun afterTextChanged(s: Editable) {
                if (s.toString().length > 5
                    && !s.toString().contains("-")
                    && (s.toString().length > oldText.length
                            || (oldText.contains("-")
                                && !s.toString().contains("-")))
                ) {
                    text = s.toString().substring(0, 5) + "-" + s.toString().substring(5)
                    etCEP.setText(text)
                    etCEP.setSelection(text.length)
                }

                oldText = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun campoSalva(etCampo: EditText?, tiCampo: TextInputLayout?): Boolean {
        if (etCampo?.text.toString().isEmpty()) {
            Utilitarios.limpaErroCampo(etCampo, tiCampo)
            tiCampo?.error = getString(R.string.campo_obrigatorio)
            return true
        }

        return false
    }

    private fun initDrawerLayoutAndNavigation() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        val headerLayout = nav_view.getHeaderView(0)
        val entrar = headerLayout.findViewById<Button>(R.id.btEntrar)

        if (entrar.visibility == View.VISIBLE) {
            entrar.setOnClickListener {
                drawer_layout.closeDrawer(GravityCompat.START)
                Utilitarios.login(this)
            }

            etTitulo.text = getString(R.string.nova_conta)
            tiSenha.visibility = View.VISIBLE
            tiSenhaConfirma.visibility = View.VISIBLE
            btSave.visibility = View.VISIBLE
        }

        nav_view.menu.findItem(R.id.nav_conta).isVisible = false
    }

    override fun onCreateOptionsMenu(
        menu: Menu?
    ): Boolean {
        menuInflater.inflate(
            R.menu.activity_conta_toolbar,
            menu
        )

        if (etTitulo.text == getString(R.string.nova_conta)) {
            menu?.findItem(R.id.itEdit)?.isVisible = false
        }

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {
        return when (item.itemId) {
            R.id.itEdit -> {

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}