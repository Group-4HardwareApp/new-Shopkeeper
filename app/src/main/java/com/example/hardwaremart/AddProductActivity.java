package com.example.hardwaremart;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.example.hardwaremart.databinding.AddProductBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {
    AddProductBinding binding;
    ArrayList<Category> categoryList;
    ArrayAdapter<Category> adapter;
    String shopkeeperId;
    Category category;
    Uri imageUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AddProductBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        CategoryService.CategoryApi categoryApi = CategoryService.getCategoryApiInstance();
        Call<ArrayList<Category>> call = categoryApi.getCategoryList();
        call.enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(Call<ArrayList<Category>> call, Response<ArrayList<Category>> response) {
                if(response.code() == 200){
                    categoryList = response.body();
                    categoryList.add(0,new Category("0","Select category",""));
                    adapter = new ArrayAdapter<Category>(AddProductActivity.this,R.layout.spinner_item_list,R.id.tvCategory,categoryList);
                    binding.spinnerCategory.setAdapter(adapter);
                    binding.spinnerCategory.setSelection(0);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Category>> call, Throwable t) {

            }
        });
        shopkeeperId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = categoryList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.ivProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(AddProductActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddProductActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
                } else {
                    Intent in = new Intent();
                    in.setAction(Intent.ACTION_GET_CONTENT);
                    in.setType("image/*");
                    startActivityForResult(in, 111);
                }
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri != null){
                    String name = binding.etProductName.getText().toString();
                    String price = binding.etProductPrice.getText().toString();
                    String brand = binding.etProductBrand.getText().toString();
                    String discount = binding.etProductDiscount.getText().toString();
                    String desc = binding.etDescription.getText().toString();
                    String qty = binding.etQuantityInStock.getText().toString();

                    File file = FileUtils.getFile(AddProductActivity.this, imageUri);
                    RequestBody requestFile =
                            RequestBody.create(
                                    MediaType.parse(Objects.requireNonNull(getContentResolver().getType(imageUri))),
                                    file
                            );

                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                    RequestBody productName = RequestBody.create(okhttp3.MultipartBody.FORM, name);
                    RequestBody productPrice = RequestBody.create(okhttp3.MultipartBody.FORM, price);
                    RequestBody productBrand = RequestBody.create(okhttp3.MultipartBody.FORM, brand);
                    RequestBody productDiscount = RequestBody.create(okhttp3.MultipartBody.FORM, discount);
                    RequestBody productDescription = RequestBody.create(okhttp3.MultipartBody.FORM, desc);
                    RequestBody productQty = RequestBody.create(okhttp3.MultipartBody.FORM, qty);
                    RequestBody skId = RequestBody.create(okhttp3.MultipartBody.FORM, shopkeeperId);
                    RequestBody catId = RequestBody.create(okhttp3.MultipartBody.FORM, category.getCategoryId());

                    ProgressDialog pd = new ProgressDialog(AddProductActivity.this);
                    pd.setMessage("Please wait while adding product");
                    pd.show();
                    ProductService.ProductApi productApi = ProductService.getProductApiInstance();
                    Call<Product> call = productApi.saveProduct(body,catId,skId,productName,productPrice,productBrand,productQty,productDescription,productDiscount);
                    call.enqueue(new Callback<Product>() {
                        @Override
                        public void onResponse(Call<Product> call, Response<Product> response) {
                            pd.dismiss();
                            if(response.code() == 200){
                                Toast.makeText(AddProductActivity.this, "Product saved", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                Toast.makeText(AddProductActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Product> call, Throwable t) {
                            Toast.makeText(AddProductActivity.this, ""+t, Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
                else
                    Toast.makeText(AddProductActivity.this, "Plz select product image", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            binding.ivProductImage.setImageURI(imageUri);
        }
    }
}

