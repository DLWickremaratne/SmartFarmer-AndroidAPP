package com.example.smartfarmerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Fragment1 extends Fragment implements View.OnClickListener{

    ImageView imageView;
    TextView nameEt,ProfessionEt,EmailEt,CityEt,AddressEt;
    ImageButton ib_edit,imageButtonMenu ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment1,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageView = getActivity().findViewById(R.id.iv_f1);
        nameEt = getActivity().findViewById(R.id.tv_name_f1);
        ProfessionEt = getActivity().findViewById(R.id.tv_prof_f1);
        EmailEt = getActivity().findViewById(R.id.tv_email_f1);
        CityEt = getActivity().findViewById(R.id.tv_city_f1);
        AddressEt = getActivity().findViewById(R.id.tv_address_f1);

        ib_edit = getActivity().findViewById(R.id.ib_edit_f1);
        imageButtonMenu = getActivity().findViewById(R.id.ib_menu_f1);


        imageButtonMenu.setOnClickListener(this);
        ib_edit.setOnClickListener(this);




    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.ib_edit_f1:
                Intent intent = new Intent(getActivity(), UpdateProfile.class);
                startActivity(intent);
                break;
            case R.id.ib_menu_f1:
                BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
                bottomSheetMenu.show(getFragmentManager(),"bottomsheet");

            break;



        }
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentid = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();


        reference =  firestore.collection("user").document(currentid);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){

                            String nameResult = task.getResult().getString("name");
                            String profResult = task.getResult().getString("prof");
                            String urlResult = task.getResult().getString("url");
                            String emailResult = task.getResult().getString("email");
                            String cityResult = task.getResult().getString("city");
                            String addressResult = task.getResult().getString("address");

                            Picasso.get().load(urlResult).into(imageView);
                            nameEt.setText(nameResult);
                            ProfessionEt.setText(profResult);
                            EmailEt.setText(emailResult);
                            CityEt.setText(cityResult);
                            AddressEt.setText(addressResult);


                        }else {
                            Intent intent = new Intent(getActivity(),CreateProfile.class);
                            startActivity(intent);

                        }
                    }
                });



    }
}
