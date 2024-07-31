package com.example.gifsearchappproject

import android.annotation.SuppressLint
import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class MySingleton private constructor(private val ctx: Context) {
    private var requestQueue: RequestQueue? = null

    init {
        requestQueue = getRequestQueue()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: MySingleton? = null

        fun getInstance(context: Context): MySingleton {
            return instance ?: synchronized(this) {
                instance ?: MySingleton(context).also { instance = it }
            }
        }
    }

    private fun getRequestQueue(): RequestQueue {
        return requestQueue ?: synchronized(this) {
            requestQueue ?: Volley.newRequestQueue(ctx.applicationContext).also { requestQueue = it }
        }
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        getRequestQueue().add(req)
    }
}

