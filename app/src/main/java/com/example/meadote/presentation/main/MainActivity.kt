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
import androidx.drawerlayout.widget.DrawerLayout
import com.example.meadote.R
import com.example.meadote.presentation.conta.ContaActivity
import com.example.meadote.util.Login
import com.example.meadote.util.Utilitarios
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_busca.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.w3c.dom.Text

class MainActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    lateinit var buscaMenu: MenuItem
    lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initDrawerLayoutAndNavigation()
        ctx = this
    }

    override fun onStart() {
        super.onStart()
        etPesquisa.setOnClickListener { search() }
    }

    private fun search() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_busca, null)

        val pesquisa = view.etPesquisa
        val back = view.ivBack
        val close = view.ivClose

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

        nav_view.setNavigationItemSelectedListener(this)
        val headerLayout = nav_view.getHeaderView(0)
        val entrar = headerLayout.btEntrar
        val nome = headerLayout.tvNome
        val infos = headerLayout.liInfoConta
        val email = headerLayout.tvEmail
        val endereco = headerLayout.tvEndereco

        drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {
                if (Utilitarios.consultaString(ctx, "email")!!.isNotEmpty()) {
                    nome.text = Utilitarios.consultaString(ctx, "nome")
                    email.text = Utilitarios.consultaString(ctx, "email")

                    val end = Utilitarios.consultaString(ctx, "rua") +
                            ", " +
                            Utilitarios.consultaString(ctx, "numero")

                    endereco.text = end

                    entrar.visibility = View.GONE
                    infos.visibility = View.VISIBLE
                    nav_view.menu.findItem(R.id.nav_conta).isVisible = true
                } else {
                    nav_view.menu.findItem(R.id.nav_conta).isVisible = false
                }
            }

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {}
        })

        toggle.syncState()

        entrar.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
            Login.doLogin(this)
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