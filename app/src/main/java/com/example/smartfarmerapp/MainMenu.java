package com.example.smartfarmerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity   {

    Button button,button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        button =(Button)findViewById(R.id.btn_sellerlogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainMenu();
            }
        });
        button2 =(Button)findViewById(R.id.btn_farmerlogin);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain();
            }
        });

    }

    private void openMain() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    private void openMainMenu() {
            Intent intent = new Intent(this, SCAdminCategoryActivity.class);
            startActivity(intent);

    }



}