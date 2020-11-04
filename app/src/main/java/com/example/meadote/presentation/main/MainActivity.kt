package com.example.meadote.presentation.main

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.meadote.R
import com.example.meadote.presentation.conta.ContaActivity
import com.example.meadote.util.Utilitarios
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var buscaMenu: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initDrawerLayoutAndNavigation()
    }

    override fun onStart() {
        super.onStart()
        etPesquisa.setOnClickListener { search() }
    }

    private fun search() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_busca, null)

        val pesquisa = view.findViewById<EditText>(R.id.etPesquisa)
        val back = view.findViewById<ImageView>(R.id.ivBack)
        val close = view.findViewById<ImageView>(R.id.ivClose)

        if (etPesquisa.text.toString().isNotEmpty()) {
            close.visibility = View.VISIBLE
        }

        pesquisa.setText(etPesquisa.text.toString())
        pesquisa.onFocusChangeListener =
            OnFocusChangeListener { v, hasFocus ->
                pesquisa.post {
                    val inputMethodManager: InputMethodManager =
                        this@MainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(
                        pesquisa,
                        InputMethodManager.SHOW_IMPLICIT
                    )
                }
            }
        pesquisa.requestFocus()

        builder.setView(view)

        val alert = builder.create()
        alert.show()
        alert.getWindow()!!.setGravity(Gravity.TOP)
        alert.getWindow()!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        close.setOnClickListener {
            etPesquisa.text = null
            buscaMenu.setIcon(R.drawable.ic_baseline_search_24)
            etPesquisa.clearFocus()
            alert.hide()
        }

        pesquisa.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                etPesquisa.setText(pesquisa.text.toString())
                etPesquisa.clearFocus()

                if (pesquisa.text.toString().isNotEmpty()) {
                    buscaMenu.setIcon(R.drawable.ic_baseline_close_24)
                } else {
                    buscaMenu.setIcon(R.drawable.ic_baseline_search_24)
                }

                alert.hide()
            }
            false
        }

        back.setOnClickListener {
            etPesquisa.clearFocus()
            alert.hide()
        }
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
        val nome = headerLayout.findViewById<TextView>(R.id.tvNome)
        val infos = headerLayout.findViewById<LinearLayout>(R.id.liInfoConta)
        val email = headerLayout.findViewById<TextView>(R.id.tvEmail)
        val endereco = headerLayout.findViewById<TextView>(R.id.tvEndereco)

        if (Utilitarios.consultaString(this, "email")!!.isNotEmpty()) {
            nome.text = Utilitarios.consultaString(this, "nome")
            email.text = Utilitarios.consultaString(this, "email")

            val end = Utilitarios.consultaString(this, "rua") +
                    ", " +
                    Utilitarios.consultaString(this, "numero")

            endereco.text = end

            entrar.visibility = View.GONE
            infos.visibility = View.VISIBLE
        } else {
            entrar.setOnClickListener {
                drawer_layout.closeDrawer(GravityCompat.START)
                Utilitarios.login(this)
            }
            nav_view.menu.findItem(R.id.nav_conta).isVisible = false
        }

        nav_view.menu.findItem(R.id.nav_home).isVisible = false
    }

    override fun onCreateOptionsMenu(
        menu: Menu?
    ): Boolean {
        menuInflater.inflate(
            R.menu.activity_main_toolbar,
            menu
        )

        buscaMenu = menu!!.findItem(R.id.itBusca)

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
      when (item.itemId) {
            R.id.nav_conta -> {
                val intent = Intent(this, ContaActivity::class.java)
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
            R.id.itBusca -> {
                search()
                true
            }
            R.id.itFiltro -> {
                val builder = AlertDialog.Builder(this)
                val inflater = LayoutInflater.from(this)
                val view = inflater.inflate(R.layout.dialog_filtro, null)

                builder.setView(view)

                val alert = builder.create()
                alert.show()
                alert.getWindow()!!.setGravity(Gravity.TOP)
                alert.getWindow()!!.setBackgroundDrawableResource(R.drawable.bg_filtro_round)
                alert.getWindow()!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}