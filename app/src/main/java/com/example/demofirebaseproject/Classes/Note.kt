package com.example.demofirebaseproject.Classes

import com.google.firebase.firestore.Exclude

data class Note(var title:String,var description:String){
    constructor() : this("","")// no-arg constructor needed

    @Exclude
    var id:String=""
}
