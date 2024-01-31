package com.example.appdyr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView text;
    EditText searchField;
    ImageButton searchButton;
    public static RequestQueue requestQueue;

    LinearLayout searchBar;
    Spinner searchResultSpinner;

    List<PokeCard> allCards;

    List<Player> player;

    int deckSize = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGui();


        requestQueue = Volley.newRequestQueue(this);
        searchButton.setOnClickListener(this);
        getAllCards();

    }

    private void initGui() {
        searchField = findViewById(R.id.et_search);
        searchButton = findViewById(R.id.btn_search);
        searchResultSpinner = findViewById(R.id.sp_searchresult);
        searchBar = findViewById(R.id.ll_searchbar);


    }

    @Override
    public void onClick(View view) {
        //TODO call Api with searchtext
        if (allCards == null) return;
        List<PokeCard> searchList = new ArrayList<>();
        for (PokeCard pc : allCards) {
            if (pc.name.toLowerCase().contains(searchField.getText().toString().toLowerCase())) {
                searchList.add(pc);
            }

        }
        createSpinner(searchList);
    }

    private void createSpinner(List<PokeCard> pcList) {
        List<String> pcNames = pcList.stream().map((PokeCard) -> PokeCard.name + ":" + PokeCard.id)
                .collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_dropdown_item, pcNames);
        searchResultSpinner.setAdapter(adapter);


        searchResultSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstTime = true;

            @Override
            public void onItemSelected(AdapterView<?> AdapterView, View view, int index, long l) {
                if (firstTime) {
                    firstTime = false;
                    return;
                }
                String id = pcNames.get(index).split(":")[1];
                Intent intent = new Intent(view.getContext(), CardActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getAllCards() {
        String url = "https://api.tcgdex.net/v2/en/cards";

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            allCards = new Gson().fromJson(response, new TypeToken<List<PokeCard>>() {
            }.getType());
            Toast.makeText(this, "cards:" + allCards.size(), Toast.LENGTH_LONG).show();

            onInitPlayer();
            searchBar.setVisibility(View.VISIBLE);
            Log.d("Cards", String.valueOf(allCards.size()));

        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();

        });
        requestQueue.add(request);
    }

    public void addCardsToPlayer(Player player) {
        Random rnd = new Random();
        for (int i = 0; i < deckSize; i++) {
            int numList = rnd.nextInt(allCards.size());
            PokeCard card = allCards.get(numList);
            getCard(card.id ,player);
        }
    }

    private void startBattle() {


    }

    private void onInitPlayer() {
        Player p1 = new Player();
        Player p2 = new Player();

        player = new ArrayList<Player>() {{
            add(p1);
            add(p2);
        }};

        for (Player player : player) {
            player.cards = new ArrayList<>();
            addCardsToPlayer(player);
        }
    }

    private void getCard(String id, Player player) {
        String url = "https://api.tcgdex.net/v2/en/cards/" + id;

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            Card card = new Gson().fromJson(response, Card.class);
            player.cards.add(card);
            Toast.makeText(this, "Added " + card.name, Toast.LENGTH_LONG).show();
        }, error -> {
        });
        MainActivity.requestQueue.add(request);
    }
}