package com.example.smartfarmerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class AskActivity extends AppCompatActivity {

    EditText editText;
    ImageView imageView;
    Uri imageUri;
    Button button;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AllQuestions,UserQuestions;//save data in two different child
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    FarmerQuestion farmer;
    String name,url,uid;
    private static final int PICK_IMAGE =1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //Get the currently signed-in user
        String currentUserid = user.getUid();

        editText = findViewById(R.id.ask_et_question);

        button = findViewById(R.id.btn_submit);
        documentReference = db.collection("user").document(currentUserid);
        AllQuestions =  database.getReference("All Questions");
        UserQuestions = database.getReference("User Questions").child(currentUserid);//save data inside user id
//save the question answers in two different branchers
        farmer = new FarmerQuestion();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String question = editText.getText().toString();

                Calendar cdate = Calendar.getInstance();//call date
                SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-YYYY");
                final  String savedate = currentdate.format(cdate.getTime());

                Calendar ctime = Calendar.getInstance();//save time
                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                final String savetime = currenttime.format(ctime.getTime());

                String time = savedate +":"+ savetime;



                if (question !=null){

                    farmer.setQuestion(question);
                    farmer.setName(name);
                    farmer.setUrl(url);
                    farmer.setUserid(uid);
                    farmer.setTime(time);
                    String id = UserQuestions.push().getKey();
                    UserQuestions.child(id).setValue(farmer); //save data in user reference

                    String child = AllQuestions.push().getKey();
                    farmer.setKey(id);
                    AllQuestions.child(child).setValue(farmer);//save data
                    Toast.makeText(AskActivity.this, "Submitted", Toast.LENGTH_SHORT).show();




                }else{
                    Toast.makeText(AskActivity.this, "Please ask question", Toast.LENGTH_SHORT).show();

                }


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

                        if (task.getResult().exists()) {

                            name = task.getResult().getString("name");
                            url = task.getResult().getString("url");
                            uid = task.getResult().getString("uid");


                        } else {
                            Toast.makeText(AskActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

}