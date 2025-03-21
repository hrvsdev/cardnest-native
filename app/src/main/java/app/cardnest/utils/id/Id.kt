package app.cardnest.utils.id

import io.viascom.nanoid.NanoId

fun genId(): String {
  return NanoId.generate(size = 8, alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789")
}
