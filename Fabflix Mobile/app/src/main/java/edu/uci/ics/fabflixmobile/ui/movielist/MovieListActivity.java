package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    private final String host = "3.138.33.88";
    private final String port = "8443";
    private final String domain = "cs122b-project4";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        // TODO: this should be retrieved from the backend server
        final ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONArray respArr = new JSONArray(this.getIntent().getStringExtra("movies"));
            for(int i = 0; i < respArr.length(); ++i){
                JSONObject movieJsonObject = respArr.getJSONObject(i);
                /*System.out.println(movieJsonObject.getString("title"));
                System.out.println(movieJsonObject.getString("year"));
                System.out.println(movieJsonObject.getString("director"));
                System.out.println("genres: " + movieJsonObject.getString("genres"));
                System.out.println("stars: " + movieJsonObject.getString("stars"));*/
                movies.add(new Movie(movieJsonObject.getString("title"),
                        (short) Integer.parseInt(movieJsonObject.getString("year")),
                        movieJsonObject.getString("director"),
                        movieJsonObject.getString("genres"),
                        movieJsonObject.getString("stars"),
                        movieJsonObject.getString("id")));
            }

            MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
            ListView listView = findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Movie movie = movies.get(position);
                final RequestQueue queue = NetworkManager.sharedManager(this).queue;
                // request type is GET
                final StringRequest searchRequest = new StringRequest(
                        Request.Method.GET,
                        baseURL + "/api/single-movie" +
                                "?id=" + movie.getId(),
                        response -> {
                            Log.d("singlemovie.success", response);

                            finish();

                            // Initialize the activity/page/destination
                            Intent singleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                            singleMoviePage.putExtra("movie", response);

                            // Activate the list page
                            startActivity(singleMoviePage);
                        },
                        error -> {
                            // error
                            Log.d("singlemovie.error", error.toString());
                        });
                // important: queue.add is where the login request is actually sent
                queue.add(searchRequest);
            });
        } catch (JSONException e) {
            Log.d("list.error", e.toString());
        }
    }
}