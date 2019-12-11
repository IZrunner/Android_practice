package com.example.notes;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class NewNoteActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private Button btnCreate;
    private EditText etTitle, etContent;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fNotesDatabase;

    private boolean exist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        btnCreate = findViewById(R.id.new_note_btn);
        etTitle = findViewById(R.id.new_note_title);
        etContent = findViewById(R.id.new_note_content);

        fAuth = FirebaseAuth.getInstance();
        fNotesDatabase = FirebaseFirestore.getInstance();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    createNote(title, content);
                } else {
                    Snackbar.make(view, "Fill empty fields", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void createNote(String title, String content) {

        if (fAuth.getCurrentUser() != null) {

            if (exist) {
            // UPDATE A NOTE
                final String[] id = new String[1];
                fNotesDatabase.collection("notes")
                        .whereEqualTo("title", title)
                        .whereEqualTo("content", content)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                         id[0] = document.getId();
                                         Log.d(TAG, "Document id got successfully");
                                         break;
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                DocumentReference noteReference = fNotesDatabase.collection("notes").document(id[0]);
                noteReference
                        .update("title", title,
                                "content", content)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });


                Toast.makeText(this, "Note updated0", Toast.LENGTH_SHORT).show();
            } else {
                // CREATE A NEW NOTE
                // Add a new document with a generated id.
                Map<String, String> data = new HashMap<>();
                data.put("title", title);
                data.put("content", content);

                fNotesDatabase.collection("notes")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }

        } else {
            Toast.makeText(this, "USERS IS NOT SIGNED IN", Toast.LENGTH_SHORT).show();
        }

    }

}
