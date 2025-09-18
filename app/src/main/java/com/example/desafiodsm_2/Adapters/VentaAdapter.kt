package com.example.desafiodsm_2.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiodsm_2.R
import com.example.desafiodsm_2.model.Venta

class VentaAdapter(
    private val lista: MutableList<Venta>
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
        val venta = lista[position]

        // 🔹 Mostrar cliente (en este caso clienteId, pero puedes cambiarlo por el nombre real si lo traes)
        holder.tvCliente.text = "Cliente: ${venta.clienteId}"

        // 🔹 Mostrar fecha
        holder.tvFecha.text = "Fecha: ${venta.fecha}"

        // 🔹 Mostrar total
        holder.tvTotal.text = "Total: $${venta.total}"
    }

    override fun getItemCount(): Int = lista.size
}
