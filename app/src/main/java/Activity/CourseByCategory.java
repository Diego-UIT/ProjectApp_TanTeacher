package Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorial_v1.R;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Adapter.courseAdapter;
import Model.category_item;
import Model.courseItem;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.*;

public class CourseByCategory extends AppCompatActivity {
    Toolbar toolbar;
    TextView nameTextView;
    RecyclerView recyclerView;
    ArrayList<courseItem> courseItems = new ArrayList<>();
    Adapter.courseAdapter courseAdapter;
    category_item categoryItem=new category_item("","");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_by_category);

        setUIReference();
        categoryItem.setID(getIntent().getStringExtra("ID"));
        categoryItem.setTextView(getIntent().getStringExtra("name"));
        nameTextView.setText(categoryItem.getname());
        ActionToolBar();
        courseAdapter = new courseAdapter(courseItems,CourseByCategory.this);
        courseAdapter.setHasStableIds(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(courseAdapter);
        LoadCourses();
    }
    boolean flag=false;
    private void LoadCourses() {

        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        iMyService.getCourseByCategory("http://149.28.24.98:9000/course/getby-category/"+categoryItem.getID()).
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

                            String temp=response;
                            JSONArray ja=new JSONArray(response);
                            for(int i=0;i<ja.length();i++)
                            {
                                JSONObject jo=ja.getJSONObject(i);
                                courseItems.add(new courseItem( "http://149.28.24.98:9000/upload/course_image/"+jo.getString("image"),
                                        jo.getString("name"),"0",jo.getJSONObject("idUser").getString("name"),
                                        Float.valueOf(jo.getJSONObject("vote").getString("EVGVote")),
                                        Float.valueOf(jo.getString("price")),
                                        Float.valueOf(jo.getString("discount")),
                                        Float.valueOf(jo.getJSONObject("vote").getString("totalVote")),jo.getString("goal"),jo.getString("description"),jo.getString("_id"),
                                        jo.getJSONObject("category").getString("name"),
                                        jo.getJSONObject("category").getString("_id"),
                                        jo.getString("ranking"),
                                        jo.getString("created_at")));
                                courseAdapter.notifyDataSetChanged();
                            }
                            flag=true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CourseByCategory.this, e.toString(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(CourseByCategory.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        }
                        else
                            Toast.makeText(CourseByCategory.this, "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUIReference() {
        toolbar=findViewById(R.id.courseByCategoryToolbar);
        nameTextView=findViewById(R.id.categoryName);
        recyclerView=findViewById(R.id.category_course);
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(-1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}