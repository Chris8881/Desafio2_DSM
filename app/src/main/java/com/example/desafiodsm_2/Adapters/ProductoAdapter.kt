package com.example.desafiodsm_2.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiodsm_2.R
import com.example.desafiodsm_2.model.Producto

class ProductoAdapter(
    private val lista: MutableList<Producto>,
    private val onEdit: (Producto) -> Unit,
    private val onDelete: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        val precio: TextView = itemView.findViewById(R.id.tvPrecio)
        val stock: TextView = itemView.findViewById(R.id.tvStock)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = lista[position]
        holder.nombre.text = producto.nombre
        holder.precio.text = "Precio: $${producto.precio}"
        holder.stock.text = "Stock: ${producto.stock}"

        holder.btnEditar.setOnClickListener { onEdit(producto) }
        holder.btnEliminar.setOnClickListener { onDelete(producto) }
    }

    override fun getItemCount(): Int = lista.size
}
