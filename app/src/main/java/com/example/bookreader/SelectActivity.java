package com.example.bookreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Locale;

import static com.example.bookreader.PictureContent.loadSavedImages;

public class SelectActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener{
    private static final String TAG = "BookReader";
    TextToSpeech mtts;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        if (recyclerViewAdapter == null) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            recyclerView = (RecyclerView) currentFragment.getView();
            recyclerViewAdapter = ((RecyclerView) currentFragment.getView()).getAdapter();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadSavedImages(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    protected File getFileFromUri(Uri uri){
        //urifile:///storage/emulated/0/Android/data/com.example.bookreader/files/Pictures/JPEG_20200407_8179335626432330184.jpg
        String[] split = uri.toString().split("/");
        String filename = split[split.length - 1];
        File file = new File(Environment.DIRECTORY_PICTURES,filename);
        return file;
    }

    protected void getImageToText(File file) {
        File path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File textfile = new File(path,"image_to_text.txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(textfile);
            File path2 = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String inputFileName= path2.toString() + "/" + file.getName();
            Bitmap bitmap = BitmapFactory.decodeFile(inputFileName);
            OcrDetectorProcessor ocrText= new OcrDetectorProcessor(getApplicationContext(),bitmap);
            String text= ocrText.getOcrText();
            Log.d(TAG,"text " + text);
            //text = "hi i am aarushi this is dummy text just to check the text file converted to audio and audio file playing in the audio player correctly or not file should be of maximum length so that audio file is of maximum length so that seekbar can be checked by progress and audio playing can be done backward or forward i hope this works now and media player get prepared properly pls dont give errors now for media player preparing failed or ocr failed or whatever";
            writer.append(text);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    StringBuffer buffer;
    protected void getTextToAudio(){
        final String mUtteranceID = "totts";
        File path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File textFile = new File(path,"image_to_text.txt");
        try {
            FileInputStream fileInputStream = new FileInputStream(textFile);
            InputStreamReader is = new InputStreamReader(fileInputStream);
            BufferedReader in = new BufferedReader(is);
            String line;
            buffer = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buffer.append(line).append(' ');
            }
            buffer.deleteCharAt(buffer.length() - 1);
        }catch(Exception e){
            String msg = e.getMessage();
            Log.d(TAG,msg);
        }
        final File audioFile = new File(path,"audio.mp3");
         mtts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mtts.setLanguage(Locale.US);
                    mtts.synthesizeToFile(buffer.toString() , null , audioFile , mUtteranceID);
                }
            }
        });
    }

    @Override
    public void onListFragmentInteraction(PictureItem item) {
            Uri uri = item.uri;
            File file = getFileFromUri(uri);
            getImageToText(file);
            getTextToAudio();
            Intent intent = new Intent(getApplicationContext(),PlayActivity.class);
            startActivity(intent);
    }
}
