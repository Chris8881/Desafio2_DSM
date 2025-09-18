package com.example.desafiodsm_2.model

data class Producto(
    var id: String = "",
    var nombre: String = "",
    var descripcion: String = "",
    var precio: Double = 0.0,
    var stock: Int = 0
)
