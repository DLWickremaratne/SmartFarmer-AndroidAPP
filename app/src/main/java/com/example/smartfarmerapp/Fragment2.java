package com.example.smartfarmerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Fragment2 extends Fragment implements View.OnClickListener{

    FloatingActionButton fb;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference,fvrtref,fvrt_listRef;
    RecyclerView recyclerView;
    Boolean fvrtChecker = false;
    ImageView imageView;

    FarmerQuestion farmer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment2,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);// fragment's activity has been created

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();////Get the currently signed-in user

        recyclerView = getActivity().findViewById(R.id.rv_f2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        databaseReference = database.getReference("All Questions");
        farmer = new FarmerQuestion();
        fvrtref = database.getReference("favourites"); //checking question is save or not
        fvrt_listRef = database.getReference("favoriteList").child(currentUserid); //reference for saving question


        imageView = getActivity().findViewById(R.id.iv_f2);
        fb = getActivity().findViewById(R.id.floatingActionButton);
        reference = db.collection("user").document(currentUserid);


        fb.setOnClickListener(this); //floating action bar
        imageView.setOnClickListener(this);

        FirebaseRecyclerOptions<FarmerQuestion> options =
                new FirebaseRecyclerOptions.Builder<FarmerQuestion>()
                .setQuery(databaseReference,FarmerQuestion.class)
                .build();//add the things

        FirebaseRecyclerAdapter<FarmerQuestion,Viewholder_Question> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<FarmerQuestion, Viewholder_Question>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder_Question holder, int position, @NonNull FarmerQuestion model) {


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserid = user.getUid();

                        final String postkey = getRef(position).getKey();

                        holder.setitem(getActivity(),model.getName(),model.getUrl(),model.getUserid(),model.getKey(),model.getQuestion(),model.getTime());

                        final String que = getItem(position).getQuestion();
                        final String name = getItem(position).getName();
                        final String url = getItem(position).getUrl();
                        final String time = getItem(position).getTime();
                        final String userid = getItem(position).getUserid();


                        holder.replybtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(),ReplyActivity.class);
                                intent.putExtra("uid",userid);
                                intent.putExtra("q",que);
                                intent.putExtra("postkey",postkey);
                                startActivity(intent);
                            }
                        });

                        holder.favoriteChecker(postkey);
                        holder.fvrt_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fvrtChecker = true;

                                fvrtref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (fvrtChecker.equals(true)){
                                            if (snapshot.child(postkey).hasChild(currentUserid)){
                                                fvrtref.child(postkey).child(currentUserid).removeValue();
                                                delete(time);
                                                Toast.makeText(getActivity(), "Removed from favourite", Toast.LENGTH_SHORT).show();
                                                fvrtChecker = false;
                                            }else{
                                                //saving everything
                                                fvrtref.child(postkey).child(currentUserid).setValue(true);
                                                farmer.setName(name);
                                                farmer.setTime(time);
                                                farmer.setUserid(userid);
                                                farmer.setUrl(url);
                                                farmer.setQuestion(que);


                                                //String id = fvrt_listRef.push().getKey();
                                                fvrt_listRef.child(postkey).setValue(farmer);
                                                fvrtChecker = false;
                                                Toast.makeText(getActivity(), "Added to favourite", Toast.LENGTH_SHORT).show();

                                            }

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public Viewholder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.question_item,parent,false);

                        return new Viewholder_Question(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }
    void delete(String time){
        Query query = fvrt_listRef.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    dataSnapshot1.getRef().removeValue();

                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) { //Called when a view has been clicked.

        switch (view.getId()) {
            case R.id.iv_f2:
                BottomSheetF2 bottomSheetF2 = new BottomSheetF2();
                bottomSheetF2.show(getFragmentManager(),"bottom");

                break;
            case R.id.floatingActionButton:
                Intent intent = new Intent(getActivity(), AskActivity.class); //send user
                startActivity(intent); //go to ask activity

                break;
        }


    }
    @Override
    public void onStart() { // the activity is becoming visible to the user
        super.onStart();
        reference.get() //retrieve data
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String url = task.getResult().getString("url");

                            Picasso.get().load(url).into(imageView);
                        }else {
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                });



    }
}
