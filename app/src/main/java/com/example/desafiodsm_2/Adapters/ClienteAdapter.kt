package com.example.desafiodsm_2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiodsm_2.R
import com.example.desafiodsm_2.model.Cliente

class ClienteAdapter(
    private val lista: MutableList<Cliente>,
    private val onEdit: (Cliente) -> Unit,
    private val onDelete: (Cliente) -> Unit
) : RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>() {

    class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombreCliente)
        val correo: TextView = itemView.findViewById(R.id.tvCorreoCliente)
        val telefono: TextView = itemView.findViewById(R.id.tvTelefonoCliente)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditarCliente)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminarCliente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente, parent, false)
        return ClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val cliente = lista[position]
        holder.nombre.text = cliente.nombre
        holder.correo.text = cliente.correo
        holder.telefono.text = cliente.telefono

        holder.btnEditar.setOnClickListener { onEdit(cliente) }
        holder.btnEliminar.setOnClickListener { onDelete(cliente) }
    }

    override fun getItemCount(): Int = lista.size
}
