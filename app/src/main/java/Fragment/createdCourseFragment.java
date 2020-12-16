package Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tutorial_v1.R;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Adapter.CreatedCourseAdapter;
import Model.courseItem;
import Activity.CreateCourse;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.*;


public class createdCourseFragment extends Fragment {

    ArrayList<courseItem> courseItems = new ArrayList<>();
    Adapter.CreatedCourseAdapter courseAdapter;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    Button createCourseButton;

    public createdCourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_created_course, container, false);
        recyclerView = rootView.findViewById(R.id.created_course_recyclerView);
        courseAdapter = new CreatedCourseAdapter(courseItems, getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        courseAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(courseAdapter);
        getCreatedCourse();
        LoadAllCategory();

        createCourseButton = rootView.findViewById(R.id.create_course_btn);
        createCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateCourse.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    boolean flag = true;
    String temp ="";
    private void getCreatedCourse() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();

        iMyService.getCreatedCourse("http://149.28.24.98:9000/course/getby-iduser/"+sharedPreferences.getString("id",""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {
                        flag=true;
                        temp=response;
                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);

                        if(flag==true)
                        {
                            //Toast.makeText(getContext(), "Cssss", Toast.LENGTH_SHORT).show();
                            try {


                                //JSONObject jsonObject=new JSONObject(temp);


                                JSONArray ja=new JSONArray(temp);
                                // JSONArray jsonArray=jsonObject.getJSONArray("");
                                for(int i=0;i<ja.length();i++)
                                {

                                    JSONObject jo=ja.getJSONObject(i);
                                    //JSONObject jo2=jo.getJSONObject("idUser");
                                    courseItem ci=new courseItem();
                                    ci.setID(jo.getString("_id"));
                                    ci.setTitle(jo.getString("name"));
                                    ci.setUrl("http://149.28.24.98:9000/upload/course_image/"+jo.getString("image"));

                                    ci.setCreateAt(jo.getString("created_at"));
                                    courseItems.add(ci);
                                    courseAdapter.notifyDataSetChanged();

                                    // if(i==7) Toast.makeText(getContext(), jo.getString("image"), Toast.LENGTH_LONG).show();


                                }
                                flag=true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }

                        }
                        else
                            Toast.makeText(getContext(), "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    boolean flag_category = false;
    private void LoadAllCategory() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.getAllCategory().
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {
                        try {

                            JSONArray ja=new JSONArray(response);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("category_array", ja.toString());
                            editor.apply();
                            Log.v("category", sharedPreferences.getString("category_array", ""));
                            flag_category = true;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();
                                    }
                                }, 500);

                        if(flag_category==true)
                        {

                        }
                        else
                            Toast.makeText(getContext(), "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getContext(), "asdasdasdasdasd", Toast.LENGTH_SHORT).show();
        if(requestCode == 1903) {


            if(resultCode == Activity.RESULT_OK) {
                courseItems.clear();
                courseAdapter.notifyDataSetChanged();
                getCreatedCourse();


            } else {

            }
        }
    }
}