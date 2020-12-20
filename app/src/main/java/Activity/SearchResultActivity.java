package Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorial_v1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Adapter.courseAdapter;
import Model.courseItem;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.*;

public class SearchResultActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView nameTextView;
    RecyclerView recyclerView;
    ArrayList<courseItem> courseItems = new ArrayList<>();
    String searchString="";
    String showString = "";
    Adapter.courseAdapter courseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        setUIReference();
        searchString=getIntent().getStringExtra("keyWord");
      //  showString = getIntent().getStringExtra("name");
        ActionToolbar();
        if(showString.length() == 0)
            nameTextView.setText("Result for \""+searchString+"\"");
        else
            nameTextView.setText("Result for \""+showString+"\"");
        courseAdapter = new courseAdapter(courseItems, SearchResultActivity.this);
        courseAdapter.setHasStableIds(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(courseAdapter);
        LoadCourses();
    }

    boolean flag=false;
    String temp="";

    private void LoadCourses() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.setTitle("Searching...");
        alertDialog.show();
        iMyService.getCourseByCategory("http://149.28.24.98:9000/course/search-course/" +searchString).
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

                            temp=response;

                            //JSONObject jsonObject=new JSONObject(temp);

                            JSONArray ja=new JSONArray(temp);
                            // JSONArray jsonArray=jsonObject.getJSONArray("");
                            for(int i=0;i<ja.length();i++)
                            {
                                JSONObject jo=ja.getJSONObject(i);
                                courseItems.add(new courseItem( "http://149.28.24.98:9000/upload/course_image/"+jo.getString("image"),
                                        jo.getString("name"),"0",jo.getString("idUser"),
                                        Float.valueOf(jo.getJSONObject("vote").getString("EVGVote")),
                                        Float.valueOf(jo.getString("price")),
                                        Float.valueOf(jo.getString("discount")),
                                        Float.valueOf(jo.getJSONObject("vote").getString("totalVote")),jo.getString("goal"),jo.getString("description"),jo.getString("_id"),
                                        "",
                                        jo.getString("category"),
                                        jo.getString("ranking"),
                                        jo.getString("created_at")));
                                courseAdapter.notifyDataSetChanged();

                                // if(i==7) Toast.makeText(getContext(), jo.getString("image"), Toast.LENGTH_LONG).show();


                            }
                            flag=true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SearchResultActivity.this, e.toString(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(SearchResultActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(SearchResultActivity.this, "", Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void setUIReference() {
        toolbar=findViewById(R.id.searchResultTB);
        nameTextView=findViewById(R.id.searchKeyWord);
        recyclerView=findViewById(R.id.resultCourse);
    }

    private void ActionToolbar() {
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