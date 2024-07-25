package com.officehunter.utils

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
    }
}