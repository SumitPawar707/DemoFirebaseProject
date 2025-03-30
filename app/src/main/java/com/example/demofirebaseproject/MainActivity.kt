package com.example.demofirebaseproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.demofirebaseproject.Classes.Note
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions

class MainActivity : AppCompatActivity() {
    private lateinit var editTextTitle:EditText
    private lateinit var editTextDescription:EditText
    private lateinit var saveBtn:Button
    private val db:FirebaseFirestore= FirebaseFirestore.getInstance()
    private val docRef:DocumentReference=db.collection("Notebook").document("My FIrst Note")
    private lateinit var displayTxtview:TextView
    private lateinit var loadBtn:Button
    private lateinit var updateTitleBtn:Button
    private lateinit var deleteDescBtn:Button
    private lateinit var deleteNoteBtn:Button
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
        saveBtn=findViewById(R.id.button_save)
        displayTxtview=findViewById(R.id.displaytxtview)
        loadBtn=findViewById(R.id.button_load)
        updateTitleBtn=findViewById(R.id.button_updateTitle)
        deleteDescBtn=findViewById(R.id.button_deleteDescription)
        deleteNoteBtn=findViewById(R.id.button_deleteNote)

        saveBtn.setOnClickListener {
            save()
        }
        loadBtn.setOnClickListener {
            loadData()
        }
        updateTitleBtn.setOnClickListener {
            updateTitle()
        }
        deleteDescBtn.setOnClickListener {
            deleteDescription()
        }
        deleteNoteBtn.setOnClickListener {
            deleteNote()
        }

    }

    override fun onStart() {
        super.onStart()
       docRef.addSnapshotListener(this) { document, error ->
            error?.let {
                return@addSnapshotListener
            }
            document?.let {
                if (it.exists()){

                    //this is for adding using map
                   // var title=it.getString(KEY_TITLE)
                   // var descripiton=it.getString(KEY_DESCRIPTION)

                    //this is using custom object
                    val note=it.toObject(Note::class.java)

                    displayTxtview.text="Title : ${note?.title} \n Description : ${note?.description}"
                }else{
                    displayTxtview.text=""
                    Toast.makeText(this@MainActivity, "Error at loading data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

//    override fun onStop() {
//        super.onStop()
//        listener.remove() //this is used to stop onstart()(manually)  when app is running in background
//    }                     //add( listener= docRef.addSnapshotListener { document, error ->)
                            //onstop can be done my automatically using "this" keyword in addSnapshotListener

    private fun save(){
        val title =editTextTitle.text.toString()
        val description=editTextDescription.text.toString()
        // Custom data object
        val note=Note(title,description)
        //adding data using map
//        val note= mutableMapOf<String,Any>()
//        note.put(KEY_TITLE,title)  //here we put (key,value) so it should have been like (title,description) but I used default title
//        note.put(KEY_DESCRIPTION,description)

        docRef.set(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Note Added Succesfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error Note not added", Toast.LENGTH_SHORT).show()
            }
    }
    private fun loadData(){
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()){
                    var title=document.getString(KEY_TITLE)
                    var descripiton=document.getString(KEY_DESCRIPTION)
                    displayTxtview.text="Title : $title \n Description : $descripiton"
                }else{
                    Toast.makeText(this@MainActivity, "Error at loading data", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "Error at loading data", Toast.LENGTH_SHORT).show()
            }
    }
    private fun updateTitle(){
        var title=editTextTitle.text.toString()
        val note = mutableMapOf<String, Any>()
        note[KEY_TITLE]=title
        docRef.set(note, SetOptions.merge()) //SetOptions.merge() this helps only to update provided data that is (title) /do not update anything other than title
                                            //Use it only if you want to update few fields not entire date
        //use docRef.update() to update entire data or fields
    }
    private fun deleteDescription(){
        val note = mutableMapOf<String, Any>()
        note[KEY_DESCRIPTION]=FieldValue.delete()
        docRef.update(note)
    }
    private fun deleteNote(){
        docRef.delete()
    }
}