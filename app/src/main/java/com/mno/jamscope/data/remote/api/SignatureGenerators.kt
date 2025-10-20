package com.mno.jamscope.data.remote.api

import com.mno.jamscope.util.Stuff
import java.security.MessageDigest

fun generateMobileApiSig(
    username: String,
    password: String,
    method: String,
): String {
    val apiSig = "api_key${Stuff.LAST_KEY}" +
            "method$method" +
            "password$password" +
            "username$username" +
            Stuff.LAST_SECRET

    return md5Hash(apiSig)
}

fun generateWebApiSig(
    token: String,
    method: String,
): String {
    val apiSignature =
        "api_key${Stuff.LAST_KEY}" +
                "method$method" +
                "token$token" +
                Stuff.LAST_SECRET

    return md5Hash(apiSignature)
}

private fun md5Hash(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(input.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}