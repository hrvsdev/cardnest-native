package app.cardnest.components.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_BLACK
import app.cardnest.ui.theme.TH_GREEN
import app.cardnest.ui.theme.TH_RED_70
import app.cardnest.ui.theme.TH_WHITE_20
import app.cardnest.ui.theme.TH_YELLOW_70

@Composable
fun PasswordInfo(text: String, type: PasswordInfoType, isVisible: Boolean) {
  val enter = remember { fadeIn() + expandVertically() }
  val exit = remember { fadeOut() + shrinkVertically() }

  AnimatedVisibility(isVisible, enter = enter, exit = exit) {
    Row {
      Box(Modifier.height(20.dp), Alignment.Center) {
        Box(Modifier.padding(start = 8.dp, end = 6.dp).size(12.dp).background(type.color, CircleShape), Alignment.Center) {
          Icon(
            painter = painterResource(type.icon),
            contentDescription = "",
            modifier = Modifier.size(10.dp),
            tint = TH_BLACK
          )
        }
      }

      AppText(text, size = AppTextSize.SM, color = type.color)
    }
  }
}

enum class PasswordInfoType(val color: Color, val icon: Int) {
  SUCCESS(TH_GREEN, R.drawable.tabler__check),
  ERROR(TH_RED_70, R.drawable.tabler__x),
  WARN(TH_YELLOW_70, R.drawable.tabler__exclamation_mark),
  DONE(TH_WHITE_20, R.drawable.tabler__check)
}
