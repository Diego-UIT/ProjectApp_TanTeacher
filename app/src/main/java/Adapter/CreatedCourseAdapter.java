package Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorial_v1.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.courseItem;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Request.Builder;
import retrofit2.Response;
import retrofit2.Retrofit;
import Retrofit.*;

public class CreatedCourseAdapter extends RecyclerView.Adapter<CreatedCourseAdapter.CustomViewHolder> {


    private final ArrayList<courseItem> items;
    private final Context context;


    public CreatedCourseAdapter(ArrayList<courseItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CreatedCourseAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.created_course_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CreatedCourseAdapter.CustomViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());

        Picasso.get().load(items.get(position).getUrl()).placeholder(R.drawable.empty23).error(R.drawable.empty23).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView);
        holder.CreatedAt.setText(items.get(position).getCreateAt());

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(context)
                        .setTitle("Delete this course")
                        .setMessage("Are you sure to delete this course ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteCourse(items.get(position).getID());
                                items.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, items.size());

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }
        });


    }
    boolean flag = false;
    private void DeleteCourse(String id) {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(context).build();
        alertDialog.show();

        iMyService.deleteCourse(sharedPreferences.getString("token", ""), "http://149.28.24.98:9000/course/delete/" + id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {
                        if (stringResponse.isSuccessful()){
                            flag = true;
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
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toasty.success(context, "Success", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toasty.error(context, "Error", Toasty.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        final private TextView title, CreatedAt;
        final private ImageView imageView;
        final private Button deleteBtn;

        public CustomViewHolder(View view) {
            super(view);
            title=view.findViewById(R.id.createdCourseName1);
            CreatedAt=view.findViewById(R.id.createdAt);
            imageView=view.findViewById(R.id.createdCourseImg);
            deleteBtn = view.findViewById(R.id.delete_course_btn);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //     Intent intent=new Intent(context, CreatedCourseLessons.class);
                    //      intent.putExtra("course",items.get(getAdapterPosition()));
                    //      ((Activity) context).startActivityForResult(intent,1903);
                }
            });
        }
    }

}
