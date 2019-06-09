package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.myfirstapp.Networking.AppNetworking;
import com.example.myfirstapp.tools.UploadUtil;

import java.io.File;
import java.io.FileNotFoundException;

public class DisplayMessageActivity extends AppCompatActivity {

    private static final int SELECT_PHOTO = 0;
    String result = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }

    private String img_src;

    /**
     * 从相册选取图片
     */
    public void selectImg(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO:
                switch (resultCode) {
                    case RESULT_OK:
                        Uri uri = data.getData();
                        img_src = uri.getPath();//这是本机的图片路径

                        ContentResolver cr = getContentResolver();
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                            /* 将Bitmap设定到ImageView */
                            ImageView imageView = findViewById(R.id.imageView);
                            imageView.setImageBitmap(bitmap);

                            String[] proj = {MediaStore.Images.Media.DATA};
                            CursorLoader loader = new CursorLoader(this, uri, proj, null, null, null);
                            Cursor cursor = loader.loadInBackground();
                            if (cursor != null) {
                                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                cursor.moveToFirst();

                                img_src = cursor.getString(column_index);//图片实际路径
                                uploadImage(img_src);
                            }
                            cursor.close();

                        } catch (FileNotFoundException e) {
                            Log.e("Exception", e.getMessage(), e);
                        }

                        break;
                }
                break;

        }
    }

    public void uploadImage(String path) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String uploadurl = AppNetworking.baseUrl+ "upload/avatar?user=ruanqiaohua";
                try {
                    File file = new File(img_src);
                    AppNetworking.getInstance().uploadImage(file, uploadurl);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
