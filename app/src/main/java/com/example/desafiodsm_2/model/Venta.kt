package com.example.desafiodsm_2.model

data class Venta(
    var id: String = "",
    var clienteId: String = "",
    var productos: Map<String, Int> = emptyMap(),
    var total: Double = 0.0,
    var fecha: String = ""
)
