package com.cameraapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView mPreviewIv;
    private RadioGroup mTypeRg;
    private String mType;
    private boolean mIsSessionAlive;
    private String mFolderToSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreviewIv = (ImageView) findViewById(R.id.iv);
        mTypeRg = (RadioGroup) findViewById(R.id.rg);
        findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType == null) {
                    Toast.makeText(MainActivity.this, "Please select type", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                }
            }
        });

        mTypeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.singlePage) {
                    if(mIsSessionAlive){
                        getUserAccentToQuitMultiPageCapture();
                    }
                    mType = getString(R.string.single_Page);
                } else if (i == R.id.multipage) {
                    mIsSessionAlive = true;
                    mType = getString(R.string.multi_page);
                }
            }
        });
    }


    private void saveImage(Bitmap finalBitmap) {
        String rootFolderPath = getExternalFilesDir(null).getAbsolutePath();
        File f = null;

        if (mType.equals(getString(R.string.single_Page))) {
            mFolderToSave = new java.util.Date().toString();
            f = new File(rootFolderPath + "/" + mFolderToSave);
            if (!f.exists()) f.mkdirs();
        } else if (mType.equals(getString(R.string.multi_page))) {
            if (mFolderToSave == null && mIsSessionAlive) {
                mFolderToSave = new java.util.Date().toString();
            }
            f = new File(rootFolderPath + "/"+mFolderToSave);
            if (!f.exists()) f.mkdirs();
        }

        Log.d("file path", f.getAbsolutePath());

        File file = new File(f, new Random().nextInt() + ".jpeg");
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) intent.getExtras().get("data");
            mPreviewIv.setImageBitmap(photo);
            getUserAccentForSavingPicture(photo);
        }
    }

    private void getUserAccentForSavingPicture(final Bitmap photo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to save the image");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveImage(photo);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mPreviewIv.setImageDrawable(null);
            }
        });
        builder.show();
    }



    private void getUserAccentToQuitMultiPageCapture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to quit multi page capture ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mIsSessionAlive=false;
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //nothing to be done because already session is alive
            }
        });
        builder.show();
    }
}
