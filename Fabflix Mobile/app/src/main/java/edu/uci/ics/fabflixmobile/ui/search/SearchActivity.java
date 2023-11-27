package edu.uci.ics.fabflixmobile.ui.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivitySearchBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private EditText searchText;
    private final String host = "3.138.33.88";
    private final String port = "8443";
    private final String domain = "cs122b-project4";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        searchText = binding.searchText;
        final Button searchButton = binding.search;

        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> search());
    }

    @SuppressLint("SetTextI18n")
    public void search() {
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is GET
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/form" +
                        "?title="  + searchText.getText().toString() +
                        "&year=&director=&star=&sort=1&n=10&page=1",
                response -> {
                    Log.d("search.success", response);

                    finish();

                    // Initialize the activity/page/destination
                    Intent listPage = new Intent(SearchActivity.this, MovieListActivity.class);
                    listPage.putExtra("movies", response);

                    // Activate the list page
                    startActivity(listPage);
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                });
        // important: queue.add is where the login request is actually sent
        queue.add(searchRequest);
    }
}