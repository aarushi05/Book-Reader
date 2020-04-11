package com.example.bookreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class  OcrDetectorProcessor {
    private static final String TAG = "BookReader";
    String ocrText;

    public String getOcrText() {
        return ocrText;
    }

    public OcrDetectorProcessor(Context context, Bitmap bitmap){
        TextRecognizer txtRecognizer = new TextRecognizer.Builder(context).build();
        if (!txtRecognizer.isOperational()) {
//            txtView.setText(R.string.error_prompt);
            Log.e("sonal","Error");
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray items = txtRecognizer.detect(frame);
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = (TextBlock) items.valueAt(i);
                strBuilder.append(item.getValue());
                strBuilder.append("/");
                for (Text line : item.getComponents()) {
                    //extract scanned text lines here
                    Log.v("lines", line.getValue());
                    for (Text element : line.getComponents()) {
                        //extract scanned text words here
                        Log.v("element", element.getValue());

                    }
                }
            }
            Log.d(TAG,"text ocr"+ strBuilder.toString());
            if(strBuilder.toString()=="")
                this.ocrText = "Sorry your image cannot be converted to audio";
            else {
                String text = strBuilder.toString().substring(0, strBuilder.toString().length() - 1);
                this.ocrText = text;
            }
        }
    }

}