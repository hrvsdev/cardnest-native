package com.hrvs.cardnest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.tooling.preview.Preview
import com.hrvs.cardnest.ui.theme.CardNestTheme
import com.hrvs.cardnest.ui.theme.TH_BLACK
import com.hrvs.cardnest.ui.theme.TH_DARKER_BLUE
import com.hrvs.cardnest.ui.theme.TH_WHITE

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			App()
		}
	}
}

@Composable
fun App() {
	CardNestTheme {
		Surface(modifier = Modifier.fillMaxSize(), color = TH_BLACK) {
			LinearGradientShader(
				from = Offset(0f, 0f),
				to = Offset(100f, 100f),
				colors = listOf(TH_BLACK, TH_DARKER_BLUE),
			)
			Text(text = "Hello, Harsh Vyas!", color = TH_WHITE)
		}
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
	App()
}
