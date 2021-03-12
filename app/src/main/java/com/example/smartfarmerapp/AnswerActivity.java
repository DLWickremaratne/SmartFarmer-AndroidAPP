package com.example.smartfarmerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Calendar;

public class AnswerActivity extends AppCompatActivity {

 String uid,que,postkey;
 EditText editText;
 Button button;
 AnswerFarmer farmer;
 FirebaseDatabase database = FirebaseDatabase.getInstance();
 DatabaseReference Allquestions;
 String name,url,time;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);


        farmer = new AnswerFarmer();
        editText = findViewById(R.id.answer_et);
        button = findViewById(R.id.btn_answer_submit);
        Bundle bundle = getIntent().getExtras();
        if(bundle !=null){
            uid = bundle.getString("key");
            postkey = bundle.getString("p");

        }else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        Allquestions = database.getReference("All Questions").child(postkey).child("Answer");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAnswer();

            }
        });

    }
    void saveAnswer(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();

        String answer = editText.getText().toString();
        if(answer !=null){

            Calendar cdate = Calendar.getInstance();
            SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-YYYY");
            final  String savedate = currentdate.format(cdate.getTime());

            Calendar ctime = Calendar.getInstance();
            SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
            final String savetime = currenttime.format(ctime.getTime());

            time = savedate +":"+ savetime;

            farmer.setAnswer(answer);
            farmer.setTime(time);
            farmer.setName(name);
            farmer.setUid(uid);
            farmer.setUrl(url);

            String id = Allquestions.push().getKey();
            Allquestions.child(id).setValue(farmer);

            Toast.makeText(this, "Your Answer Submitted", Toast.LENGTH_SHORT).show();



        }else {
            Toast.makeText(this, "Please write answer", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        FirebaseFirestore d = FirebaseFirestore.getInstance();
        DocumentReference reference;
        reference = d.collection("user").document(userid);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            url = task.getResult().getString("url");
                            name = task.getResult().getString("name");

                        } else {
                            Toast.makeText(AnswerActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}