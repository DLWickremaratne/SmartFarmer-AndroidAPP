
package com.example.smartfarmerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SCAdminCategoryActivity extends AppCompatActivity {

    private ImageView axe,seeds,fatiliser,tactor;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_c_admin_category);


        axe =(ImageView) findViewById(R.id.axe);
        seeds =(ImageView) findViewById(R.id.seeds);
        fatiliser =(ImageView) findViewById(R.id.fatiliser);
        tactor =(ImageView) findViewById(R.id.tactor);



        axe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(SCAdminCategoryActivity.this,SCAdminAddNewProduct.class);
                intent.putExtra("category","Equipments");
                startActivity(intent);

            }
        });

        seeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(SCAdminCategoryActivity.this,SCAdminAddNewProduct.class);
                intent.putExtra("category","Seeds");
                startActivity(intent);


            }
        });

        fatiliser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(SCAdminCategoryActivity.this,SCAdminAddNewProduct.class);
                intent.putExtra("category","Fertilizers");
                startActivity(intent);


            }
        });

        tactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(SCAdminCategoryActivity.this,SCAdminAddNewProduct.class);
                intent.putExtra("category","Vehicles Items");
                startActivity(intent);


            }
        });



    }
}