package com.example.theprivate

import kotlin.collections.*

class Validation {
    companion object {
        fun idValidate(id:String, list:MutableList<String>):Boolean = if(list.contains(id)) false else true
        fun contactFormValidate(contact:String) = if(contact.matches(Regex("(010)-[1-9][0-9]{3,4}-[0-9]{4,5}")) or contact.matches(Regex("(011)-[1-9][0-9]{2,3}-[0-9]{4,5}"))) true else false
        fun contactValidate(contact:String, list:ArrayList<String>) = if(list.contains(contact)) false else true
    }
}