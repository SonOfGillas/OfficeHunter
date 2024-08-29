package com.officehunter.utils

import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class Formatter {
    companion object{
        fun int2string(number: Int):String{
            if(number>999999){
              return   "${number/1000000}M"
            } else if (number>999){
                return "${number/1000}K"
            } else {
                return number.toString()
            }
        }

        fun double2percentage(value: Double):String{
            val percentage = value * 100
            if(percentage >= 100.0) return  "100%"
            if(percentage == 0.0) return "0%"
            val percentageString = percentage.toString()
            val decimalIndex = percentageString.indexOf('.')
            var maxNumberOfDecimal = 3
            return if (decimalIndex != -1 && percentageString.length > decimalIndex + maxNumberOfDecimal) {
                if (percentage >= 1)
                    maxNumberOfDecimal=1
                else if(percentage >= 0.1)
                    maxNumberOfDecimal=2
                percentageString.substring(0, decimalIndex + (maxNumberOfDecimal+1)) + "%"
            } else {
                "$percentageString%"
            }
        }

        fun date2String(value:Date):String {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.format(value)
        }

        fun date2Birthday(value: Date):String{
            val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
            return dateFormat.format(value)
        }

        fun date2TimePassed(value: Date):String{
            val today = Date()
            val timeElapsed = today.time - value.time
            val totalDays = TimeUnit.MILLISECONDS.toDays(timeElapsed)
            val years = totalDays / 365
            val remainingDays = totalDays % 365
            val months = remainingDays / 30
            if (timeElapsed >= TimeUnit.DAYS.toMillis(365)){
                return "$years years and $months month"
            } else if (timeElapsed >= TimeUnit.DAYS.toMillis(30)) {
                return "${TimeUnit.MILLISECONDS.toDays(timeElapsed)/30} months"
            } else {
                return "${TimeUnit.MILLISECONDS.toDays(timeElapsed)/1} days"
            }
        }
    }
}