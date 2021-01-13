package com.example.hardwaremart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hardwaremart.databinding.CategoryItemListBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    Context context;
    OnRecyclerClick listener;
    ArrayList<Category> al;

    public CategoryAdapter(Context context, ArrayList<Category>al){
        this.context = context;
        this.al = al;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryItemListBinding binding = CategoryItemListBinding.inflate(LayoutInflater.from(context),parent,false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = al.get(position);
        Picasso.get().load(category.getImageUrl()).into(holder.binding.iv);
        holder.binding.tvCategory.setText(category.getCategoryName());
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        CategoryItemListBinding binding;
        public CategoryViewHolder(CategoryItemListBinding binding){
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Category c = al.get(position);
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClickListener(c,position);
                    }
                }
            });
        }
    }
    public interface  OnRecyclerClick{
        void onItemClickListener(Category category, int position);
    }
    public void setOnItemClickListener(OnRecyclerClick listener){
        this.listener =  listener;
    }
}
