package com.rsmbloqueador.rsmbloqueador;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import android.view.*;
import android.widget.*;

public class ActivityTutoriais extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    // URL dos vídeos
    private final String URL_APP_RSM = "GSP5wYqvlBA";
    private final String URL_PLATAFORMA = "BoTt_OExna0";
    private final String URL_PLATAFORMA_DESKTOP = "UJYV0Ty8VwQ";
    //----------------------------------------------------------

    // API KEY
    private final String API_KEY = "AIzaSyBfjzWXPW_ClRJYBawAfOuLqny6Z3LJU7w";
    //-----------------------------------------------------------------------

    private YouTubePlayerSupportFragment fragYoutubePlay;
    private TextView txtYoutubePlay;
    private ImageView imageViewAppRsm;
    private TextView textViewAppRsm;
    private ImageView imageViewPlataforma;
    private TextView textViewPlataforma;
    private ImageView imageViewPlataformaDesktop;
    private TextView textViewPlataformaDesktop;

    // Config
    private int currentVideo;
    private boolean statusPlay;
    //-------------------------

    public ActivityTutoriais(){
        this.currentVideo = 0;
        statusPlay = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoriais);

        // Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_home_tutoriais);
        //---------------------------------------------------------------------

        txtYoutubePlay = (TextView) findViewById(R.id.txtYoutubePlay);
        imageViewAppRsm = (ImageView) findViewById(R.id.imageViewAppRsm);
        textViewAppRsm = (TextView) findViewById(R.id.textViewAppRsm);
        imageViewPlataforma = (ImageView) findViewById(R.id.imageViewPlataforma);
        textViewPlataforma = (TextView) findViewById(R.id.textViewPlataforma);
        imageViewPlataformaDesktop = (ImageView) findViewById(R.id.imageViewPlataformaDesktop);
        textViewPlataformaDesktop = (TextView) findViewById(R.id.textViewPlataformaDesktop);

        // Salvar contexto
        if(savedInstanceState != null){
            int valor = savedInstanceState.getInt("CurrentVideo");
            if(valor >= 0){
                currentVideo = valor;
                statusPlay = savedInstanceState.getBoolean("StatusPlay");
                saveInstanceState(currentVideo);
            }
        }
        //--------------------------------------------------------------------

        // Iniciar primeiro vídeo
        fragYoutubePlay = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtubePlay);
        fragYoutubePlay.initialize(API_KEY, this);

        imageViewAppRsm.setOnClickListener(new ViewFlipper.OnClickListener() {
            @Override
            public void onClick(View v) {
                appRsm();
            }
        });
        //--------------------------------------------------------------------------------------------------------------

        textViewAppRsm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appRsm();
            }
        });

        imageViewPlataforma.setOnClickListener(new ViewFlipper.OnClickListener() {
            @Override
            public void onClick(View v) {
                plataforma();
            }
        });

        textViewPlataforma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plataforma();
            }
        });

        imageViewPlataformaDesktop.setOnClickListener(new ViewFlipper.OnClickListener() {
            @Override
            public void onClick(View v) {
                plataformaDesktop();
            }
        });

        textViewPlataformaDesktop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plataformaDesktop();
            }
        });
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlaybackEventListener(playbackEventListener);

        if(!b){
            String url;
            switch (currentVideo){
                case 0:
                case 1:
                    url = URL_APP_RSM; break;
                case 2:
                    url = URL_PLATAFORMA; break;
                case 3:
                    url = URL_PLATAFORMA_DESKTOP; break;
                default:
                    url = URL_APP_RSM;
            }
            if(currentVideo > 0){
                youTubePlayer.loadVideo(url);
            }else{
                youTubePlayer.cueVideo(url);
            }
        }

        if(statusPlay){
            youTubePlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener(){
        @Override
        public void onPlaying() {
            statusPlay = true;
        }

        @Override
        public void onPaused() {
            statusPlay = false;
        }

        @Override
        public void onStopped() {}

        @Override
        public void onBuffering(boolean b) {}

        @Override
        public void onSeekTo(int i) {}
    };

    public void appRsm(){
        if(currentVideo != 1) {
            currentVideo = 1;
            statusPlay = true;
            saveInstanceState(currentVideo);
            restartYouTubePlay();
        }
    }

    public void plataforma(){
        if(currentVideo != 2) {
            currentVideo = 2;
            statusPlay = true;
            saveInstanceState(currentVideo);
            restartYouTubePlay();
        }
    }

    public void plataformaDesktop(){
        if(currentVideo != 3){
            currentVideo = 3;
            statusPlay = true;
            saveInstanceState(currentVideo);
            restartYouTubePlay();
        }
    }

    public void restartYouTubePlay(){
        fragYoutubePlay = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtubePlay);
        fragYoutubePlay.onDestroy();
        fragYoutubePlay.initialize(API_KEY, this);
    }

    public void saveInstanceState(int currentVideo){
        switch (currentVideo){
            case 1:
                txtYoutubePlay.setText("Tutorial: App RSM Bloqueador");
                imageViewAppRsm.setImageResource(R.drawable.tutorial_app_rsm_pressed);
                imageViewPlataforma.setImageResource(R.drawable.tutorial_plataforma);
                imageViewPlataformaDesktop.setImageResource(R.drawable.tutorial_plataforma_desktop);

                textViewAppRsm.setTextColor(getResources().getColor(R.color.colorAccent));
                textViewPlataforma.setTextColor(getResources().getColor(R.color.colorBlack));
                textViewPlataformaDesktop.setTextColor(getResources().getColor(R.color.colorBlack));
                break;
            case 2:
                txtYoutubePlay.setText("Tutorial: Plataforma Online");
                imageViewAppRsm.setImageResource(R.drawable.tutorial_app_rsm);
                imageViewPlataforma.setImageResource(R.drawable.tutorial_plataforma_pressed);
                imageViewPlataformaDesktop.setImageResource(R.drawable.tutorial_plataforma_desktop);

                textViewAppRsm.setTextColor(getResources().getColor(R.color.colorBlack));
                textViewPlataforma.setTextColor(getResources().getColor(R.color.colorAccent));
                textViewPlataformaDesktop.setTextColor(getResources().getColor(R.color.colorBlack));
                break;
            case 3:
                txtYoutubePlay.setText("Tutorial: Plataforma Online pelo Desktop");
                imageViewAppRsm.setImageResource(R.drawable.tutorial_app_rsm);
                imageViewPlataforma.setImageResource(R.drawable.tutorial_plataforma);
                imageViewPlataformaDesktop.setImageResource(R.drawable.tutorial_plataforma_desktop_pressed);

                textViewAppRsm.setTextColor(getResources().getColor(R.color.colorBlack));
                textViewPlataforma.setTextColor(getResources().getColor(R.color.colorBlack));
                textViewPlataformaDesktop.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CurrentVideo", currentVideo);
        outState.putBoolean("StatusPlay", statusPlay);
    }
}