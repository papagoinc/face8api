package com.papagoinc.face8api;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.papagoinc.face8api.Utils.MyOkHttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //FaceSet API
    static final String FACE8_API_CREATE = "https://face8.pakka.ai/api/v2/faceset/create";
    static final String FACE8_API_ADDPHOTO = "https://face8.pakka.ai/api/v2/faceset/addFace";
    static final String FACE8_API_REMOVEFACE = "https://face8.pakka.ai/api/v2/faceset/removeFace";
    static final String FACE8_API_DELETE = "https://face8.pakka.ai/api/v2/faceset/delete";
    static final String FACE8_API_GETDETAIL = "https://face8.pakka.ai/api/v2/faceset/getDetail";

    //Facial API
    static final String FACE8_API_FACEDETECT = " https://face8.pakka.ai/api/v2/faceDetect";
    static final String FACE8_API_FACECOMPARE = " https://face8.pakka.ai/api/v2/faceCompare";
    static final String FACE8_API_FACESEARCH = " https://face8.pakka.ai/api/v2/search";

    //apply api key firstly @https://www.face8.ai/api-doc/
    private static String api_key = "f265c9365cb24cf8b6348674c34f18ed";
    private static String api_faceset_token = "74d82724356442689ee3042c2f164dce";
    private static String api_face_token = "6acb9e88f6994ab5b501e244ace115a4";

    private int imgPickerID1 = R.drawable.leon01;
    private int imgPickerID2 = R.drawable.leon01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //create faceset to get faceset token
    public void createFaceSet(View view) {
        Toast.makeText( this , "createFaceSet", Toast.LENGTH_LONG ).show();

        final String uri_face8api = FACE8_API_CREATE;
        final String key_1 = "api_key";
        final String key_2 = "faceset_name";

        MultipartBody.Builder multiPartBody = new MultipartBody.Builder();
        multiPartBody.setType(MultipartBody.FORM);
        multiPartBody.addFormDataPart(key_1, api_key);
        multiPartBody.addFormDataPart(key_2, "testSet");

        MyOkHttpUtils.postAsyn(uri_face8api, multiPartBody.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "createFaceSet=" + "failed" + e.toString());
                if (e.toString().contains("No address associated with hostname"))
                    showAlert("網路未連接！");
                else
                    showAlert("FaceSet創建失敗！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                Log.i(TAG, "createFaceSet=" + "success" + str);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jObject = new JSONObject(str);
                            String strToken = jObject.getString("faceset_token");
                            Log.i(TAG, "faceset_token = " + strToken);
                            showAlert("FaceSet創建成功！faceset_token="+strToken);
                        } catch (JSONException e) {
                            // Oops
                        }
                    }
                });
            }
        });
    }

    //detect face to get facetoken
    public void faceDetect(View view) {
        Toast.makeText( this , "faceDetect", Toast.LENGTH_LONG ).show();

        final String uri_face8api = FACE8_API_FACEDETECT;
        final String key_1 = "api_key";
        final String key_2 = "image_file";

        MultipartBody.Builder multiPartBody = new MultipartBody.Builder();
        multiPartBody.setType(MultipartBody.FORM);
        multiPartBody.addFormDataPart(key_1, api_key);

        File file = drawableToFile(this, R.drawable.leon01, "faceDetect.png");
        if (file.exists() && !file.isDirectory()) {
            multiPartBody.addFormDataPart(key_2, file.getName(),
                    RequestBody.create(MediaType.parse("image/png"), file));
        }

        MyOkHttpUtils.postAsyn(uri_face8api, multiPartBody.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "faceDetect=" + "failed" + e.toString());
                if (e.toString().contains("No address associated with hostname"))
                    showAlert("網路未連接！");
                else
                    showAlert("人臉偵測失敗！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                Log.i(TAG, "faceDetect=" + "success" + str);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jObject = new JSONObject(str);
                            String strToken = jObject.getJSONArray("faces").
                                    getJSONObject(0).
                                    getString("face_token");
                            Log.i(TAG, "face_token = " + strToken);
                            showAlert("人臉偵測成功！face_token="+strToken);
                        } catch (JSONException e) {
                            // Oops
                        }
                    }
                });
            }
        });
    }

    //prepare faceset token and face token, then addFace
    public void addFace(View view) {
        Toast.makeText( this , "addFace", Toast.LENGTH_LONG ).show();

        final String uri_face8api = FACE8_API_ADDPHOTO;
        final String key_1 = "api_key";
        final String key_2 = "faceset_token";
        final String key_3 = "face_tokens";

        MultipartBody.Builder multiPartBody = new MultipartBody.Builder();
        multiPartBody.setType(MultipartBody.FORM);
        multiPartBody.addFormDataPart(key_1, api_key);
        multiPartBody.addFormDataPart(key_2, api_faceset_token);
        multiPartBody.addFormDataPart(key_3, api_face_token);

        MyOkHttpUtils.postAsyn(uri_face8api, multiPartBody.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "addFace=" + "failed" + e.toString());
                if (e.toString().contains("No address associated with hostname"))
                    showAlert("網路未連接！");
                else
                    showAlert("人臉加入失敗！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                Log.i(TAG, "addFace=" + "success" + str);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jObject = new JSONObject(str);
                            int faceCount = jObject.getInt("face_count");
                            Log.i(TAG, "face_count = " + faceCount);
                            showAlert("人臉加入成功！face_count="+faceCount);
                        } catch (JSONException e) {
                            // Oops
                        }
                    }
                });
            }
        });
    }

    //compare 2 img files
    public void faceCompare() {
        Toast.makeText( this , "faceCompare", Toast.LENGTH_LONG ).show();

        final String uri_face8api = FACE8_API_FACECOMPARE;
        final String key_1 = "api_key";
        final String key_2 = "image_file1";
        final String key_3 = "image_file2";

        MultipartBody.Builder multiPartBody = new MultipartBody.Builder();
        multiPartBody.setType(MultipartBody.FORM);
        multiPartBody.addFormDataPart(key_1, api_key);

        File file1 = drawableToFile(this, imgPickerID1, "faceCmp1.png");
        if (file1.exists() && !file1.isDirectory()) {
            multiPartBody.addFormDataPart(key_2, file1.getName(),
                    RequestBody.create(MediaType.parse("image/png"), file1));
        }

        File file2 = drawableToFile(this, imgPickerID2, "faceCmp2.png");
        if (file2.exists() && !file2.isDirectory()) {
            multiPartBody.addFormDataPart(key_3, file2.getName(),
                    RequestBody.create(MediaType.parse("image/png"), file2));
        }

        MyOkHttpUtils.postAsyn(uri_face8api, multiPartBody.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "faceCompare=" + "failed" + e.toString());
                if (e.toString().contains("No address associated with hostname"))
                    showAlert("網路未連接！");
                else
                    showAlert("人臉比對失敗！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                Log.i(TAG, "faceCompare=" + "success" + str);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Double confidence;
                        Double threshold;
                        try {
                            JSONObject jObject = new JSONObject(str);
                            if (jObject.has("confidence")) {
                                threshold = jObject.getJSONObject("thresholds")
                                        .getDouble("1e-5");
                                confidence = jObject.getDouble("confidence");
                                Log.i(TAG, "confidence=" + confidence);
                                if (confidence > threshold)
                                    showAlert("人臉比對完成！同一人(confidence="+confidence+")");
                                else
                                    showAlert("人臉比對完成！不同一人(confidence="+confidence+")");
                            }

                        } catch (JSONException e) {
                            // Oops
                        }
                    }
                });
            }
        });
    }

    public void faceSearch() {
        Toast.makeText( this , "faceSearch", Toast.LENGTH_LONG ).show();

        final String uri_face8api = FACE8_API_FACESEARCH;
        final String key_1 = "api_key";
        final String key_2 = "faceset_token";
        final String key_3 = "image_file";

        MultipartBody.Builder multiPartBody = new MultipartBody.Builder();
        multiPartBody.setType(MultipartBody.FORM);
        multiPartBody.addFormDataPart(key_1, api_key);
        multiPartBody.addFormDataPart(key_2, api_faceset_token);

        File file = drawableToFile(this, imgPickerID1, "faceSearch.png");
        if (file.exists() && !file.isDirectory()) {
            multiPartBody.addFormDataPart(key_3, file.getName(),
                    RequestBody.create(MediaType.parse("image/png"), file));
        }

        MyOkHttpUtils.postAsyn(uri_face8api, multiPartBody.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "faceSearch=" + "failed" + e.toString());
                if (e.toString().contains("No address associated with hostname"))
                    showAlert("網路未連接！");
                else
                    showAlert("人臉搜尋失敗！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                Log.i(TAG, "faceSearch=" + "success" + str);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Double confidence, threshold;
                        String face_token;
                        try {
                            JSONObject jObject = new JSONObject(str);
                            if (jObject.has("message")) {
                                threshold = jObject.getJSONObject("thresholds")
                                        .getDouble("1e-5");
                                confidence = jObject.getJSONArray("results").
                                        getJSONObject(0).getDouble("confidence");
                                if (jObject.getString("message").equals("OK")) {
                                    if (confidence > threshold)
                                        showAlert("人臉搜尋完成！找到(confidence="+confidence+")");
                                    else
                                        showAlert("人臉搜尋完成！沒找到(confidence="+confidence+")");
                                }
                            }
                            if (jObject.has("faces")) {
                                Log.i(TAG, "face_token=" + jObject.getJSONArray("faces")
                                        .getJSONObject(0).getString("face_token"));
                            }

                        } catch (JSONException e) {
                            // Oops
                        }
                    }
                });
            }
        });
    }

    public void removeFace(View view) {
        Toast.makeText( this , "removeFace", Toast.LENGTH_LONG ).show();

        final String uri_face8api = FACE8_API_REMOVEFACE;
        final String key_1 = "api_key";
        final String key_2 = "faceset_token";
        final String key_3 = "face_tokens";

        MultipartBody.Builder multiPartBody = new MultipartBody.Builder();
        multiPartBody.setType(MultipartBody.FORM);
        multiPartBody.addFormDataPart(key_1, api_key);
        multiPartBody.addFormDataPart(key_2, api_faceset_token);
        multiPartBody.addFormDataPart(key_3, api_face_token);

        MyOkHttpUtils.postAsyn(uri_face8api, multiPartBody.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "removeFace=" + "failed" + e.toString());
                if (e.toString().contains("No address associated with hostname"))
                    showAlert("網路未連接！");
                else
                    showAlert("人臉移除失敗！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                Log.i(TAG, "removeFace=" + "success" + str);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jObject = new JSONObject(str);
                            int faceCount = jObject.getInt("face_count");
                            Log.i(TAG, "face_count = " + faceCount);
                            showAlert("人臉移除成功！face_count="+faceCount);
                        } catch (JSONException e) {
                            // Oops
                        }
                    }
                });
            }
        });
    }

    public void deleteFaceSet(View view) {
        Toast.makeText( this , "deleteFaceSet", Toast.LENGTH_LONG ).show();

        final String uri_face8api = FACE8_API_DELETE;
        final String key_1 = "api_key";
        final String key_2 = "faceset_token";

        MultipartBody.Builder multiPartBody = new MultipartBody.Builder();
        multiPartBody.setType(MultipartBody.FORM);
        multiPartBody.addFormDataPart(key_1, api_key);
        multiPartBody.addFormDataPart(key_2, api_faceset_token);

        MyOkHttpUtils.postAsyn(uri_face8api, multiPartBody.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "deleteFaceSet=" + "failed" + e.toString());
                if (e.toString().contains("No address associated with hostname"))
                    showAlert("網路未連接！");
                else
                    showAlert("FaceSet刪除失敗！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                Log.i(TAG, "deleteFaceSet=" + "success" + str);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jObject = new JSONObject(str);
                            String strToken = jObject.getString("faceset_token");
                            Log.d(TAG, "faceset_token=" + strToken);
                            showAlert("FaceSet刪除成功！faceset_token="+strToken);
                        } catch (JSONException e) {
                            // Oops
                        }
                    }
                });
            }
        });
    }

    public void getDetail(View view) {
        Toast.makeText( this , "getDetail", Toast.LENGTH_LONG ).show();

        final String uri_face8api = FACE8_API_GETDETAIL;
        final String key_1 = "api_key";
        final String key_2 = "faceset_token";

        MultipartBody.Builder multiPartBody = new MultipartBody.Builder();
        multiPartBody.setType(MultipartBody.FORM);
        multiPartBody.addFormDataPart(key_1, api_key);
        multiPartBody.addFormDataPart(key_2, api_faceset_token);

        MyOkHttpUtils.postAsyn(uri_face8api, multiPartBody.build(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "getDetail=" + "failed" + e.toString());
                if (e.toString().contains("No address associated with hostname"))
                    showAlert("網路未連接！");
                else
                    showAlert("獲取FaceSet資訊失敗！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String str = response.body().string();
                Log.i(TAG, "getDetail=" + "success" + str);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jObject = new JSONObject(str);
                            String strToken = jObject.getString("faceset_token");
                            int faceCount = jObject.getInt("face_count");
                            Log.d(TAG, "faceset_token=" + strToken +
                                    ", face_count=" + faceCount);
                            showAlert("獲取FaceSet資訊成功！face_count="+faceCount);
                        } catch (JSONException e) {
                            // Oops
                        }
                    }
                });
            }
        });
    }

    /**
     * drawable轉為file
     * @param mContext
     * @param drawableId  drawable的ID
     * @param fileName   轉換後的文件名
     * @return File
     */
    public File drawableToFile(Context mContext, int drawableId, String fileName){
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawableId);
        String defaultPath = mContext.getFilesDir().getAbsolutePath() + "/face8api";
        File file = new File(defaultPath);
        if (!file.exists()) {
            file.mkdirs();
        } else {
        }
        String defaultImgPath = defaultPath + "/"+fileName;
        file = new File(defaultImgPath);
        try {
            file.createNewFile();

            FileOutputStream fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 20, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     *利用Handler，將顯示Alert的工作，放在主(UI)線程中來做
     * @param msg 要顯示的訊息
     */
    public void showAlert(final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(msg)
                        .setTitle("訊息")
                        .setNeutralButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    //for facecompare
    int iCompare = 0;
    public void imgPickerCompare(View view) {
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View textEntryView = factory.inflate(R.layout.img_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("請點選要比對的圖片")
                .setView(textEntryView)
                .show();
        Window win = alertDialog.getWindow();
        ImageButton btn1 = (ImageButton)win.findViewById(R.id.imageBtn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iCompare==0)
                    imgPickerID1 = R.drawable.leon01;
                else
                    imgPickerID2 = R.drawable.leon01;
                ++iCompare;
                view.setBackgroundColor(Color.RED);
                if (iCompare==2) {
                    iCompare = 0;
                    alertDialog.dismiss();
                    faceCompare();
                }
            }
        });
        ImageButton btn2 = (ImageButton)win.findViewById(R.id.imageBtn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iCompare==0)
                    imgPickerID1 = R.drawable.leon02;
                else
                    imgPickerID2 = R.drawable.leon02;
                ++iCompare;
                view.setBackgroundColor(Color.RED);
                if (iCompare==2) {
                    iCompare = 0;
                    alertDialog.dismiss();
                    faceCompare();
                }
            }
        });
        ImageButton btn3 = (ImageButton)win.findViewById(R.id.imageBtn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iCompare==0)
                    imgPickerID1 = R.drawable.mark01;
                else
                    imgPickerID2 = R.drawable.mark01;
                ++iCompare;
                view.setBackgroundColor(Color.RED);
                if (iCompare==2) {
                    iCompare = 0;
                    alertDialog.dismiss();
                    faceCompare();
                }
            }
        });
        ImageButton btn4 = (ImageButton)win.findViewById(R.id.imageBtn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iCompare==0)
                    imgPickerID1 = R.drawable.grace01;
                else
                    imgPickerID2 = R.drawable.grace01;
                ++iCompare;
                view.setBackgroundColor(Color.RED);
                if (iCompare==2) {
                    iCompare = 0;
                    alertDialog.dismiss();
                    faceCompare();
                }
            }
        });
    }

    //for facesearch
    public void imgPickerSearch(View view) {
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        final View textEntryView = factory.inflate(R.layout.img_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("請點選要搜尋的圖片")
                .setView(textEntryView)
                .show();
        Window win = alertDialog.getWindow();
        ImageButton btn1 = (ImageButton)win.findViewById(R.id.imageBtn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPickerID1 = R.drawable.leon01;
                alertDialog.dismiss();
                faceSearch();
            }
        });
        ImageButton btn2 = (ImageButton)win.findViewById(R.id.imageBtn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPickerID1 = R.drawable.leon02;
                alertDialog.dismiss();
                faceSearch();
            }
        });
        ImageButton btn3 = (ImageButton)win.findViewById(R.id.imageBtn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPickerID1 = R.drawable.mark01;
                alertDialog.dismiss();
                faceSearch();
            }
        });
        ImageButton btn4 = (ImageButton)win.findViewById(R.id.imageBtn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPickerID1 = R.drawable.grace01;
                alertDialog.dismiss();
                faceSearch();
            }
        });
    }
}
