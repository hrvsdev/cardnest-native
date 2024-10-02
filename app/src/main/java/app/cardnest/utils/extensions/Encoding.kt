package app.cardnest.utils.extensions

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
val ByteArray.encoded get() = Base64.encode(this)

@OptIn(ExperimentalEncodingApi::class)
val String.decoded get() = Base64.decode(this)
