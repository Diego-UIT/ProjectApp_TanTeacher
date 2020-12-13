package Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tutorial_v1.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Model.category_item;
import Retrofit.IMyService;
import Retrofit.RetrofitClient;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateCourse extends AppCompatActivity {
    Toolbar createCourseTB;
    Spinner spinner;
    EditText courseName, courseTarget, courseDescription,
    coursePrice, courseDiscount;
    String token, name, target,categoryId, description, price, discount;
    File file;
    Bitmap bitmap;
    IMyService iMyService;
    boolean flag2 = false, flag = false;
    AlertDialog alertDialog;
    ImageView courseImage;
    TextView categoryName;
    Button galleryButton, submitButton;
    SharedPreferences sharedPreferences;
    JSONArray ja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        setUIReference();
        ActionToolBar();
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = sharedPreferences.getString("token", "");
        try {
            ja = new JSONArray(sharedPreferences.getString("category_array",""));
            categoryId = ja.getJSONObject(0).getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //TODO - 1st - Get String from sharedPreferences and put into JSONArray

        //TODO - Spinner for category
        {
            categoryName = findViewById(R.id.create_course_category);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.categories, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String text = parent.getItemAtPosition(position).toString();
                    //TODO - 2nd - Change into for loop to get category name and put into view
                    //TODO - 3rd - Get ID from category for loop and put into categoryId
                    if (text.equals("Mathematics - Informatics")) {
                        categoryName.setText(R.string.math_category);
                        try {
                            for(int i=0; i < ja.length(); i++) {
                                String tempName = ja.getJSONObject(i).getString("name");
                                String tempID = ja.getJSONObject(i).getString("_id");
                                if (text.equals(tempName)){
                                    categoryId = tempID;
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    if (text.equals("Information Technology")) {
                        categoryName.setText(R.string.it_category);
                        try {
                            for(int i=0; i < ja.length(); i++) {
                                String tempName = ja.getJSONObject(i).getString("name");
                                String tempID = ja.getJSONObject(i).getString("_id");
                                if (text.equals(tempName)){
                                    categoryId = tempID;
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    if (text.equals("Languages")) {
                        categoryName.setText(R.string.la_category);
                        try {
                            for(int i=0; i < ja.length(); i++) {
                                String tempName = ja.getJSONObject(i).getString("name");
                                String tempID = ja.getJSONObject(i).getString("_id");
                                if (text.equals(tempName)){
                                    categoryId = tempID;
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        //permission not granted, request it.
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, 1000);
                    }
                    else {
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else {
                    //system os is less then marshmallow
                    pickImageFromGallery();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setString();
                CreateNewCourse();
            }
        });
    }

    private void CreateNewCourse() {
        setButtonState(false);
        RequestBody fileReqBody =
                RequestBody.create(
                        MediaType.parse("image/jpg"),
                        file
                );
        //MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), fileReqBody);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("name", name)
                .addFormDataPart("goal", target)
                .addFormDataPart("description", description)
                .addFormDataPart("category", categoryId)
                .addFormDataPart("price", price)
                .addFormDataPart("discount", discount)
                .addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"),
                        file)).build();

        alertDialog.show();
        iMyService.createCourse(token, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe( Disposable d) {

                    }

                    @Override
                    public void onNext( Response<String> stringResponse) {
                        if (stringResponse.isSuccessful()){
                            if (stringResponse.body().contains("success")){
                                flag = true;
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toasty.error(CreateCourse.this, e.getMessage(), Toasty.LENGTH_LONG).show();
                        setButtonState(true);
                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        if (flag == true){
                            Toasty.success(CreateCourse.this, "Success", Toasty.LENGTH_LONG).show();
                            finish();
                        }else {
                            Toasty.error(CreateCourse.this, "Error", Toasty.LENGTH_LONG).show();
                            setButtonState(true);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1000 && data.getData() != null){
            //set image to image view

            Uri path=data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                courseImage.setImageBitmap(bitmap);
                file = new File(getRealPathFromURI(path));
                flag2=true;
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1000:{


                if (grantResults.length >0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission was granted
                    pickImageFromGallery();
                }
                else {
                    //permission was denied
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1000);
    }
    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void setString() {
        name = courseName.getText().toString();
        target = courseTarget.getText().toString();
        description = courseDescription.getText().toString();
        price = coursePrice.getText().toString();
        discount = courseDiscount.getText().toString();
    }
    private void setUIReference() {
        spinner = findViewById(R.id.category_spinner);
        createCourseTB = findViewById(R.id.createCourseToolBar);
        courseImage = findViewById(R.id.create_course_image);
        galleryButton = findViewById(R.id.create_course_library);
        courseName = findViewById(R.id.create_course_name_input);
        courseTarget = findViewById(R.id.create_course_target_input);
        courseDescription = findViewById(R.id.create_course_description_input);
        coursePrice = findViewById(R.id.create_course_price_input);
        courseDiscount = findViewById(R.id.create_course_discount_input);
        submitButton = findViewById(R.id.create_course_submit_btn);
    }

    private void setButtonState (boolean state) {
        submitButton.setClickable(state);
        submitButton.setEnabled(state);
        galleryButton.setClickable(state);
        galleryButton.setEnabled(state);
    }

    private void ActionToolBar() {
        setSupportActionBar(createCourseTB);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        createCourseTB.setTitleTextColor(-1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        createCourseTB.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}