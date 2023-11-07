package com.example.theprivate

import java.util.*
import android.icu.text.*

class Converter {
    companion object {
        fun changeDateToFormat(date:Date):String {
            var format = StringBuilder()
            var sdf = SimpleDateFormat("MM dd, yyyy")
            var temp = sdf.format(date)
            var arr = temp.split(" ")
            var month = ""
            when(arr[0].toInt()) {
                1 -> month = "Jan"
                2 -> month = "Feb"
                3 -> month = "Mar"
                4 -> month = "Apr"
                5 -> month = "May"
                6 -> month = "Jun"
                7 -> month = "Jul"
                8 -> month = "Aug"
                9 -> month = "Sep"
                10 -> month = "Oct"
                11 -> month = "Nov"
                12 -> month = "Dec"
            }
            format.append(month + " ")
            format.append(arr[1] + " ")
            format.append(arr[2])
            return format.toString()
        }
        fun changeStrToFormat(dateStr:String):String {
            var format = StringBuilder()
            var arr = dateStr.split("-")
            var month = ""
            when(arr[1].toInt()) {
                1 -> month = "Jan"
                2 -> month = "Feb"
                3 -> month = "Mar"
                4 -> month = "Apr"
                5 -> month = "May"
                6 -> month = "Jun"
                7 -> month = "Jul"
                8 -> month = "Aug"
                9 -> month = "Sep"
                10 -> month = "Oct"
                11 -> month = "Nov"
                12 -> month = "Dec"
            }
            format.append(month + " ")
            format.append(arr[2] + ", ")
            format.append(arr[0])
            return format.toString()
        }
    }
}