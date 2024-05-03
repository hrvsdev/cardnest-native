package com.hrvs.cardnest

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      App()
    }
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
  App()
}
