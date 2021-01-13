package com.example.hardwaremart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.hardwaremart.databinding.ProductActivityBinding;
import com.example.hardwaremart.databinding.ProductDetailActivityBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    ProductDetailActivityBinding binding;
    Product product;
    String currentUserId;
    ArrayList<Product>productList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProductDetailActivityBinding.inflate(LayoutInflater.from(ProductDetailActivity.this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.homeToolBar);
        getSupportActionBar().setTitle("Product Detail");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent in = getIntent();
        product = (Product) in.getSerializableExtra("productDescription");

        Picasso.get().load(product.getImageUrl()).into(binding.ivProductImage);

        binding.tvProductName.setText("Name: "+product.getName());
        binding.tvDiscount.setText("Discount:% "+product.getDiscount());
        binding.tvQtyInStock.setText("Quantity: "+product.getQtyInStock());
        binding.tvDescription.setText("Description: "+product.getDescription());
        binding.tvPrice.setText("Price:₹ "+product.getPrice());
        ProductService.ProductApi productApi = ProductService.getProductApiInstance();
        Call<List<Product>> call = productApi.getProductByCategoryAndShopKeeper(product.getCategoryId(), currentUserId);
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("please wait ...");
        pd.show();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                pd.dismiss();
                if(response.code() == 200){
                    productList = (ArrayList<Product>) response.body();
                    //adapter = new ProductAdapter(ProductDetailActivity.this, productList);
                    //binding.rvProductDetail.setAdapter(adapter);
                    //binding.rvProductDetail.setLayoutManager(new GridLayoutManager(ProductDetailActivity.this,2));
                }
                else
                    Toast.makeText(ProductDetailActivity.this, response.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(ProductDetailActivity.this, ""+t, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
