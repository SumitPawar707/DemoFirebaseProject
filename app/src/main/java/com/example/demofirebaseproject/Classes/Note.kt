package com.example.demofirebaseproject.Classes

import com.google.firebase.firestore.Exclude

data class Note(var title:String,var description:String, var priority:Int){
    constructor() : this("","",0)// no-arg constructor needed

    @Exclude
    var id:String=""
}
