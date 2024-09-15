package app.cardnest.utils.extensions

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun ByteArray.toEncoded() = Base64.encode(this)

@OptIn(ExperimentalEncodingApi::class)
fun String.toDecoded() = Base64.decode(this)
