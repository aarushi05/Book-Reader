package com.example.bookreader;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayActivity extends AppCompatActivity {

    String TAG = "audio";
    ImageView imageView;
    SeekBar seekBar;
    Button button;
    Boolean audioPlaying;
    Boolean startMedia;
    MediaPlayer mediaPlayer;
    Timer timer;
    private static int  sTime =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Log.d(TAG,"onCreate");
        audioPlaying=false;
        startMedia = false;
        imageView = findViewById(R.id.imageView);
        seekBar = findViewById(R.id.seekBar);
        button = findViewById(R.id.button);

        mediaPlayer = new MediaPlayer();
        FileInputStream fis = null;
        try {
            mediaPlayer.reset();
            File file = getAudioFile();
            fis = new FileInputStream(file);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG,"Prepared");
                    seekBar.setMax(mediaPlayer.getDuration());
                    startMedia = true;
                }
            });
        } catch(Exception e) {
            String msg = e.getMessage();
            Log.d(TAG,"exception "+ msg );
        }finally {
            if(fis!=null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG,"onCompletionListener");
                audioPlaying = false;
            }

        });

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
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        },0,1000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    protected File getAudioFile(){
        Log.d(TAG,"get Audio File");
        File path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path,"audio.mp3");
        return file;
    }

    protected void playPauseButtonEvent(){
        if(audioPlaying && startMedia){
            mediaPlayer.pause();
            audioPlaying=false;
            seekBar.setProgress(sTime);
        }else if (audioPlaying==false && startMedia){
            mediaPlayer.start();
            audioPlaying=true;
        }else if(!startMedia){
            Log.d(TAG,"not prepared");
            Toast.makeText(getApplicationContext(),"Media Player not Prepared",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        timer.cancel();
    }
}
