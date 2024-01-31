package com.example.appdyr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CardActivity extends AppCompatActivity {

    Card card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("id");
        getCard(id);
    }

    private void getCard(String id) {
        String url = "https://api.tcgdex.net/v2/en/cards/" + id;

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            card = new Gson().fromJson(response, Card.class);
            ((TextView)findViewById(R.id.tv_name)).setText(card.name);
            getImage();
        }, error -> {
        });
        MainActivity.requestQueue.add(request);
    }

    private  void getImage(){
        ImageView cardImage = findViewById(R.id.img_card);

        Picasso.get().load(card.image+"/high.jpg").into(cardImage);

    }
}