package com.example.hotels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddProductActivity extends AppCompatActivity {

    private String CategoryName, Description, Price, Pname , saveCurrentDate, saveCurrentTime ;
    private Button AddNewHotelButton;
    private ImageView InputHotelImage;
    private EditText InputHotelName, InputHotelDescription, InputHotelPrice;
    private static final int GalleryPic = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference mStorageRef;
    //private StorageReference ProductImagesRef;
    private DatabaseReference productsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        CategoryName = getIntent().getExtras().get("category").toString();
        //ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        productsRef = FirebaseDatabase.getInstance().getReference().child("products");

        AddNewHotelButton = (Button) findViewById(R.id.add_new_product);
        InputHotelImage = (ImageView) findViewById(R.id.select_product_image);
        InputHotelName = (EditText) findViewById(R.id.product_name);
        InputHotelDescription = (EditText) findViewById(R.id.product_description);
        InputHotelPrice = (EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);

        InputHotelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        AddNewHotelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPic && resultCode==RESULT_OK && data!=null) {
            ImageUri = data.getData();
            InputHotelImage.setImageURI(ImageUri);
        }

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPic);
    }
    private void ValidateProductData() {
        Description = InputHotelDescription.getText().toString();
        Price = InputHotelPrice.getText().toString();
        Pname = InputHotelName.getText().toString();

        if (ImageUri == null) {
            Toast.makeText(this, "Image is Required!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description)) {
            Toast.makeText(this, "Description is required!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Price is required!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Pname)) {
            Toast.makeText(this, "pizza name is required!", Toast.LENGTH_SHORT).show();
        }

        else {
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {
        loadingBar.setTitle("Add new Product");
        loadingBar.setMessage("Please wait to add product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;
        final StorageReference filePath = mStorageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddProductActivity.this, "Error Occourd!"+ message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddProductActivity.this, "Image Uploaded successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddProductActivity.this, "got the product image url successfully ", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("pName", Pname);

        productsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {


                            Intent intent = new Intent(AdminAddProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddProductActivity.this, "product is added successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddProductActivity.this, "Error Occourd!" +message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
