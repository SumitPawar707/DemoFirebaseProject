package com.example.demofirebaseproject.Classes

import com.google.firebase.firestore.Exclude

data class Note(
    var title:String,
    var description:String, var priority:Int,
    var tags: MutableMap<String, Boolean>?
){
    constructor() : this("","",0,null)// no-arg constructor needed

    @Exclude
    var id:String=""
}
