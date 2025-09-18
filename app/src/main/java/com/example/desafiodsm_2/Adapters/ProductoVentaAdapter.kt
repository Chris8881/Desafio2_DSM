package com.example.desafiodsm_2.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiodsm_2.R
import com.example.desafiodsm_2.model.Producto

class ProductoVentaAdapter(
    private val productos: MutableList<Producto>,
    private val onCantidadChange: (Producto, Int, Boolean) -> Unit
) : RecyclerView.Adapter<ProductoVentaAdapter.ProductoVentaViewHolder>() {

    class ProductoVentaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreVenta)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecioVenta)
        val tvStock: TextView = itemView.findViewById(R.id.tvStockVenta)
        val cbSeleccionar: CheckBox = itemView.findViewById(R.id.cbSeleccionar)
        val etCantidad: EditText = itemView.findViewById(R.id.etCantidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoVentaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_venta, parent, false)
        return ProductoVentaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoVentaViewHolder, position: Int) {
        val producto = productos[position]
        holder.tvNombre.text = producto.nombre
        holder.tvPrecio.text = "Precio: $${producto.precio}"
        holder.tvStock.text = "Stock: ${producto.stock}"

        // Evento al seleccionar/deseleccionar
        holder.cbSeleccionar.setOnCheckedChangeListener { _, isChecked ->
            val cantidad = holder.etCantidad.text.toString().toIntOrNull() ?: 0
            onCantidadChange(producto, cantidad, isChecked)
        }

        // Evento al cambiar cantidad (solo aplica si está seleccionado)
        holder.etCantidad.setOnFocusChangeListener { _, _ ->
            if (holder.cbSeleccionar.isChecked) {
                val cantidad = holder.etCantidad.text.toString().toIntOrNull() ?: 0
                onCantidadChange(producto, cantidad, true)
            }
        }
    }

    override fun getItemCount(): Int = productos.size
}
