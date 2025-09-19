package com.example.desafiodsm_2.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiodsm_2.R
import com.example.desafiodsm_2.model.Cliente
import com.example.desafiodsm_2.model.Venta

class VentaAdapter(
    private val listaVentas: MutableList<Venta>,
    private val listaClientes: MutableList<Cliente>
) : RecyclerView.Adapter<VentaAdapter.VentaViewHolder>() {

    class VentaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCliente: TextView = itemView.findViewById(R.id.tvClienteVenta)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFechaVenta)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotalVenta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_venta, parent, false)
        return VentaViewHolder(view)
    }

    override fun onBindViewHolder(holder: VentaViewHolder, position: Int) {
        val venta = listaVentas[position]

        // Buscar cliente relacionado
        val cliente = listaClientes.find { it.id == venta.clienteId }

        // Mostrar nombre si existe, de lo contrario mostrar el ID
        holder.tvCliente.text = "Cliente: ${cliente?.nombre ?: "ID ${venta.clienteId}"}"
        holder.tvFecha.text = "Fecha: ${venta.fecha}"
        holder.tvTotal.text = "Total: $${venta.total}"
    }

    override fun getItemCount(): Int = listaVentas.size
}
