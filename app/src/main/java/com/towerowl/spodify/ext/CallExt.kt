package com.towerowl.spodify.ext

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> Call<T>.enqueue(onResponse: (T) -> Unit, onFailure: (Exception) -> Unit) {
    try {
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                response.body()?.run(onResponse) ?: onFailure(Exception("Missing body"))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                onFailure(Exception(t))
            }
        })
    } catch (e: Exception) {
        onFailure(e)
    }
}