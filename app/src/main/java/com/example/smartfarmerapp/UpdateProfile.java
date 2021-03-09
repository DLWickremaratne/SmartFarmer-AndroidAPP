package com.example.smartfarmerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;


public class UpdateProfile extends AppCompatActivity {

    EditText etname,etProfession,etEmail,etCity,etAddress;
    Button button;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid= user.getUid();
        documentReference = db.collection("user").document(currentuid);


        etname =findViewById(R.id.et_name_up);
        etProfession =findViewById(R.id.et_Profession_up);
        etEmail =findViewById(R.id.et_email_up);
        etCity =findViewById(R.id.et_city_up);
        etAddress =findViewById(R.id.et_Adress_up);
        button =findViewById(R.id.btn_up);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){

                            String nameResult = task.getResult().getString("name");
                            String profResult = task.getResult().getString("prof");
                            String urlResult = task.getResult().getString("url");
                            String emailResult = task.getResult().getString("email");
                            String cityResult = task.getResult().getString("city");
                            String addressResult = task.getResult().getString("address");


                            etname.setText(nameResult);
                            etProfession.setText(profResult);
                            etEmail.setText(emailResult);
                            etCity.setText(cityResult);
                            etAddress.setText(addressResult);
                            
                        }else{
                            Toast.makeText(UpdateProfile.this, "No Profile", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    private void updateProfile() {


        String name = etname.getText().toString();
        String profession = etProfession.getText().toString();
        String email = etEmail.getText().toString();
        String city = etCity.getText().toString();
        String address = etAddress.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid1= user.getUid();
        final DocumentReference sDoc = db.collection("user").document(currentuid1);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sDoc);


                transaction.update(sDoc, "name",name );
                transaction.update(sDoc,"prof",profession);
                transaction.update(sDoc,"email",email);
                transaction.update(sDoc,"city",city);
                transaction.update(sDoc,"address",address);


                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateProfile.this, "updated", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfile.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                });





    }
}