package com.example.demofirebaseproject

import android.annotation.SuppressLint

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.demofirebaseproject.Classes.Note
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MainActivity : AppCompatActivity() {
    private lateinit var editTextTitle:EditText
    private lateinit var editTextDescription:EditText
    private lateinit var editTextPriority:EditText
    private lateinit var editTextTags:EditText
    private lateinit var saveBtn:Button
    private lateinit var loadBtn:Button
    private lateinit var displayTxtview:TextView

   private val db:FirebaseFirestore=FirebaseFirestore.getInstance()
    private val docRef:DocumentReference=db.collection("Notebook").document("My FIrst Note")
    private val noteBookRef:CollectionReference=db.collection("Notebook")
    private lateinit var listener:ListenerRegistration //Stores a Firestore listener reference.

    private val KEY_TITLE="title"
    private val KEY_DESCRIPTION="description"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        editTextTitle=findViewById(R.id.edit_text_title)
        editTextDescription=findViewById(R.id.edit_text_description)
        editTextPriority=findViewById(R.id.edit_text_priority)
        editTextTags=findViewById(R.id.edit_text_tags)
        saveBtn=findViewById(R.id.add_button)
        displayTxtview=findViewById(R.id.displaytxtview)
        loadBtn=findViewById(R.id.button_load)


        saveBtn.setOnClickListener {
            addNote()
        }
        loadBtn.setOnClickListener {
            loadNotes()
        }
        updateObjects()

    }

    override fun onStart() {
        super.onStart()
    }

//    override fun onStop() {
//        super.onStop()
//        listener.remove() //this is used to stop onstart()(manually)  when app is running in background
//    }                     //add( listener= docRef.addSnapshotListener { document, error ->)
                            //onstop can be done my automatically using "this" keyword in addSnapshotListener

    private fun addNote(){
        val title =editTextTitle.text.toString()
        val description=editTextDescription.text.toString()

        if (editTextPriority.text.isEmpty()){
            editTextPriority.setText("0")
        }
        val priority=editTextPriority.text.toString().toInt()

        val tagsArray=editTextTags.text.toString().trim().split(",")
        val tags= mutableMapOf<String,Boolean>()
        for(tag in tagsArray){
            tags[tag]=true
        }

        // Custom data object
        val note=Note(title,description,priority,tags)
        //adding data using map
//        val note= mutableMapOf<String,Any>()
//        note.put(KEY_TITLE,title)  //here we put (key,value) so it should have been like (title,description) but I used default title
//        note.put(KEY_DESCRIPTION,description)

        noteBookRef.add(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Note Added Succesfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error Note not added", Toast.LENGTH_SHORT).show()
            }
    }
    @SuppressLint("SuspiciousIndentation")
    private fun loadNotes(){
        noteBookRef.whereEqualTo("tags.tag1",true)//adding query only retrieve those documment where tags.tag1=true
            .get()
            .addOnSuccessListener { querysnapshot ->
            var data=""
            for (documentSnapshot in querysnapshot){
                val note=documentSnapshot.toObject(Note::class.java)
                note.id=documentSnapshot.id
                data += "\n ID : ${note.id}"
                for (tag in note.tags!!.keys){ //To only retriveing keys not values
                    data += "\n $tag"
                }
            }
            displayTxtview.text=data

        }


    }
    private fun updateObjects(){ //updating objects in particular document
        noteBookRef.document("QIRVgFBAa4h0U6RQpvRR") //getting document first
           // .update("tags.tag1",false) //updating particular field in document
            .update("tags.tag1",FieldValue.delete())//deleting tags.tag1 field
    }



}