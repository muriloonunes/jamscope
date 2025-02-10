package com.murile.nowplaying.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.murile.nowplaying.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun dateStringFormatter(dataIso: String, forWidget: Boolean, context: Context?): String {
    val dataFormatada = ZonedDateTime.parse(dataIso)
    val currentTime = ZonedDateTime.now()
    val duration = java.time.Duration.between(dataFormatada, currentTime)

    return when {
        //menos de  1hora
        duration.abs().toMinutes() < 60 -> if (forWidget) context!!.getString(
            R.string.minutes_ago,
            duration.abs().toMinutes()
        ) else stringResource(
            R.string.minutes_ago,
            duration.abs().toMinutes()
        )
        //entre 1 e 2 horas
        duration.toMinutes() in 60 until 120 -> if (forWidget) context!!.getString(R.string.one_hour_ago) else stringResource(
            R.string.one_hour_ago
        )
        // menos de 24h
        duration.toMinutes() in 120 until 1440 -> {
            val hours = duration.toHours()
            if (forWidget) context!!.getString(
                R.string.hours_ago,
                hours
            ) else stringResource(R.string.hours_ago, hours)
        }
        // entr 1 e 2 dias
        duration.toDays() in 1 until 2 -> if (forWidget) context!!.getString(R.string.one_day_ago) else stringResource(
            R.string.one_day_ago
        )
        // menos de 1 mes
        duration.toDays() in 2 until 30 -> {
            val days = duration.toDays()
            if (forWidget) context!!.getString(
                R.string.days_ago,
                days
            ) else stringResource(R.string.days_ago, days)
        }

        else -> {
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
            val formattedDate = dataFormatada.format(formatter)
            formattedDate
        }
    }
}