package com.example.hardwaremart;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.hardwaremart.databinding.HomeBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    HomeBinding binding;
    String currentUserId;
    CategoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.homeToolBar);
        getSupportActionBar().setTitle("HardwareMart");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        CategoryService.CategoryApi categoryApi = CategoryService.getCategoryApiInstance();

        Call<ArrayList<Category>>call = categoryApi.getCategoryList();

        call.enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(Call<ArrayList<Category>> call, Response<ArrayList<Category>> response) {
                if(response.code()==200){
                    ArrayList<Category>categoryList = response.body();
                    adapter = new CategoryAdapter(HomeActivity.this,categoryList);
                    binding.rv.setAdapter(adapter);
                    binding.rv.setLayoutManager(new GridLayoutManager(HomeActivity.this,2));
                    adapter.setOnItemClickListener(new CategoryAdapter.OnRecyclerClick() {
                        @Override
                        public void onItemClickListener(Category category, int position) {
                            Intent in = new Intent(HomeActivity.this,ProductActivity.class);
                            in.putExtra("category", category);
                            startActivity(in);
                        }
                    });
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<ArrayList<Category>> call, Throwable t) {

            }
        });
        binding.fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(HomeActivity.this,AddProductActivity.class);
                startActivity(in);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserProfile();
    }
    private void checkUserProfile(){
        StoreService.StoreApi storeApi = StoreService.getStoreApiInstance();
        Call<Store> call = storeApi.getShopKeeperProfile(currentUserId);
        call.enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if(response.code()==200){
                    Store store = response.body();
                    Toast.makeText(HomeActivity.this,"Profile already created",Toast.LENGTH_SHORT).show();
                }
                else if (response.code()==404){
                    Toast.makeText(HomeActivity.this,"Please create profile first",Toast.LENGTH_SHORT).show();
                    sendUserToProfileActivity();
                }
                else
                    Toast.makeText(HomeActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                Toast.makeText(HomeActivity.this,"Internal server error ,Try Again later",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendUserToProfileActivity(){

       // Log.e("log in","HomeActivity");
        Intent in = new Intent(HomeActivity.this,ProfileActivity.class);
        startActivity(in);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("LogOut");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = item.getTitle().toString();
        if(title.equals("LogOut")){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            sendUserToLoginActivity();
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendUserToLoginActivity(){
        Intent in = new Intent(this,LoginActivity.class);
        startActivity(in);
        finish();
    }
}
