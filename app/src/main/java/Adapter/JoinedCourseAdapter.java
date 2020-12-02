package Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.tutorial_v1.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.courseItem;

public class JoinedCourseAdapter extends RecyclerView.Adapter<JoinedCourseAdapter.CustomViewHolder> {


    private ArrayList<courseItem> items;
    private Context context;


    public JoinedCourseAdapter(ArrayList<courseItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public JoinedCourseAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JoinedCourseAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.joined_course_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull JoinedCourseAdapter.CustomViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());


        Picasso.get().load(items.get(position).getUrl()).placeholder(R.drawable.empty23).error(R.drawable.empty23).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView);
        holder.joinedAt.setText(items.get(position).getCreateAt());
        holder.progressBar.setProgress(items.get(position).getPercent());
        holder.percentText.setText(""+items.get(position).getPercent()+"%");



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
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView title, joinedAt,percentText;
        private ImageView imageView;
        ProgressBar progressBar;

        public CustomViewHolder(View view) {
            super(view);
            title=view.findViewById(R.id.joinedCourseName1);
            joinedAt=view.findViewById(R.id.joinedAt);
            imageView=view.findViewById(R.id.joinedCourseImg);
            progressBar=view.findViewById(R.id.joinedCourseProgressbar);
            percentText=view.findViewById(R.id.joinedCoursePercent);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
               //     Intent intent=new Intent(context, JoinedCourseLessons.class);
              //      intent.putExtra("course",items.get(getAdapterPosition()));
              //      ((Activity) context).startActivityForResult(intent,1903);
                }
            });
        }
    }

}
