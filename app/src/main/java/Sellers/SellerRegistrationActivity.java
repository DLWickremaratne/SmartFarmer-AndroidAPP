package Sellers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.smartfarmerapp.MainActivity;
import com.example.smartfarmerapp.R;

public class SellerRegistrationActivity extends AppCompatActivity {

    private Button sellerLoginBegin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);


        sellerLoginBegin = findViewById(R.id.seller_already_have_account_btn);


        sellerLoginBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(SellerRegistrationActivity.this, SellerLogin.class);
                startActivity(intent);

            }
        });
    }
}