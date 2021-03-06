package com.example.nanu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nanu.R;
import com.example.nanu.model.Bimp;
import com.example.nanu.model.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishActivity extends BaseActivity {
    private GridView gw;
    private List<Map<String, Object>> datas;
    private GridViewAddImgesAdpter gridViewAddImgesAdpter;
    TextView send;
    private Dialog dialog;
    private final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;
    private final String IMAGE_DIR =
            PublishActivity.this.getExternalFilesDir(null)+ "/gridview/";
    /* 头像名称 */
    private final String PHOTO_FILE_NAME = "temp_photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        gw = (GridView) findViewById(R.id.noScrollgridview);
        datas = new ArrayList<>();
        gridViewAddImgesAdpter = new GridViewAddImgesAdpter(datas, this);
        gw.setAdapter(gridViewAddImgesAdpter);
        gw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                showdialog();
            }
        });

        send= (TextView) findViewById(R.id.activity_selectimg_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PublishActivity.this, "发表成功", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 选择图片对话框
     */
    public void showdialog() {
        View localView = LayoutInflater.from(this).inflate(R.layout.item_popupwindows, null);
        TextView tv_camera = (TextView) localView.findViewById(R.id.item_popupwindows_camera);
        TextView tv_gallery = (TextView) localView.findViewById(R.id.item_popupwindows_Photo);
        TextView tv_cancel = (TextView) localView.findViewById(R.id.item_popupwindows_cancel);
        dialog = new Dialog(this, R.style.custom_dialog);
        dialog.setContentView(localView);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        // 设置全屏(已过时)
//        WindowManager windowManager = getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//        lp.width = display.getWidth(); // 设置宽度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = displayMetrics.widthPixels;

        dialog.getWindow().setAttributes(lp);
        dialog.show();
        tv_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        tv_camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // 拍照
                camera();
            }
        });

        tv_gallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // 从系统相册选取照片
                gallery();
//                Intent intent = new Intent(Intent.ACTION_PICK, null);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(intent, CameraGallaryUtil.PHOTO_REQUEST_GALLERY);
            }
        });
    }

    /**
     * 拍照
     */
    public void camera() {
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {

            File dir = new File(IMAGE_DIR);
            if (!dir.exists()) {
                dir.mkdir();
            }
            tempFile = new File(dir,
                    System.currentTimeMillis() + "_" + PHOTO_FILE_NAME);
            //从文件中创建uri
            Uri uri = Uri.fromFile(tempFile);
            Intent intent = new Intent();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addCategory(intent.CATEGORY_DEFAULT);
            // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
            startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
        } else {
            Toast.makeText(this, "未找到存储卡，无法拍照！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断sdcard是否被挂载
     */
    public boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    /**
     * 从相册获取2
     */
    public void gallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }
    //        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, 100);//(Intent.createChooser(intent, "选择图像..."), PICK_IMAGE_REQUEST);


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO_REQUEST_GALLERY) {
                // 从相册返回的数据
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    String[] proj = {MediaStore.Images.Media.DATA};
                    //好像是android多媒体数据库的封装接口，具体的看Android文档
                    Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                    //按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    //将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    uploadImage(path);
                }

            } else if (requestCode == PHOTO_REQUEST_CAREMA) {
                if (resultCode != RESULT_CANCELED) {
                    // 从相机返回的数据
                    if (hasSdcard()) {
                        if (tempFile != null) {
                            uploadImage(tempFile.getPath());
                        } else {
                            Toast.makeText(this, "相机异常请稍后再试！", Toast.LENGTH_SHORT).show();
                        }

                        Log.i("images", "拿到照片path=" + tempFile.getPath());
                    } else {
                        Toast.makeText(this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0xAAAAAAAA) {
                photoPath(msg.obj.toString());
            }

        }
    };

    /**
     * 上传图片
     *
     * @param path
     */
    private void uploadImage(final String path) {
        new Thread() {
            @Override
            public void run() {
                if (new File(path).exists()) {
                    Log.d("images", "源文件存在" + path);
                } else {
                    Log.d("images", "源文件不存在" + path);
                }

                File dir = new File(IMAGE_DIR);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                final File file = new File(dir + "/temp_photo" + System.currentTimeMillis() + ".jpg");
                saveBitmapToFile(path,file.getAbsolutePath());
                if (file.exists()) {
                    Log.d("images", "压缩后的文件存在" + file.getAbsolutePath());
                } else {
                    Log.d("images", "压缩后的不存在" + file.getAbsolutePath());
                }
                Message message = new Message();
                message.what = 0xAAAAAAAA;
                message.obj = file.getAbsolutePath();
                handler.sendMessage(message);

            }
        }.start();

    }
    public static String saveBitmapToFile(String file, String newpath) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
//            file.createNewFile();
//
//
//            FileOutputStream outputStream = new FileOutputStream(file);
//
//            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);


            File aa = new File(newpath);

            FileOutputStream outputStream = new FileOutputStream(aa);

            //choose another format if PNG doesn't suit you

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);


            String filepath = aa.getAbsolutePath();
            Log.e("getAbsolutePath", aa.getAbsolutePath());

            return filepath;
        } catch (Exception e) {
            return null;
        }
    }
    public void photoPath(String path) {
        Map<String,Object> map=new HashMap<>();
        map.put("path",path);
        datas.add(map);
        Log.e("我是照片=",""+map);
        gridViewAddImgesAdpter.notifyDataSetChanged();
    }
