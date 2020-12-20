package Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.content.Context;
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

import Adapter.JoinedCourseAdapter;
import Model.courseItem;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.*;

public class mycoursesFragment extends Fragment {

    ArrayList<courseItem> courseItems = new ArrayList<>();
    Adapter.JoinedCourseAdapter courseAdapter;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    TextView tv;
    public mycoursesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview =inflater.inflate(R.layout.fragment_mycourses, container, false);
        recyclerView=rootview.findViewById(R.id.my_joined_course);
        // tv=rootview.findViewById(R.id.sss);
        courseAdapter=new JoinedCourseAdapter(courseItems,getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        courseAdapter.setHasStableIds(true);
        // Toast.makeText(getContext(), sharedPreferences.getString("id",null), Toast.LENGTH_LONG).show();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(courseAdapter);
        getJoinedCoure();

        return  rootview;
    }
    boolean flag=false;
    String temp="";
    private void getJoinedCoure() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.getJoinedCourse("http://149.28.24.98:9000/join/get-courses-joined-by-user/"+sharedPreferences.getString("id","")).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
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
                                    JSONObject jo2=jo.getJSONObject("idCourse");
                                    courseItem ci=new courseItem();
                                    ci.setID(jo2.getString("_id"));
                                    ci.setTitle(jo2.getString("name"));
                                    ci.setUrl("http://149.28.24.98:9000/upload/course_image/"+jo2.getString("image"));
                                    ci.setPercent(jo.getInt("percentCompleted"));

                                    ci.setCreateAt(jo.getString("created_at"));
                                    courseItems.add(ci);
                                    courseAdapter.notifyDataSetChanged();

                                    // if(i==7) Toast.makeText(getContext(), jo.getString("image"), Toast.LENGTH_LONG).show();


                                }
                                flag=true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                /*Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();*/
                            }

                        }
                        else
                            Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();

                    }
                });
        //tv.setText(temp);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getContext(), "asdasdasdasdasd", Toast.LENGTH_SHORT).show();
        if(requestCode == 1903) {

            if(resultCode == Activity.RESULT_OK) {
                courseItems.clear();
                courseAdapter.notifyDataSetChanged();
                getJoinedCoure();


            } else {

            }
        }
    }



}
