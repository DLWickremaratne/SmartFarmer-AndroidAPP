package com.example.smartfarmerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class InstructionActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    private Uri selectedUri;
    private static final int PICK_FILE = 1;
    UploadTask uploadTask;
    EditText etdesc;
    Button btnchoosefile,btnuploadfile;
    VideoView videoView;
    String url,name;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db1,db2,db3;

    MediaController mediaController;
    String type;
    FarmerInstruction farmerInstruction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        farmerInstruction = new FarmerInstruction();

        mediaController = new MediaController(this);

        progressBar = findViewById(R.id.pb_post);
        imageView = findViewById(R.id.iv_post);
        videoView= findViewById(R.id.vv_post);
        btnchoosefile = findViewById(R.id.btn_choosefile_post);
        btnuploadfile = findViewById(R.id.btn_uploadfile_post);
        etdesc = findViewById(R.id.et_desc_post);



        storageReference = FirebaseStorage.getInstance().getReference("User posts");//upload files


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All posts");

        btnuploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dopost();
            }
        });

        btnchoosefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();

            }
        });


    }

    private void chooseImage() {
////        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/* video/*"); //both videos and photos
//        //intent.setAction(Intent.ACTION_GET_CONTENT);
////        startActivityForResult(intent,PICK_FILE);


        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/* video/*");
        startActivityForResult(galleryIntent,PICK_FILE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE || resultCode == RESULT_OK ||

                data != null || data.getData() != null){

            selectedUri = data.getData();


            if (selectedUri.toString().contains("image")){
                Picasso.get().load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                type = "iv";
            }else if (selectedUri.toString().contains("video")){
                videoView.setMediaController(mediaController);
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";
            }else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }

        }

    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver(); //select file is mp4 or jpack
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(currentuid);


        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {

                            name = task.getResult().getString("name");
                            url = task.getResult().getString("url");


                        } else {
                            Toast.makeText(InstructionActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }
    void Dopost() {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String currentuid = user.getUid();

            String desc = etdesc.getText().toString();

            Calendar cdate = Calendar.getInstance();
            SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-YYYY");
            final String savedate = currentdate.format(cdate.getTime());

            Calendar ctime = Calendar.getInstance();
            SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
            final String savetime = currenttime.format(ctime.getTime());

            final String time = savedate + ":" + savetime;

            //press the btn first get time

            //do the validation
            if (TextUtils.isEmpty(desc) || selectedUri != null) {

                //create referance for storage

                progressBar.setVisibility(View.VISIBLE);
                final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(selectedUri));
                uploadTask = reference.putFile(selectedUri);

                //when the task succesfully uploaded
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }


                        return reference.getDownloadUrl();
                    }
                    //validation if task is sucessfull
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            if (type.equals("iv")) {
                                farmerInstruction.setDesc(desc);
                                farmerInstruction.setName(name);
                                farmerInstruction.setPostUri(downloadUri.toString());
                                farmerInstruction.setTime(time);
                                farmerInstruction.setUid(currentuid);
                                farmerInstruction.setUrl(url);
                                farmerInstruction.setType("iv");

                                // for image
                                String id = db1.push().getKey();
                                db1.child(id).setValue(farmerInstruction);
                                // for both
                                String id1 = db3.push().getKey();
                                db3.child(id1).setValue(farmerInstruction);

                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(InstructionActivity.this, "Instruction Uploaded", Toast.LENGTH_SHORT).show();


                            } else if (type.equals("vv")) {

                                farmerInstruction.setDesc(desc);//description
                                farmerInstruction.setName(name);
                                farmerInstruction.setPostUri(downloadUri.toString());
                                farmerInstruction.setTime(time);
                                farmerInstruction.setUid(currentuid);
                                farmerInstruction.setUrl(url);
                                farmerInstruction.setType("vv");

                                // for video
                                String id3 = db2.push().getKey();
                                db1.child(id3).setValue(farmerInstruction);

                                // for both
                                String id4 = db3.push().getKey();
                                db3.child(id4).setValue(farmerInstruction);

                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(InstructionActivity.this, "Instruction Uploaded", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(InstructionActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }


                        }

                    }
                });

            } else {
                Toast.makeText(this, "Please enter all Details", Toast.LENGTH_SHORT).show();
            }


        }




    }


