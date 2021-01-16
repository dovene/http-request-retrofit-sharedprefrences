package com.dov.httprequestlocalestorage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.dov.httprequestlocalestorage.databinding.ActivityMainBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String ID_KEY = "ID_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        if (getIdFromSharedPreferences() != 0) {
            binding.idEt.setText(String.valueOf(getIdFromSharedPreferences()));
        }
    }

    private void search() {
        if (TextUtils.isEmpty(binding.idEt.getText().toString())) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setMessage("Veuillez saisir un entier");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return;
        }
        callService();
    }

    private void callService() {
        AlbumApi.AlbumService service = AlbumApi.getInstance().getClient().create(AlbumApi.AlbumService.class);
        Call<List<Album>> call = service.getBooks(Integer.valueOf(binding.idEt.getText().toString()));
        call.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                updateView(response);
                writeIdToSharePreferences();
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {

            }
        });
    }

    private void updateView(Response<List<Album>> response) {
        if (response.body().size() > 0) {
            binding.photoTitle.setText(response.body().get(0).getTitle());
            //API https://via.placeholder.com requires user agent while dealing with images so we need a little addition before querying images
            GlideUrl glideUrl = new GlideUrl(response.body().get(0).getThumbnailUrl(), new LazyHeaders.Builder()
                    .addHeader("User-Agent", WebSettings.getDefaultUserAgent(getApplicationContext()))
                    .build());
            Glide.with(getApplicationContext()).load(glideUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.photoImage);
        }
    }

    private void writeIdToSharePreferences() {
        getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putInt(ID_KEY, Integer.valueOf(binding.idEt.getText().toString()))
                .apply();
    }

    private int getIdFromSharedPreferences() {
        return getPreferences(Context.MODE_PRIVATE).getInt(ID_KEY, 0);
    }

}