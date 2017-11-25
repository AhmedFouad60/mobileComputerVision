package com.example.foush.computerVision;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.glidebitmappool.GlideBitmapFactory;
import com.glidebitmappool.GlideBitmapPool;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileproviderVision";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static Uri photoURI;
    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.title_text_view)
    TextView titleTextView;
    @BindView(R.id.Gobutton)
    Button Gobutton;
    @BindView(R.id.clear_button)
    FloatingActionButton clearButton;
    @BindView(R.id.save_button)
    FloatingActionButton saveButton;
    @BindView(R.id.share_button)
    FloatingActionButton shareButton;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;

    private String mTempPhotoPath;

    private Bitmap mResultsBitmap;
    File photoFile = null;
    private Bitmap theBitmap = null;

    //save the uri of the 3 photos taken
    private static List<String>photosUriList=new ArrayList<String>();
    private static int count=0;
    private Bitmap tempBitmap;
    private static List<File>filesList=new ArrayList<File>();
    private static String FoushTest="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        GlideBitmapPool.initialize(10 * 1024 * 1024); // 10mb max memory size




    }

    @OnClick(R.id.Gobutton)
    public void onViewClicked() {


        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Launch the camera if the permission exists
            launchCamera();
        }

    }

    /**
     * Creates a temporary image file and captures a picture to store in it.
     */
    private void launchCamera() {

        // Create the capture image intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the temporary File where the photo should go

            try {
                photoFile = BitmapUtils.createTempImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                // Get the path of the temporary file
                mTempPhotoPath = photoFile.getAbsolutePath();
                Log.d(TAG, "launchCamera: photo uri uri uri uri uri uri is  "+mTempPhotoPath);



                // Get the content URI for the image file
                 photoURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile);

                // Add the URI so the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the image capture activity was called and was successful
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {


            // Process the image and set it to the TextView
            try {
                processAndSetImage();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

            // Otherwise, delete the temporary image file
            BitmapUtils.deleteImageFile(this, mTempPhotoPath);
        }
    }

    /**
     * Method for processing the captured image and setting it to the TextView.
     */
    @SuppressLint({"CheckResult", "StaticFieldLeak"})
    private void processAndSetImage() throws ExecutionException, InterruptedException {
        //Toggle Visibility of the Views
        Gobutton.setVisibility(View.GONE);
        titleTextView.setVisibility(View.GONE);
        saveButton.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.VISIBLE);
        clearButton.setVisibility(View.VISIBLE);

        // Resample the saved image to save memory
       // mResultsBitmap = BitmapUtils.resamplePic(this, mTempPhotoPath);

        //Load the Image with it's uri

        Bitmap myBitmap = GlideBitmapFactory.decodeFile(mTempPhotoPath);
        imageView.setImageBitmap(myBitmap);


        //create a paint object for drawing with
        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);

        //create a canvas object for drawing on
        tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        //create the face detector
        FaceDetector faceDetector = new
               FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                .build();
        if(!faceDetector.isOperational()){
            new AlertDialog.Builder(this).setMessage("Could not set up the face detector!").show();
           return;

        }

        //Detect faces
        //Draw Rectangles on the faces


        //Detect the Faces
        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        //Draw Rectangles on the Faces
        for(int i=0; i<faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
        }
        imageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));

        /**save the image and get the images path*/

            // Delete the temporary image file
            BitmapUtils.deleteImageFile(this, mTempPhotoPath);
            // Save the image
            String imagPath=BitmapUtils.saveImage(this, tempBitmap);
            photosUriList.add(imagPath);
            count++;
        Log.d(TAG, "processAndSetImage: my array is  "+photosUriList.toString());


        /**write fuction to cut the only the face with rectangle drawing */


        /**Write function to zip this cropped photo**/



        if(count==3) {
            ZipUtil.pack(new File(""+BitmapUtils.storageDir), new File(""+BitmapUtils.storageDir+".zip"));
            ZipUtil.unexplode(new File(""+BitmapUtils.storageDir+".zip"));


        }


        /*** write function to send the ziped file to the server [moustafa]*/




















    }

    @OnClick({R.id.clear_button, R.id.save_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.clear_button:
                break;
            case R.id.save_button:
                // Delete the temporary image file
                BitmapUtils.deleteImageFile(this, mTempPhotoPath);

                // Save the image
                BitmapUtils.saveImage(this, tempBitmap);



                break;
        }
    }




}
