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
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

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
        executeTransaction()

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
    @SuppressLint("SuspiciousIndentation")
    private fun loadNotes(){
        //Firestore Queries doesnt have "OR" operator and "NOT" operator (Ex : retrieve document not equal to  (priority =2)) It is not possible
        val task1=noteBookRef.whereLessThan("priority",2)
            .orderBy("priority")
            .get()
        val task2 = noteBookRef.whereGreaterThan("priority", 2)
            .orderBy("priority")
            .get()

        var allTasks:Task<List<QuerySnapshot>> = Tasks.whenAllSuccess(task1,task2)
            allTasks.addOnSuccessListener {
                var data=""
                for (querySnapshot in it){
                    for (documentsnapshot in querySnapshot){
                        var note=documentsnapshot.toObject(Note::class.java)
                        note.id=documentsnapshot.id
                        var title=note.title
                        var desc=note.description
                        var priority=note.priority
                        data += "Id: ${note.id} \n Title: $title \n Description: $desc \n Priority : $priority \n\n"
                    }
                }
                displayTxtview.text=data
            }
    }
    private fun executeTransaction(){
        //This FUnction is next version of batch()
        //It perform operation on whole batch but additionaly it performs operation on current value
        //if the user enter new value during process of executing batch operation then it will get that new value as (current value)

       db.runTransaction{
           val docRef = noteBookRef.document("New Note")
           val snapshot=it.get(docRef)
           val newPriority=snapshot.getLong("priority")?.plus(1)
           it.update(docRef,"priority",newPriority)
       }
    }



}