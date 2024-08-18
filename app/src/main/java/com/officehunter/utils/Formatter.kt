package com.officehunter.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    }
}