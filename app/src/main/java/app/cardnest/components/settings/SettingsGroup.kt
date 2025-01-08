package app.cardnest.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_WHITE_60

@Composable
fun SettingsGroup(title: String? = null, description: String? = null, content: @Composable ColumnScope.() -> Unit) {
  Column {
    if (title != null) {
      AppText(
        text = title.uppercase(),
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
        size = AppTextSize.XS,
        color = TH_WHITE_60
      )
    }

    Column(verticalArrangement = Arrangement.spacedBy(2.dp), content = content)

    if (description != null) {
      AppText(
        text = description,
        modifier = Modifier.padding(start = 8.dp, top = 8.dp),
        size = AppTextSize.SM,
        color = TH_WHITE_60
      )
    }
  }
}
