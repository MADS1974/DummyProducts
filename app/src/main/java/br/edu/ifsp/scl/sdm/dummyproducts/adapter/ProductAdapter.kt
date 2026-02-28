package br.edu.ifsp.scl.sdm.dummyproducts.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import br.edu.ifsp.scl.sdm.dummyproducts.model.Product

class ProductAdapter(
    private val activityContext: Context,
    private val productList: MutableList<Product>
):  ArrayAdapter<Product>(activityContext,  android.R.layout.simple_list_item_1, productList) {
    private data class ProductsHolder(val productTitleTv: TextView)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val productView = convertView ?: LayoutInflater.from(activityContext)
            .inflate(android.R.layout.simple_list_item_1, parent, false).apply {
                tag = ProductsHolder(findViewById(android.R.id.text1))
            }

        (productView.tag as ProductsHolder).productTitleTv.text = productList[position].title

        return productView

    }
}