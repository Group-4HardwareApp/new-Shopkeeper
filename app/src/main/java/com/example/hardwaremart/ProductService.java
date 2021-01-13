package com.example.hardwaremart;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class ProductService {
    public static ProductApi productApi = null;

    public static ProductApi getProductApiInstance(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAddress.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if(productApi == null)
            productApi = retrofit.create(ProductApi.class);
        return productApi;
    }


    public interface ProductApi{
        @Multipart
        @POST("/product/")
        public Call<Product> saveProduct(@Part MultipartBody.Part file,
                                         @Part("categoryId")RequestBody categoryId,
                                         @Part("shopKeeperId") RequestBody shopKeeperId,
                                         @Part("name") RequestBody name,
                                         @Part("price") RequestBody price,
                                         @Part("brand") RequestBody brand,
                                         @Part("qtyInStock") RequestBody qtyInStock,
                                         @Part("description") RequestBody description,
                                         @Part("discount") RequestBody discount);

        @GET("/product/productlist/{categoryId}/{shopkeerperId}")
        public Call<List<Product>> getProductByCategoryAndShopKeeper(@Path("categoryId") String categoryId,
                                                                     @Path("shopkeerperId")String shopkeerperId);
    }

}
