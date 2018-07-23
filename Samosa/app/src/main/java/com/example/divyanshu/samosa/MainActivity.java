package com.example.divyanshu.samosa;

import android.Manifest;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.tensorflow.lite.Interpreter;


public class MainActivity extends AppCompatActivity {
    int REQUEST_IMAGE_CAPTURE = 1;
    int WRITE_EXTERNAL_STORAGE = 2;
    TextView result;
    Uri file;
    Bitmap bitmap = null;
    Bitmap bm = null;
    Interpreter tflite1, tflite2, tflite3;
    String MODEL_FILE1 = "inp_CL4.tflite";
    String MODEL_FILE2 = "UPSMPL_CL5.tflite";
    String MODEL_FILE3 = "UPSMPL2_CL6.tflite";

    int pich = 256;
    int picw = 256;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_IMAGE_CAPTURE);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                WRITE_EXTERNAL_STORAGE);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                3);
        Button button = (Button) findViewById(R.id.launch);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        //Load the Tensorflow Interpreter

        {
            try {
                tflite1 = new Interpreter(loadModelFile(MainActivity.this,MODEL_FILE1));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                tflite2 = new Interpreter(loadModelFile(MainActivity.this,MODEL_FILE2));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                tflite3 = new Interpreter(loadModelFile(MainActivity.this,MODEL_FILE3));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




    }

    private File getOutputMediaFile(){
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    public void dispatchTakePictureIntent() {
        final int REQUEST_TAKE_PHOTO = 1;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            file = FileProvider.getUriForFile(this, "com.example.android.fileprovider", getOutputMediaFile());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);

            startActivityForResult(takePictureIntent, 100);
            }

        }
    @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int[] pix = new int[picw * pich];
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), file);
                    ImageView im = (ImageView)findViewById(R.id.imview);
                    result = (TextView)findViewById(R.id.result);
                    im.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                bm = Bitmap.createScaledBitmap(bitmap, 256, 256, false);
                bm.getPixels(pix, 0, picw, 0, 0, picw, pich);
             int R = 0, G = 0, B = 0;

                int[][] Red = new int[256][256];
                int[][] Green = new int[256][256];
                int[][] Blue = new int[256][256];

                float [][][][] image = new float[1][256][256][3];
                for (int y = 0; y < pich; y++) {
                    for (int x = 0; x < picw; x++) {
                        int index = y * picw + x;
                        R = (pix[index] >> 16) & 0xff;     //bitwise shifting
                        Red[y][x] = R;
                        G = (pix[index] >> 8) & 0xff;
                        Green[y][x] = G;
                        B = pix[index] & 0xff;
                        Blue[y][x] = B;
                    }
                }
                //For Red
                for(int x = 0; x < 256; x++)
                {
                    for(int y = 0; x < 256; x++)
                    {
                        image[0][x][y][2] = Red[x][y];
                    }
                }
                //For Green
                for(int x = 0; x < 256; x++)
                {
                    for(int y = 0; x < 256; x++)
                    {
                        image[0][x][y][1] = Green[x][y];
                    }
                }
                //For Blue
                for(int x = 0; x < 256; x++)
                {
                    for(int y = 0; x < 256; x++)
                    {
                        image[0][x][y][0] = Blue[x][y];
                    }
                }
                for(int x = 0; x < 256; x++)
                {
                    for(int y = 0; x < 256; x++)
                    {
                        for(int z = 0; z < 3; z++)
                        {
                            image[0][x][y][z] = (image[0][x][y][z]/255);
                        }

                    }
                }
                float[][][][] inp = image;
                float[][][][] out = new float[1][32][32][64];

                tflite1.run(inp,out);
                float[][][][] cl5_inp = new float[1][64][64][64];
                for(int i = 0; i < 32; i++)
                {
                    for(int j = 0; j < 32; j ++)
                    {
                        for(int k = 0; k < 64; k++)
                        {
                            cl5_inp[0][2*i][2*j][k] = out[0][i][j][k];
                            cl5_inp[0][2*i + 1][2*j + 1][k] = out[0][i][j][k];
                            cl5_inp[0][2*i][2*j + 1][k] = out[0][i][j][k];
                            cl5_inp[0][2*i + 1][2*j][k] = out[0][i][j][k];
                        }
                    }
                }
                float[][][][] cl5_op = new float[1][32][32][32];
                tflite2.run(cl5_inp, cl5_op);
                float[][][][] temp1 = new float[1][64][64][32];
                float[][][][] temp2 = new float[1][128][128][32];
                float[][][][] cl6_inp = new float[1][256][256][32];
                float[][][][] cl6_op = new float[1][256][256][3];



                for(int i = 0; i < 32; i++)
                {
                    for(int j = 0; j < 32; j ++)
                    {
                        for(int k = 0; k < 32; k++)
                        {
                            temp1[0][2*i][2*j][k] = cl5_op[0][i][j][k];
                            temp1[0][2*i + 1][2*j + 1][k] = cl5_op[0][i][j][k];
                            temp1[0][2*i][2*j + 1][k] = cl5_op[0][i][j][k];
                            temp1[0][2*i + 1][2*j][k] = cl5_op[0][i][j][k];
                        }
                    }
                }
                for(int i = 0; i < 64; i++)
                {
                    for(int j = 0; j < 64; j ++)
                    {
                        for(int k = 0; k < 32; k++)
                        {
                            temp2[0][2*i][2*j][k] = temp1[0][i][j][k];
                            temp2[0][2*i + 1][2*j + 1][k] = temp1[0][i][j][k];
                            temp2[0][2*i][2*j + 1][k] = temp1[0][i][j][k];
                            temp2[0][2*i + 1][2*j][k] = temp1[0][i][j][k];
                        }
                    }
                }
                for(int i = 0; i < 128; i++)
                {
                    for(int j = 0; j < 128; j ++)
                    {
                        for(int k = 0; k < 32; k++)
                        {
                            cl6_inp[0][2*i][2*j][k] = temp2[0][i][j][k];
                            cl6_inp[0][2*i + 1][2*j + 1][k] = temp2[0][i][j][k];
                            cl6_inp[0][2*i][2*j + 1][k] = temp2[0][i][j][k];
                            cl6_inp[0][2*i + 1][2*j][k] = temp2[0][i][j][k];
                        }
                    }
                }

                tflite3.run(cl6_inp, cl6_op);
                //tf.square(tf.subtract(op,inp))

                for(int i = 0; i < 256; i++)
                    {
                        for(int j = 0; j < 256; j++)
                            {
                                for(int k = 0; k < 3; k++)
                                    {
                                        cl6_op[0][i][j][k] = ((cl6_op[0][i][j][k] - inp[0][i][j][k])*(cl6_op[0][i][j][k] - inp[0][i][j][k]));
                                    }
                            }
                    }

                //tf.reduce_sum
                //Add columns in 1st column. Delete all columns except 1st
                for(int i = 0; i < 256; i++)
                    {
                        for(int j = 1; j < 256; j++)
                            {
                                for(int k = 0; k < 3; k++)
                                    {
                                        cl6_op[0][i][0][k] += cl6_op[0][i][j][k];
                                    }
                            }
                    }

                float[][][] l2 = new float[1][256][3];
                for(int i = 0; i < 256; i++) {
                    for (int k = 0; k < 3; k++) {
                        l2[0][i][k] = (float)Math.sqrt(cl6_op[0][i][0][k]);

                    }
                }
                float[] l2_n = new float[3];
                for(int i = 0; i < 256; i++)
                {

                        l2[0][0][0] += l2[0][i][0];

                }
                l2_n[0] = (l2[0][0][0]/256);
                for(int i = 0; i < 256; i++)
                {

                    l2[0][0][1] += l2[0][i][1];

                }
                l2_n[1] = (l2[0][0][1]/256);
                for(int i = 0; i < 256; i++)
                {

                    l2[0][0][2] += l2[0][i][2];

                }
                l2_n[2] = (l2[0][0][2]/255);
                float loss = ((l2_n[0] + l2_n[1] + l2_n[2])/3);
                if(loss < 0.123)
                {
                    result.setBackgroundColor(0xff7db700);
                    result.setText("It is a Samosa");
                }
                else
                {
                    result.setBackgroundColor(0xfff00000);
                    result.setText("I don't think it is a Samosa...");
                }

            }

        }
    }
    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE1) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE1);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


}
