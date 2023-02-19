package com.example.android.memegenerataor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.OnSwipe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.memegenerataor.network_utilities.NetworkUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.sax.SAXResult;

public class MainActivity extends AppCompatActivity {


    public static final String URL_STRING = "https://meme-api.herokuapp.com/gimme/";

    ImageView meme_img;
    ImageView shareButton,nextButton,searchButton;
    Bitmap bitmap;
    ProgressBar progressBar;
    EditText editText;
    String url_query="IndianDankMemes";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meme_img = findViewById(R.id.imageView);
        nextButton = findViewById(R.id.imageView2);
        shareButton = findViewById(R.id.imageView3);
        searchButton = findViewById(R.id.imageView4);
        progressBar = findViewById(R.id.progressBar);
        editText = findViewById(R.id.editTextTextPersonName);
        editText.setVisibility(View.INVISIBLE);

        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);

        String saved_query = sharedPreferences.getString("url_query",url_query);
        MemeAsyncTask memeAsyncTask = new MemeAsyncTask();
        memeAsyncTask.execute(URL_STRING+saved_query+"/1");

        meme_img.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeLeft() {
                String saved_query = sharedPreferences.getString("url_query",url_query);
                MemeAsyncTask memeAsyncTask = new MemeAsyncTask();
                memeAsyncTask.execute(URL_STRING+saved_query+"/1");
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getVisibility() == View.INVISIBLE){
                    editText.setVisibility(View.VISIBLE);
                }else{
                    url_query = editText.getText().toString();
                    sharedPreferences.edit().putString("url_query", url_query).apply();
                    editText.setVisibility(View.INVISIBLE);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String saved_query = sharedPreferences.getString("url_query",url_query);
                MemeAsyncTask memeAsyncTask = new MemeAsyncTask();
                memeAsyncTask.execute(URL_STRING+saved_query+"/1");
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image Description", null);
                Uri uri = Uri.parse(path);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                startActivity(Intent.createChooser(intent, "Share Image"));
            }
        });
    }
    public class MemeAsyncTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length < 1 || strings[0] == null) {
                return null;
            }
            String imageUrl = NetworkUtils.fetchEarthquakeData(strings[0]);
            return imageUrl;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String string) {
            Log.d("check", "onPostExecute: "+string);
            ImageAsyncTask imageAsyncTask = new ImageAsyncTask(meme_img);
            imageAsyncTask.execute(string);
        }
    }
    private class ImageAsyncTask extends AsyncTask<String,Void, Bitmap>{
        ImageView imageView;

        ImageAsyncTask(ImageView imageView){
            this.imageView = imageView;

        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            bitmap = null;
            if (url == null) {
                url = "https://im.ge/i/F5hdSS";
                InputStream inputStream = null;
                try {
                    inputStream = new URL(url).openStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }
            try {
                InputStream inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressBar.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(bitmap);
        }
    }
}