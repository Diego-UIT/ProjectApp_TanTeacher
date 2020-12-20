package Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.tutorial_v1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapter.categoryRVAdapter;
import Adapter.topCourseAdapter;
import Model.category_item;
import Model.courseItem;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import Retrofit.*;
/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class FeatureFragment extends Fragment {

    ImageSlider imageSlider;

    RecyclerView recyclerView1,recyclerView2,recyclerView3;
    ArrayList<courseItem> courseItems = new ArrayList<>();
    ArrayList<courseItem> courseItems2 = new ArrayList<>();
    ArrayList<courseItem> courseItems3 = new ArrayList<>();

    Adapter.topCourseAdapter topCourseAdapter,topCourseAdapter2,topCourseAdapter3;
    ArrayList<category_item> items = new ArrayList<>();
    categoryRVAdapter adapter;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;

    public FeatureFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView= inflater.inflate(R.layout.fragment_feature, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        imageSlider= rootView.findViewById(R.id.image_slider);
        List<SlideModel> imageList=new ArrayList<>();

        imageList.add(new SlideModel("https://images.unsplash.com/photo-1510915228340-29c85a43dcfe?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80", "“The authority of those who teach is often an obstacle to those who want to learn.”-Marcus Tullius Cicero", true));
        imageList.add(new SlideModel("https://learnworthy.net/wp-content/uploads/2019/12/Why-programming-is-the-skill-you-have-to-learn.jpg","“The ink of the scholar is more holy than the blood of the martyr”― Anonymous, Qurʾan"));
        imageList.add(new SlideModel("https://images.unsplash.com/photo-1510915361894-db8b60106cb1?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80", "“The more that you read, the more things you will know. The more that you learn, the more places you’ll go.” – Dr.  Seus"));

        imageSlider.setImageList(imageList,true);
        //load category
        adapter = new categoryRVAdapter(items,getActivity());
        recyclerView = rootView.findViewById(R.id.category_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        LoadAllCategory();
        //newcourse
        topCourseAdapter = new topCourseAdapter(courseItems,getActivity());
        topCourseAdapter.setHasStableIds(true);
        recyclerView1 = rootView.findViewById(R.id.top_course1_recyclerView);
        recyclerView1.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        recyclerView1.setAdapter(topCourseAdapter);
        //freecourse
        topCourseAdapter2 = new topCourseAdapter(courseItems2,getActivity());
        topCourseAdapter2.setHasStableIds(true);
        recyclerView2 = rootView.findViewById(R.id.top_course1_recyclerView1);
        recyclerView2.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        recyclerView2.setAdapter(topCourseAdapter2);
        //top course
        topCourseAdapter3 = new topCourseAdapter(courseItems3,getActivity());
        topCourseAdapter3.setHasStableIds(true);
        recyclerView3 = rootView.findViewById(R.id.top_course1_recyclerView2);
        recyclerView3.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        recyclerView3.setAdapter(topCourseAdapter3);
        LoadNewCourses();
        LoadFreeCourses();
        LoadTopCourse();
        return  rootView;
    }

    private void LoadFreeCourses() {

        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.getFreeCourse().
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

                            //JSONObject jsonObject=new JSONObject(temp);

                            JSONArray ja=new JSONArray(response);
                            // JSONArray jsonArray=jsonObject.getJSONArray("");
                            for(int i=0;i<ja.length();i++)
                            {
                                JSONObject jo=ja.getJSONObject(i);
                                courseItems2.add(new courseItem( "http://149.28.24.98:9000/upload/course_image/"+jo.getString("image"),
                                        jo.getString("name"),"0",jo.getJSONObject("idUser").getString("name"),
                                        Float.valueOf(jo.getJSONObject("vote").getString("EVGVote")),
                                        Float.valueOf(jo.getString("price")),
                                        Float.valueOf(jo.getString("discount")),
                                        Float.valueOf(jo.getJSONObject("vote").getString("totalVote")),jo.getString("goal"),jo.getString("description"),jo.getString("_id"),
                                        jo.getJSONObject("category").getString("name"),
                                        jo.getJSONObject("category").getString("_id"),
                                        jo.getString("ranking"),
                                        jo.getString("created_at")));
                                topCourseAdapter2.notifyDataSetChanged();

                                // if(i==7) Toast.makeText(getContext(), jo.getString("image"), Toast.LENGTH_LONG).show();


                            }
                            flag2=true;
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

                        if(flag2==true)
                        {


                        }
                        else
                            Toast.makeText(getContext(), "Load free courses Error", Toast.LENGTH_SHORT).show();

                    }
                });
    }
    boolean flag=false;
    boolean flag2=false;
    boolean flag3=false;
    private void LoadNewCourses() {


        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.getAllCourse().
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

                            //JSONObject jsonObject=new JSONObject(temp);

                            JSONArray ja=new JSONArray(response);
                            // JSONArray jsonArray=jsonObject.getJSONArray("");
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
                                topCourseAdapter.notifyDataSetChanged();

                                // if(i==7) Toast.makeText(getContext(), jo.getString("image"), Toast.LENGTH_LONG).show();


                            }
                            flag=true;
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

                        if(flag==true)
                        {


                        }
                        else
                            Toast.makeText(getContext(), "Load new courses Error", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void LoadTopCourse() {

        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.getTopCourse().
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

                            //JSONObject jsonObject=new JSONObject(temp);

                            JSONArray ja=new JSONArray(response);
                            // JSONArray jsonArray=jsonObject.getJSONArray("");
                            for(int i=0;i<ja.length();i++)
                            {
                                JSONObject jo=ja.getJSONObject(i);

                                courseItems3.add(new courseItem( "http://149.28.24.98:9000/upload/course_image/"+jo.getString("image"),
                                        jo.getString("name"),"0",jo.getJSONObject("idUser").getString("name"),
                                        Float.valueOf(jo.getJSONObject("vote").getString("EVGVote")),
                                        Float.valueOf(jo.getString("price")),
                                        Float.valueOf(jo.getString("discount")),
                                        Float.valueOf(jo.getJSONObject("vote").getString("totalVote")),jo.getString("goal"),jo.getString("description"),jo.getString("_id"),
                                        jo.getJSONObject("category").getString("name"),
                                        jo.getJSONObject("category").getString("_id"),
                                        jo.getString("ranking"),
                                        jo.getString("created_at")));
                                topCourseAdapter3.notifyDataSetChanged();

                                // if(i==7) Toast.makeText(getContext(), jo.getString("image"), Toast.LENGTH_LONG).show();


                            }
                            flag3=true;
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

                        if(flag3==true)
                        {


                        }
                        else
                            Toast.makeText(getContext(), "Load to courses Error", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    boolean flag_category=false;
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

                            String temp=response;

                            //JSONObject jsonObject=new JSONObject(temp);
                            //Toast.makeText(getContext(), "here", Toast.LENGTH_SHORT).show();
                            JSONArray ja=new JSONArray(response);
                            // JSONArray jsonArray=jsonObject.getJSONArray("");
                            for(int i=0;i<ja.length();i++)
                            {
                                JSONObject jo=ja.getJSONObject(i);
                                String tempName=jo.getString("name");
                                String tempID=jo.getString("_id");
                                String img=jo.getString("image");
                                items.add(new category_item(tempName,tempID,"http://149.28.24.98:9000/upload/category/"+img));
                                adapter.notifyDataSetChanged();

                                flag_category=true;

                            }
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
                            Toast.makeText(getContext(), "Load categories Error", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}