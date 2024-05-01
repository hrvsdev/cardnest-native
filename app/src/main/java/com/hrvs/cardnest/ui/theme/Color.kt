package com.hrvs.cardnest.ui.theme

import androidx.compose.ui.graphics.Color

val TH_BLACK = Color(0xFF00060C)
val TH_BLACK_00 = Color(0x0000060C)
val TH_BLACK_20 = Color(0x3300060C)
val TH_BLACK_40 = Color(0x6600060C)
val TH_BLACK_80 = Color(0xCC00060C)

val TH_DARKER_BLUE = Color(0xFF001528)

val TH_SKY = Color(0xFF3FA1FF)
val TH_SKY_10 = Color(0x193FA1FF)
val TH_SKY_20 = Color(0x333FA1FF)

val TH_RED = Color(0xFFF31260)
val TH_RED_10 = Color(0x19F31260)
val TH_RED_12 = Color(0x1FF31260)
val TH_RED_15 = Color(0x26F31260)
val TH_RED_60 = Color(0x99F31260)

val TH_WHITE = Color(0xFFF4F7FB)
val TH_WHITE_00 = Color(0x00F4F7FB)
val TH_WHITE_05 = Color(0x0DF4F7FB)
val TH_WHITE_07 = Color(0x12F4F7FB)
val TH_WHITE_10 = Color(0x19F4F7FB)
val TH_WHITE_20 = Color(0x33F4F7FB)
val TH_WHITE_50 = Color(0x7FF4F7FB)
val TH_WHITE_60 = Color(0x99F4F7FB)
val TH_WHITE_65 = Color(0xA6F4F7FB)
val TH_WHITE_70 = Color(0xB2F4F7FB)
val TH_WHITE_80 = Color(0xCCF4F7FB)
val TH_WHITE_85 = Color(0xD9F4F7FB)
val TH_WHITE_90 = Color(0xE6F4F7FB)

val CARD_RED_500 = Color(0xFFEF4444)
val CARD_RED_700 = Color(0xFFB91C1C)

val CARD_ORANGE_500 = Color(0xFFF97316)
val CARD_ORANGE_700 = Color(0xFFC2410C)

val CARD_YELLOW_500 = Color(0xFFEAB308)
val CARD_YELLOW_700 = Color(0xFFA16207)

val CARD_GREEN_500 = Color(0xFF22C55E)
val CARD_GREEN_700 = Color(0xFF15803D)

val CARD_EMERALD_500 = Color(0xFF10B981)
val CARD_EMERALD_700 = Color(0xFF047857)

val CARD_TEAL_500 = Color(0xFF14B8A6)
val CARD_TEAL_700 = Color(0xFF0F766E)

val CARD_CYAN_500 = Color(0xFF06B6D4)
val CARD_CYAN_700 = Color(0xFF0E7490)

val CARD_SKY_500 = Color(0xFF0EA5E9)
val CARD_SKY_700 = Color(0xFF0369A1)

val CARD_BLUE_500 = Color(0xFF3B82F6)
val CARD_BLUE_700 = Color(0xFF1D4ED8)

val CARD_INDIGO_500 = Color(0xFF6366F1)
val CARD_INDIGO_700 = Color(0xFF4338CA)

val CARD_VIOLET_500 = Color(0xFF8B5CF6)
val CARD_VIOLET_700 = Color(0xFF6D28D9)

val CARD_PURPLE_500 = Color(0xFFA855F7)
val CARD_PURPLE_700 = Color(0xFF7E22CE)

val CARD_FUCHSIA_500 = Color(0xFFD946EF)
val CARD_FUCHSIA_700 = Color(0xFFA21CAF)

val CARD_PINK_500 = Color(0xFFEC4899)
val CARD_PINK_700 = Color(0xFFBE185D)

val CARD_ROSE_500 = Color(0xFFF43F5E)
val CARD_ROSE_700 = Color(0xFFBE123C)

fun getCardTheme(color: CardColor) = when (color) {
  CardColor.RED -> listOf(CARD_RED_500, CARD_RED_700)
  CardColor.ORANGE -> listOf(CARD_ORANGE_500, CARD_ORANGE_700)
  CardColor.YELLOW -> listOf(CARD_YELLOW_500, CARD_YELLOW_700)
  CardColor.GREEN -> listOf(CARD_GREEN_500, CARD_GREEN_700)
  CardColor.EMERALD -> listOf(CARD_EMERALD_500, CARD_EMERALD_700)
  CardColor.TEAL -> listOf(CARD_TEAL_500, CARD_TEAL_700)
  CardColor.CYAN -> listOf(CARD_CYAN_500, CARD_CYAN_700)
  CardColor.SKY -> listOf(CARD_SKY_500, CARD_SKY_700)
  CardColor.BLUE -> listOf(CARD_BLUE_500, CARD_BLUE_700)
  CardColor.INDIGO -> listOf(CARD_INDIGO_500, CARD_INDIGO_700)
  CardColor.VIOLET -> listOf(CARD_VIOLET_500, CARD_VIOLET_700)
  CardColor.PURPLE -> listOf(CARD_PURPLE_500, CARD_PURPLE_700)
  CardColor.FUCHSIA -> listOf(CARD_FUCHSIA_500, CARD_FUCHSIA_700)
  CardColor.PINK -> listOf(CARD_PINK_500, CARD_PINK_700)
  CardColor.ROSE -> listOf(CARD_ROSE_500, CARD_ROSE_700)
}

enum class CardColor {
  RED,
  ORANGE,
  YELLOW,
  GREEN,
  EMERALD,
  TEAL,
  CYAN,
  SKY,
  BLUE,
  INDIGO,
  VIOLET,
  PURPLE,
  FUCHSIA,
  PINK,
  ROSE,
}
