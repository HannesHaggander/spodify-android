package com.towerowl.spodify.ext

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> Call<T>.enqueue(
    onResponse: (Call<T>, Response<T>) -> Unit,
    onFailure: (Call<T>, Exception) -> Unit
) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            onResponse(call, response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure(call, Exception(t))
        }
    })
}