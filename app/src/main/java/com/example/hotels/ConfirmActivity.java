package com.example.hotels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hotels.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmActivity extends AppCompatActivity {
    private EditText customerName, phone, address, City;
    private Button confirmOrderBtn;

    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price= Rs :" +totalAmount, Toast.LENGTH_SHORT).show();

        confirmOrderBtn = (Button) findViewById(R.id.confirm_order);
        customerName = (EditText) findViewById(R.id.ordering_name);
        phone = (EditText) findViewById(R.id.ordering_phone_number);
        address = (EditText) findViewById(R.id.ordering_address);
        City = (EditText) findViewById(R.id.ordering_city);

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });
    }

    private void check() {

        if (TextUtils.isEmpty(customerName.getText().toString())) {
            Toast.makeText(this, "please type your full name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone.getText().toString())) {
            Toast.makeText(this, "please type your phone number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(address.getText().toString())) {
            Toast.makeText(this, "please type your current address", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(City.getText().toString())) {
            Toast.makeText(this, "please type your Country and city", Toast.LENGTH_SHORT).show();
        }
        else {
            confirmOrder();
        }

    }

    private void confirmOrder() {

        final String saveCurrentDate, saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, YYYY");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", customerName.getText().toString());
        ordersMap.put("phone", phone.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("address", address.getText().toString());
        ordersMap.put("city", City.getText().toString());
        ordersMap.put("discount", "");
        ordersMap.put("state", "not booked");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(ConfirmActivity.this, "your final order is successfully finished", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    }/*
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            setTime1();*/
                                }

                            });
                }
            }
        });
    }
}
