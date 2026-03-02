package br.edu.ifsp.scl.sdm.dummyproducts.ui

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.sdm.dummyproducts.R
import br.edu.ifsp.scl.sdm.dummyproducts.adapter.ProductAdapter
import br.edu.ifsp.scl.sdm.dummyproducts.adapter.ProductImageAdapter
import br.edu.ifsp.scl.sdm.dummyproducts.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.dummyproducts.model.Product
import br.edu.ifsp.scl.sdm.dummyproducts.model.ProductList
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.URL
import kotlin.collections.forEach
import java.io.BufferedInputStream
import android.graphics.BitmapFactory
import android.widget.ImageView
import br.edu.ifsp.scl.sdm.dummyproducts.model.DummyJSONAPI
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest


class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val productList: MutableList<Product> = mutableListOf()
    private val productAdapter: ProductAdapter by lazy {
        ProductAdapter(this, productList)
    }
    private val productImageList: MutableList<Bitmap> = mutableListOf()
    private val productImageAdapter: ProductImageAdapter by lazy {
        ProductImageAdapter(this, productImageList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.mainTb.apply {
            title = "DummyProducts"
        })

        amb.productsSp.apply {
            adapter = productAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val product = productList[position]

                    // --- EXERCÍCIO DE FIXAÇÃO: ATUALIZANDO A INTERFACE ---
                    amb.productPriceTv.text = "Preço: $${product.price}"
                    amb.productDescriptionTv.text = product.description
                    // -----------------------------------------------------

                    val size = productImageList.size
                    productImageList.clear()
                    productImageAdapter.notifyItemRangeRemoved(0, size)
                    retrieveProductImages(product)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // NSA
                }
            }
        }

        amb.productImagesRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = productImageAdapter
        }

        retrieveProducts()
    }

    private fun retrieveProducts() =
        DummyJSONAPI.ProductListRequest({ productListResponse ->
            productListResponse.products.also {
                productAdapter.addAll(it)
            }
        }, {
            Toast.makeText(this, "Request problem.", Toast.LENGTH_SHORT).show()
        }).also {
            DummyJSONAPI.getInstance(this).addToRequestQueue(it)
        }

    private fun retrieveProductImages(product: Product) =
        product.images.forEach { imageUrl ->
            ImageRequest(
                imageUrl,
                { response ->
                    productImageList.add(response)
                    productImageAdapter.notifyItemInserted(productImageList.lastIndex)
                },
                0, 0,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.ARGB_8888,
                {
                    Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
                }
            ).also {
                DummyJSONAPI.getInstance(this).addToRequestQueue(it)
            }
        }
}