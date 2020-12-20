package Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorial_v1.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import Adapter.topCourseAdapter;
import Fragment.RatingCommentAdapter;
import Model.RatingComment;
import Model.courseItem;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import Retrofit.*;

import static android.view.View.GONE;

public class CourseDetail extends AppCompatActivity {
    ImageView courseImage;
    TextView courseName,courseGoal,courseRank,courseAuthor
            ,courseUpdateTime,coursePrice,courseOldPrice,courseJoinBtn
            ,videoCount,fileCount, quizCount,courseDescription
            ,totalVote;
    RatingBar ratingBar;
    Button addToCart, writeComment;
    Model.courseItem courseItem;
    IMyService iMyService;
    AlertDialog alertDialog;
    RecyclerView recyclerView1;
    ArrayList<courseItem> courseItems = new ArrayList<>();
    Adapter.topCourseAdapter topCourseAdapter;
    boolean flag=false;
    SharedPreferences sharedPreferences;
    JSONArray cartArray=new JSONArray();
    boolean checkCart=false;
    String cmtContent="";
    float numstar;
    RecyclerView recyclerView;
    RatingCommentAdapter ratingCommentAdapter;
    ArrayList<RatingComment> item=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        setUIReference();
        courseItem= (courseItem) getIntent().getSerializableExtra("course");
        setUp();
        recyclerView=findViewById(R.id.courseComment);
        ratingCommentAdapter=new RatingCommentAdapter(item,this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(ratingCommentAdapter);
        topCourseAdapter = new topCourseAdapter(courseItems, this);

        topCourseAdapter.setHasStableIds(true);
        recyclerView1 = findViewById(R.id.recommendCourses);
        recyclerView1.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        recyclerView1.setAdapter(topCourseAdapter);

        getRecommendCourses();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(courseItem.getPrice()>0) CheckIsBought();
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        if(courseItem.getPrice()!=0) courseJoinBtn.setVisibility(GONE);
        courseJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinCourse();
            }
        });

        try {
            cartArray= new JSONArray(sharedPreferences.getString("cartArray", "[]"));
            for (int i = 0; i < cartArray.length(); i++) {

                checkCart=true;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringFromJSONArray=cartArray.toString();
                if(stringFromJSONArray.contains(courseItem.getID()))
                    Toasty.warning(CourseDetail.this, "Already in the cart !", Toast.LENGTH_SHORT).show();
                else {
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put("courseImage", courseItem.getUrl());
                        jo.put("author", courseItem.getAuthor());
                        jo.put("courseID", courseItem.getID());
                        jo.put("title", courseItem.getTitle());
                        jo.put("price", courseItem.getPrice());
                        jo.put("discount", courseItem.getDiscount());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cartArray.put(jo);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("cartArray", cartArray.toString());
                    editor.apply();
                    Toasty.success(CourseDetail.this, "Add to cart", Toast.LENGTH_SHORT).show();
                }
                addToCart.setEnabled(false);

            }

        });
        writeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder  mbuilder = new AlertDialog.Builder(CourseDetail.this);
                View view=getLayoutInflater().inflate(R.layout.create_rate_dialog,null);
                RatingBar dialogRatingBar=view.findViewById(R.id.courseRatingBar);
                EditText cmtContentEditText=view.findViewById(R.id.courseComment);
                Button sendBtn=view.findViewById(R.id.sendRating);
                mbuilder.setView(view);
                AlertDialog alertDialog=mbuilder.create();
                sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        numstar=dialogRatingBar.getRating();
                        cmtContent=cmtContentEditText.getText().toString();
                        PostRating();
                        item.clear();
                        ratingCommentAdapter.notifyDataSetChanged();
                        alertDialog.dismiss();
                        GetRating();
                    }
                });
                alertDialog.show();
            }
        });
        GetRating();
    }
    private void GetRating(){
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();


        iMyService.getListComment("http://149.28.24.98:9000/rate/get-rate-by-course/"+courseItem.getID()).
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
                        try {
                            JSONArray jsonArray=new JSONArray(temp);
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jo1=jsonArray.getJSONObject(i);
                                JSONObject jo2=jo1.getJSONObject("idUser");
                                String name=jo2.getString("name");
                                String url=jo2.getString("image");
                                String content=jo1.getString("content");
                                double rating=jo1.getDouble("numStar");
                                RatingComment ratingComment=new RatingComment(name,content,(float)rating,"http://149.28.24.98:9000/upload/user_image/"+url);
                                item.add(ratingComment);
                                result=result+(float)rating;
                                ratingCommentAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        // Toasty.error(CourseDetail.this, "Bạn chưa hoàn thành 80% khóa học", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onComplete() {


                        if(flag==true)
                        {
                            result=result/item.size();
                            // Toasty.success(CourseDetail.this, "Gửi thành công", Toast.LENGTH_SHORT).show();


                        }

                        // Toast.makeText(CourseDetail.this, "Lỗi", Toast.LENGTH_SHORT).show();

                    }
                });



    }
    float result=0;
    String temp="";
    private void PostRating() {

        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CourseDetail.this);
        JSONObject jo=new JSONObject();
        try {
            jo.put("idUser",sharedPreferences.getString("id",null));
            jo.put("idCourse",courseItem.getID());
            jo.put("content",cmtContent);
            jo.put("numStar",numstar);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jo.toString());
        iMyService.postRating(body).
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
                        Toasty.error(CourseDetail.this, "You haven't completed 80% of this course", Toast.LENGTH_SHORT).show();


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


                            Toasty.success(CourseDetail.this, "Success", Toast.LENGTH_SHORT).show();


                        }
                        else
                            Toast.makeText(CourseDetail.this, "Error", Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void joinCourse() {

        alertDialog.show();
        iMyService.joinCourse(sharedPreferences.getString("id",null),courseItem.getID()).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(Response<String> response) {

                        if(response.isSuccessful()){
                            Toast.makeText(CourseDetail.this, response.body(), Toast.LENGTH_SHORT).show();


                            //JSONObject jsonObject=new JSONObject(temp);

                            flag=true;}



                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(CourseDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(CourseDetail.this, "Joined success", Toast.LENGTH_SHORT).show();
                            flag=false;

                        }
                        else
                            Toast.makeText(CourseDetail.this, "You already joined this course", Toast.LENGTH_SHORT).show();

                    }
                });


    }

    boolean flagBought=false;
    boolean checkBought=false;
    private void CheckIsBought() {

        IMyService iMyService;
        AlertDialog alertDialog;


        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        iMyService.getListComment("http://149.28.24.98:9000/course/check-is-bough-this-course/"+courseItem.getID()+"/"+sharedPreferences.getString("id",null)).
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
                            JSONObject jsonObject=new JSONObject(response);
                            checkBought=jsonObject.getBoolean("bought");
                            flagBought=true;

                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        Toast.makeText(CourseDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();
                                    }
                                }, 500);

                        if(flagBought==true)
                        {
                            if(checkBought==true) addToCart.setVisibility(GONE);
                        }
                        else{}
                        //Toast.makeText(CourseDetail.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    boolean flag3=false;
    private void getRecommendCourses() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        iMyService.getListComment("http://149.28.24.98:9000/course/recommend-course/"+courseItem.getID()).
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
                                        "",
                                        jo.getString("category"),
                                        jo.getString("ranking"),
                                        jo.getString("created_at")));
                                topCourseAdapter.notifyDataSetChanged();
                                // if(i==7) Toast.makeText(getContext(), jo.getString("image"), Toast.LENGTH_LONG).show();
                            }
                            flag3=true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CourseDetail.this, e.toString(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(CourseDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        else{}
                        //Toast.makeText(CourseDetail.this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUp() {
        Picasso.get().load(courseItem.getUrl()).placeholder(R.drawable.image1).error(R.drawable.image1).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(courseImage);
        courseName.setText(courseItem.getTitle());
        courseGoal.setText(courseItem.getGoal());
        courseRank.setText("Raking "+courseItem.getRanking());
        courseAuthor.setText(courseItem.getAuthor());
        courseUpdateTime.setText("Update time: "+courseItem.getUpdateTime());

        NumberFormat formatter = new DecimalFormat("#,###");
        double price=(double)courseItem.getPrice();
        double discount=(double)courseItem.getDiscount();
        if(price==0) {
            coursePrice.setText("Free");
            courseJoinBtn.setText("Join Now");
            courseOldPrice.setVisibility(GONE);
            addToCart.setVisibility(GONE);
        }else if(price!=0&&discount==0)
        {
            String formattedNumber1 = formatter.format(price);
            coursePrice.setText(formattedNumber1);
            courseOldPrice.setVisibility(GONE);
        }else if(price!=0&&discount!=0)
        {
            String oldPrice = formatter.format(price);
            price=price-(price*discount)/100;
            String newprice = formatter.format(price);
            coursePrice.setText(newprice+ "đ");
            courseOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            courseOldPrice.setText(oldPrice+"");
        }
        courseDescription.setText(courseItem.getDesription());
        totalVote.setText("("+(int)courseItem.getTotalVote()+")");
        ratingBar.setRating(courseItem.getRating());

    }
    private void setUIReference() {
        courseImage=findViewById(R.id.courseDetailImage);
        courseName=findViewById(R.id.courseDetailName);
        courseGoal=findViewById(R.id.courseDetailGoal);
        courseRank=findViewById(R.id.coursRank);
        courseAuthor=findViewById(R.id.courseAuthor);
        courseUpdateTime=findViewById(R.id.updateTime);
        coursePrice=findViewById(R.id.ccoursePrice);
        courseOldPrice=findViewById(R.id.courseOldPrice);
        courseJoinBtn=findViewById(R.id.joinCourse);
        courseDescription=findViewById(R.id.authorsDescription);
        totalVote=findViewById(R.id.totalVoteRating);
        addToCart=findViewById(R.id.addToCart);
        writeComment=findViewById(R.id.writeComment);
        ratingBar=findViewById(R.id.courseDetailRating);
    }
}