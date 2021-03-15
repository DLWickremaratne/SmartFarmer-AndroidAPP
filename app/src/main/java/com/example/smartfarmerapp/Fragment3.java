package com.example.smartfarmerapp;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Fragment3 extends Fragment implements View.OnClickListener {
    Button button;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference, likeref;
    Boolean likechecker = false;

    DatabaseReference db1,db2,db3;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment3, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button = getActivity().findViewById(R.id.createpost_f3);
        reference = database.getReference("All posts");
        likeref = database.getReference("post likes");
        recyclerView = getActivity().findViewById(R.id.rv_posts);
        recyclerView.setHasFixedSize(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All posts");

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));




        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.createpost_f3:
                Intent intent = new Intent(getActivity(), InstructionActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<FarmerInstruction> options =
                new FirebaseRecyclerOptions.Builder<FarmerInstruction>()
                        .setQuery(reference, FarmerInstruction.class)
                        .build();

        FirebaseRecyclerAdapter<FarmerInstruction, InstructionViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<FarmerInstruction, InstructionViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull InstructionViewholder holder, int position, @NonNull FarmerInstruction model) {


                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserid = user.getUid();

                        final String postkey = getRef(position).getKey();
                        holder.SetPost(getActivity(), model.getName(), model.getUrl(), model.getPostUri(), model.getTime()
                                , model.getUid(), model.getType(), model.getDesc());


                        final String url = getItem(position).getPostUri();
                        final String name = getItem(position).getName();
                        //final String url = getItem(position).getUrl();
                        final String time = getItem(position).getTime();
                        final String userid = getItem(position).getUid();
                        final String type = getItem(position).getType();


                        holder.likeschecker(postkey);

                        holder.menuoptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showDialog(name, url, time, userid,type);
                            }
                        });
                        holder.likebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                likechecker = true;

                                likeref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (likechecker.equals(true)) {
                                            if (snapshot.child(postkey).hasChild(currentUserid)) {
                                                likeref.child(postkey).child(currentUserid).removeValue();
                                                //delete(time);
                                                likechecker = false;


                                            } else {
                                                //saving everything
                                                likeref.child(postkey).child(currentUserid).setValue(true);

                                                likechecker = false;
                                                Toast.makeText(getActivity(), "You Accept this instruction", Toast.LENGTH_SHORT).show();

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
                    public InstructionViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.instruction_layout, parent, false);

                        return new InstructionViewholder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    void showDialog(String name, String url, String time, String userid, String type) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.instruction_options, null);

        TextView delete = view.findViewById(R.id.delete_tv_post);
        TextView share = view.findViewById(R.id.share_tv_ins);
        TextView copyurl = view.findViewById(R.id.copyurl_tv_ins);


        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();

        alertDialog.show();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        if (userid.equals(currentuid)){
            delete.setVisibility(View.VISIBLE);
        }else {
            delete.setVisibility(View.INVISIBLE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Query query = db1.orderByChild("time").equalTo(time);
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
                Query query2 = db2.orderByChild("time").equalTo(time);
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
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
                Query query3 = db3.orderByChild("time").equalTo(time);
                query3.addListenerForSingleValueEvent(new ValueEventListener() {
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
                alertDialog.dismiss();


            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sharetext = name +"\n" +"\n"+ url;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_TEXT,sharetext);
                intent.setType("text/plain");
                startActivity(intent.createChooser(intent,"share via"));

                alertDialog.dismiss();

            }
        });

        copyurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager cp = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("String",url);
                cp.setPrimaryClip(clip);
                clip.getDescription();
                Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();

                alertDialog.dismiss();


            }
        });

    }
}



