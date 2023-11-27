package edu.uci.ics.fabflixmobile.ui.singlemovie;

import android.util.Log;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SingleMovieActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        final ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONArray movieArr = new JSONArray(this.getIntent().getStringExtra("movie"));
            JSONObject movie = new JSONObject(movieArr.get(0).toString());
            movies.add(new Movie(movie.getString("title"),
                        (short) Integer.parseInt(movie.getString("year")),
                        movie.getString("director"),
                        movie.getString("genres"),
                        movie.getString("stars"),
                        movie.getString("id")));

            SingleMovieViewAdapter adapter = new SingleMovieViewAdapter(this, movies);
            ListView listView = findViewById(R.id.list);
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            Log.d("list.error", e.toString());
        }
    }
}