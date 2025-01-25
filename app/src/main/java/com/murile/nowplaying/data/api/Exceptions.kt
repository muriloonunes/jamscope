package com.murile.nowplaying.data.api

import android.content.Context
import com.murile.nowplaying.R

class Exceptions(
    private val context: Context
) {
    fun handleError(code: Int): String {
        val errorMessage = when (code) {
            403 -> context.getString(R.string.invalid_user_password)
            429 -> context.getString(R.string.too_many_requests)
            500 -> context.getString(R.string.server_error)
            666 -> context.getString(R.string.connection_error)
            else -> context.getString(R.string.unknown_error)
        }
        return errorMessage
    }
}