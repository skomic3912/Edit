package com.komikomi.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import
import android.media.FaceDetector.Face;
import android.util.

import java.io.FileDescriptor;
import java.io.IOException;


public class main extends Activity {

    private static final int RESULT_PICK_IMAGEFILE = 1001;
    private ImageView imageView;
    private Button button;
    private TextView dcimPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.gg_button);
        imageView = (ImageView) findViewById(R.id.imageView);
        dcimPath = (TextView) findViewById(R.id.gtview);
        dcimPath.setText("Garally's Path:　" + getGalleryPath());

        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Detector<Face> safeDetector = new SafeFaceDetector(detector);

        Frame frame = new Frame.Builder().setBitmap(bmp).build();
        SparseArray<Face> faces = safeDetector.detect(frame);

        if (faces.size() > 0) {
            String text =  "SmilingProbability: " + String.format("%.2f", faces.get(0).getIsSmilingProbability());
            Toast.makeText(this,text, Toast.LENGTH_LONG).show();
        }
        //activity following here
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Filter to show only images, using the image MIME data type.
                // it would be "*/*".
                intent.setType("image/*");

                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
            }
        });
    }
    private String getGalleryPath() {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("", "Uri: " + uri.toString());

                try {
                    Bitmap bmp = getBitmapFromUri(uri);
                    imageView.setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }



    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

}
