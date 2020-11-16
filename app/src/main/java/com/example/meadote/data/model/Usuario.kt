package com.example.meadote.data.model

class Usuario (val nome: String,
               val id: String,
               val email: String,
               val cep: String,
               val bairro: String,
               val rua: String,
               val numero: String,
               val complemento: String,
               val cidade: String,
               val estado: String,
               val foto: String) {

    constructor() : this ("", "", "", "", "", "", "", "", "", "", "") {

    }

}