package com.mno.jamscope.data.remote.api

import android.content.Context
import com.mno.jamscope.R
import javax.inject.Inject

class Exceptions @Inject constructor(
    private val context: Context
) {
    fun handleError(code: Int): String {
        val errorMessage = when (code) {
            6 -> context.getString(R.string.no_friends)
            17 -> context.getString(R.string.login_required_error)
            403 -> context.getString(R.string.invalid_user_password)
            429 -> context.getString(R.string.too_many_requests)
            500 -> context.getString(R.string.server_error)
            666 -> context.getString(R.string.connection_error)
            999 -> context.getString(R.string.fail_fetch_friend)
            else -> context.getString(R.string.unknown_error)
        }
        return errorMessage
    }
}
//TODO mover todas as funcoes que extendem Context pra um arquivo a parte
fun Context.handleError(code: Int): String {
    val errorMessage = when (code) {
        6 -> getString(R.string.no_friends)
        17 -> getString(R.string.login_required_error)
        403 -> getString(R.string.invalid_user_password)
        429 -> getString(R.string.too_many_requests)
        500 -> getString(R.string.server_error)
        666 -> getString(R.string.connection_error)
        999 -> getString(R.string.fail_fetch_friend)
        else -> getString(R.string.unknown_error)
    }
    return errorMessage
}