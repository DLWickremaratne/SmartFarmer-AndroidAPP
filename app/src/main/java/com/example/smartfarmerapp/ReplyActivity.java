package com.example.smartfarmerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ReplyActivity extends AppCompatActivity {


    String uid, question, post_key;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference, reference2;

    TextView nametv, questiontv, tvreply;
    RecyclerView recyclerView;
    ImageView imageViewQue, imageViewUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        nametv = findViewById(R.id.name_reply_tv);
        questiontv = findViewById(R.id.que_reply_tv);
        imageViewQue = findViewById(R.id.iv_que_user);
        imageViewUser = findViewById(R.id.iv_reply_user);
        tvreply = findViewById(R.id.answer_tv);

        recyclerView = findViewById(R.id.rv_ans);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReplyActivity.this));


        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            uid = extra.getString("uid");
            question = extra.getString("q");
            post_key = extra.getString("postkey");

            // key = extra.getString("key");
        } else {
            Toast.makeText(this, "opps", Toast.LENGTH_SHORT).show();
        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();


        reference = db.collection("user").document(currentUserid);
        reference2 = db.collection("user").document(currentUserid);


        tvreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ReplyActivity.this,ReplyActivity.class);
                intent.putExtra("uid",uid);
                //intent.putExtra("q",question);
                intent.putExtra("postkey",post_key);
                startActivity(intent);




            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            String url = task.getResult().getString("url");
                            String name = task.getResult().getString("name");
                            Picasso.get().load(url).into(imageViewQue);
                            questiontv.setText(question);
                            nametv.setText(name);
                        } else {
                            Toast.makeText(ReplyActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        reference2.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            String url = task.getResult().getString("url");
                            Picasso.get().load(url).into(imageViewUser);

                        } else {
                            Toast.makeText(ReplyActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}