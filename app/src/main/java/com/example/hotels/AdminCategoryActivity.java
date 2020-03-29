package com.example.hotels;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView a;
    private ImageView b;
    private ImageView c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        a = (ImageView) findViewById(R.id.a_img);
        b = (ImageView) findViewById(R.id.b_img);
        c = (ImageView) findViewById(R.id.c_img);



        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddProductActivity.class);
                intent.putExtra("category", "a");
                startActivity(intent);
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddProductActivity.class);
                intent.putExtra("category", "b");
                startActivity(intent);
            }
        });

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddProductActivity.class);
                intent.putExtra("category", "c");
                startActivity(intent);
            }
        });

    }
}
