package com.cgc.net

interface ResponseHandle<RESPONSE> {
    fun onError(type: Int, msg: String?)
    fun onSuccess(type: Int, result: RESPONSE)
}