package com.example.bookreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class PlayActivity extends AppCompatActivity {

    String TAG = "audio";
    TextView textView;
    SeekBar seekBar;
    Button button;
    Timer timer;
    Boolean audioPlaying;
    private SimpleExoPlayer player;
    private boolean playWhenReady;
    private long playbackPosition;

    private MediaSource buildMediaSource(Uri uri) {
        Log.d(TAG,"mediasource");
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    public void initializePlayer(){
        Log.d(TAG,"initialize player");
        player = ExoPlayerFactory.newSimpleInstance(this);

        File file = getAudioFile();
        Uri uri = Uri.fromFile(file);
        MediaSource mediaSource = buildMediaSource(uri);

        player.prepare(mediaSource, false, false);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(playbackPosition);

    }

    private void releasePlayer() {
        if (player != null) {
            Log.d(TAG,"release player");
            player.release();
            player = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Log.d(TAG, "onCreate");
        audioPlaying = false;
        playWhenReady = true;
        playbackPosition = 0;
        textView = findViewById(R.id.textView);
        seekBar = findViewById(R.id.seekBar);
        button = findViewById(R.id.button);

        initializePlayer();
        getTextFile();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseButtonEvent();
            }
        });

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(player!=null) {
                    Log.d(TAG, "timer");
                    seekBar.setProgress((int)((player.getCurrentPosition()*100)/player.getDuration()));
                }
            }
        },0,1000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(player != null && fromUser) {
                    Log.d(TAG, "seekbar changed");
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    protected void getTextFile(){
        try{
            File path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File file = new File(path,"image_to_text.txt");
        FileInputStream stream = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer();
        String s = br.readLine();
        while (s != null) {
            buffer.append(s + "\n");
            s = br.readLine();
        }
        textView.setText(buffer.toString().trim());
        br.close();
        reader.close();
        stream.close();
    } catch (Exception ex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Error");
        builder.setMessage(ex.getMessage());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    }

    protected File getAudioFile(){
        Log.d(TAG,"get audio file");
        File path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path,"audio.mp3");
        return file;
    }

    protected void playPauseButtonEvent(){
        if(player.getPlayWhenReady()){
            Log.d(TAG,"pause");
            Toast.makeText(getApplicationContext(),"Pause",Toast.LENGTH_SHORT).show();
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }else {
            Log.d(TAG,"play");
            Toast.makeText(getApplicationContext(),"Play",Toast.LENGTH_SHORT).show();
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}
