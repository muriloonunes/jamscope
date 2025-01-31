package com.murile.nowplaying.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.murile.nowplaying.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun dateFormatter(dataIso: String): String {
    val dataFormatada = ZonedDateTime.parse(dataIso)
    val currentTime = ZonedDateTime.now()
    val duration = java.time.Duration.between(dataFormatada, currentTime)

    return when {
        //menos de  1hora
        duration.abs().toMinutes() < 60 -> stringResource(
            R.string.minutes_ago,
            duration.abs().toMinutes()
        )
        //entre 1 e 2 horas
        duration.toMinutes() in 60 until 120 -> stringResource(R.string.one_hour_ago)
        // menos de 24h
        duration.toMinutes() in 120 until 1440 -> {
            val hours = duration.toHours()
            stringResource(R.string.hours_ago, hours)
        }
        // entr 1 e 2 dias
        duration.toDays() in 1 until 2 -> stringResource(R.string.one_day_ago)
        // menos de 1 mes
        duration.toDays() in 2 until 30 -> {
            val days = duration.toDays()
            stringResource(R.string.days_ago, days)
        }

        else -> {
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
            val formattedDate = dataFormatada.format(formatter)
            formattedDate
        }
    }
}