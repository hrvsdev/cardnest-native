package app.cardnest.firebase

import com.google.firebase.Firebase
import com.google.firebase.database.database

val rtDb = Firebase.database.also {
  it.setPersistenceEnabled(true)
}
