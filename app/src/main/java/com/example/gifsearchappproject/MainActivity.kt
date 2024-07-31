package com.example.gifsearchappproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException

class MainActivity : AppCompatActivity(), DataAdapter.OnItemClickListener {
    private lateinit var rView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var errorMessage: TextView
    private var searchJob: Job? = null

    private var dataModelArrayList: ArrayList<DataModel> = ArrayList()
    private lateinit var dataAdapter: DataAdapter

    private val API_KEY = "4P6voGNkcVYpEJevWwN9QlmBDa1l4FRB"
    private val BASE_URL = "https://api.giphy.com/v1/gifs/trending?api_key="
    private val SEARCH_URL = "https://api.giphy.com/v1/gifs/search?api_key="
    private val url = BASE_URL + API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rView = findViewById(R.id.recyclerView)
        searchBar = findViewById(R.id.search_bar)
        loadingIndicator = findViewById(R.id.loading_indicator)
        errorMessage = findViewById(R.id.error_message)

        rView.layoutManager = GridLayoutManager(this, 2)
        rView.addItemDecoration(SpaceItemDecoration(10))
        rView.setHasFixedSize(true)

        searchBar.addTextChangedListener { text ->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                text?.let {
                    delay(300)  // Delay for 300ms
                    performSearch(it.toString())
                }
            }
        }

        searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP)) {
                // Hide the keyboard
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                // Perform search
                performSearch(searchBar.text.toString())
                true
            } else {
                false
            }
        }

        fetchTrendingGifs()
    }

    private fun fetchTrendingGifs() {
        showLoadingIndicator()
        val objectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                hideLoadingIndicator()
                try {
                    val dataArray = response.getJSONArray("data")
                    dataModelArrayList.clear()

                    for (i in 0 until dataArray.length()) {
                        val obj = dataArray.getJSONObject(i)
                        val obj1 = obj.getJSONObject("images")
                        val obj2 = obj1.getJSONObject("downsized_medium")
                        val sourceUrl = obj2.getString("url")

                        dataModelArrayList.add(DataModel(sourceUrl))
                    }

                    dataAdapter = DataAdapter(this, dataModelArrayList)
                    rView.adapter = dataAdapter
                    dataAdapter.setOnItemClickListener(this)

                } catch (e: JSONException) {
                    showError("Failed to parse response")
                }
            },
            { error: VolleyError ->
                showError("Error: ${error.message}")
            })

        MySingleton.getInstance(this).addToRequestQueue(objectRequest)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            fetchTrendingGifs()
            return
        }

        showLoadingIndicator()
        val searchUrl = "$SEARCH_URL$API_KEY&q=$query"
        val searchRequest = JsonObjectRequest(Request.Method.GET, searchUrl, null,
            { response ->
                hideLoadingIndicator()
                try {
                    val dataArray = response.getJSONArray("data")
                    dataModelArrayList.clear()  // Clear the existing data

                    for (i in 0 until dataArray.length()) {
                        val obj = dataArray.getJSONObject(i)
                        val obj1 = obj.getJSONObject("images")
                        val obj2 = obj1.getJSONObject("downsized_medium")
                        val sourceUrl = obj2.getString("url")

                        dataModelArrayList.add(DataModel(sourceUrl))
                    }

                    dataAdapter.notifyDataSetChanged()

                } catch (e: JSONException) {
                    showError("Failed to parse response")
                }
            },
            { error: VolleyError ->
                showError("Error: ${error.message}")
            })

        MySingleton.getInstance(this).addToRequestQueue(searchRequest)
    }


    private fun showLoadingIndicator() {
        loadingIndicator.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        loadingIndicator.visibility = View.GONE
    }

    private fun showError(message: String?) {
        errorMessage.text = message ?: "An error occurred"
        errorMessage.visibility = View.VISIBLE
        hideLoadingIndicator()
    }

    private fun hideError() {
        errorMessage.visibility = View.GONE
    }

    override fun onItemClick(pos: Int) {
        val fullView = Intent(this, FullActivity::class.java)
        val clickedItem = dataModelArrayList[pos]
        fullView.putExtra("imageUrl", clickedItem.imageUrl)
        startActivity(fullView)
    }
}
