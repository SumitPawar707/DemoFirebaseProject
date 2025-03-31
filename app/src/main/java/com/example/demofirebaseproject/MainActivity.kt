package com.example.demofirebaseproject

import android.annotation.SuppressLint
import android.content.ContentValues.TAG

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.demofirebaseproject.Classes.Note
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject

class MainActivity : AppCompatActivity() {
    private lateinit var editTextTitle:EditText
    private lateinit var editTextDescription:EditText
    private lateinit var editTextPriority:EditText
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
        saveBtn=findViewById(R.id.add_button)
        displayTxtview=findViewById(R.id.displaytxtview)
        loadBtn=findViewById(R.id.button_load)


        saveBtn.setOnClickListener {
            addNote()
        }
        loadBtn.setOnClickListener {
            loadNotes()
        }

    }

    override fun onStart() {
        super.onStart()
        noteBookRef.whereGreaterThanOrEqualTo("priority",2)
            .orderBy("priority",Query.Direction.ASCENDING)
            .addSnapshotListener(this) { documentSnapshot, error ->
            error?.let {
                return@addSnapshotListener
            }
            //querydocumentsnapshot are gurenteed to exist so there is no need to check
            documentSnapshot?.let {
                var data=""
                for (documentsnapshot in it){
                    val note=documentsnapshot.toObject(Note::class.java)
                    note.id=documentsnapshot.id
                    var title=note.title
                    var desc=note.description
                    var priority=note.priority
                    data += "Id: ${note.id} \n Title: $title \n Description: $desc \n Priority : $priority \n\n"
                }
                displayTxtview.text=data
            }
        }

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
        // Custom data object
        val note=Note(title,description,priority)
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
    private fun loadNotes(){
        //Firestore Queries doesnt have "OR" operator and "NOT" operator (Ex : retrieve document not equal to  (priority =2)) It is not possible
        noteBookRef.whereGreaterThanOrEqualTo("priority",2)
            .whereEqualTo("title","Aa")//This query needs to create index
                                        //This is because firestore doesnt actually look into documents one by one instead it will create indexes
            .get()
            .addOnSuccessListener { querySnapshot -> //querysnapshot contains all the documents
                //querydocumentsnapshot are gurenteed to exist so there is no need to check
                var data=""
                for (documentSnapshot in querySnapshot){ //Loop through each document
                    var note=documentSnapshot.toObject(Note::class.java) //convert to note object
                    var title=note.title
                    var desc=note.description
                    var priority=note.priority
                    data += "Title : $title \n Description: $desc \n Priority: $priority \n \n"
                }
                displayTxtview.text=data
            }
            .addOnFailureListener {
               Log.d(TAG, it.toString())
            }

    }

}