//    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA };
//    private GridView noScrollgridview;
//    private GridAdapter adapter;
//    private TextView activity_selectimg_send;
//    private String localTempImgDir = "tempDir";
//    private File f;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_publish);
//        Init();
//    }
//
//
//        public void update() {
//            loading();
//        }
//
//        public int getCount() {
//            return (Bimp.bmp.size() + 1);
//        }
//
//        public Object getItem(int arg0) {
//
//            return null;
//        }
//
//        public long getItemId(int arg0) {
//
//            return 0;
//        }
//
//        public void setSelectedPosition(int position) {
//            selectedPosition = position;
//        }
//
//        public int getSelectedPosition() {
//            return selectedPosition;
//        }
//
//        /**
//         * ListView Item设置
//         */
//        public View getView(int position, View convertView, ViewGroup parent) {
//            final int coord = position;
//            ViewHolder holder = null;
//            if (convertView == null) {
//
//                convertView = inflater.inflate(R.layout.item_published_grida,
//                        parent, false);
//                holder = new ViewHolder();
//                holder.image = (ImageView) convertView
//                        .findViewById(R.id.item_grida_image);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            if (position == Bimp.bmp.size()) {
//                holder.image.setImageBitmap(BitmapFactory.decodeResource(
//                        getResources(), R.drawable.icon_addpic_unfocused));
//                if (position == 9) {
//                    holder.image.setVisibility(View.GONE);
//                }
//            } else {
//                holder.image.setImageBitmap(Bimp.bmp.get(position));
//            }
//
//            return convertView;
//        }
//
//        public class ViewHolder {
//            public ImageView image;
//        }
//
//        Handler handler = new Handler() {
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case 1:
//                        adapter.notifyDataSetChanged();
//                        break;
//                }
//                super.handleMessage(msg);
//            }
//        };
//
//        public void loading() {
//            new Thread(new Runnable() {
//                public void run() {
//                    while (true) {
//                        if (Bimp.max == Bimp.drr.size()) {
//                            Message message = new Message();
//                            message.what = 1;
//                            handler.sendMessage(message);
//                            break;
//                        } else {
//                            try {
//                                String path = Bimp.drr.get(Bimp.max);
//                                System.out.println(path);
//                                Bitmap bm = Bimp.revitionImageSize(path);
//                                Bimp.bmp.add(bm);
//                                String newStr = path.substring(
//                                        path.lastIndexOf("/") + 1,
//                                        path.lastIndexOf("."));
//                                FileUtils.saveBitmap(bm, "" + newStr);
//                                Bimp.max += 1;
//                                Message message = new Message();
//                                message.what = 1;
//                                handler.sendMessage(message);
//                            } catch (IOException e) {
//
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//            }).start();
//        }
//    }
//
//    public String getString(String s) {
//        String path = null;
//        if (s == null)
//            return "";
//        for (int i = s.length() - 1; i > 0; i++) {
//            s.charAt(i);
//        }
//        return path;
//    }
//
//    protected void onRestart() {
//        adapter.update();
//        super.onRestart();
//    }
//
//    public class PopupWindows extends PopupWindow {
//
//        public PopupWindows(Context mContext, View parent) {
//
//            View view = View
//                    .inflate(mContext, R.layout.item_popupwindows, null);
//            view.startAnimation(AnimationUtils.loadAnimation(mContext,
//                    R.anim.fade_ins));
//            LinearLayout ll_popup = (LinearLayout) view
//                    .findViewById(R.id.ll_popup);
//            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
//                    R.anim.push_bottom_in_2));
//
//            setWidth(ViewGroup.LayoutParams.FILL_PARENT);
//            setHeight(ViewGroup.LayoutParams.FILL_PARENT);
//            setBackgroundDrawable(new BitmapDrawable());
//            setFocusable(true);
//            setOutsideTouchable(true);
//            setContentView(view);
//            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
//            update();
//
//            Button bt1 = (Button) view
//                    .findViewById(R.id.item_popupwindows_camera);
//            Button bt2 = (Button) view
//                    .findViewById(R.id.item_popupwindows_Photo);
//            Button bt3 = (Button) view
//                    .findViewById(R.id.item_popupwindows_cancel);
//            bt1.setOnClickListener(new View.OnClickListener() {//拍照
//                public void onClick(View v) {
//                    photo();
//                    dismiss();
//                }
//            });
//            bt2.setOnClickListener(new View.OnClickListener() {//相册
//                public void onClick(View v) {
////                    Intent intent = new Intent(PublishActivity.this,
////                            TestPicActivity.class);
////                    startActivity(intent);
//                    selectImage();
//                    dismiss();
//                }
//            });
//            bt3.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    dismiss();
//                }
//            });
//
//        }
//    }
//
//    private static final int TAKE_PICTURE = 0x000000;
//    private String path = "";
//
//    private void selectImage() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, 100);//(Intent.createChooser(intent, "选择图像..."), PICK_IMAGE_REQUEST);
//    }
//    public void photo() {//拍照
////        if (verifyPermissions(PublishActivity.this, PERMISSIONS_STORAGE[2]) == 0) {
////            ActivityCompat.requestPermissions(PublishActivity.this, PERMISSIONS_STORAGE, 3);
////        }else{
////            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  //跳转到 ACTION_IMAGE_CAPTURE
////            //判断内存卡是否可用，可用的话就进行存储
////            //putExtra：取值，Uri.fromFile：传一个拍照所得到的文件，fileImg.jpg：文件名
////
////            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(PublishActivity.this.getExternalFilesDir(null), "zy.jpg")));
////            startActivityForResult(intent,101); // 101: 相机的返回码参数
////        }
//		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		File file = new File(PublishActivity.this.getExternalFilesDir(null)
//				+ "/myimage/", String.valueOf(System.currentTimeMillis())
//				+ ".jpg");
//		path = file.getPath();
//		Uri imageUri = Uri.fromFile(file);
//
//        }
//    }
}

