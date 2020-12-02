package Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.example.tutorial_v1.R;
import java.util.ArrayList;

import Model.RatingComment;

public class RatingCommentAdapter extends RecyclerView.Adapter<RatingCommentAdapter.CustomViewHolder> {

    private ArrayList<RatingComment> items;
    private Context context;

    public RatingCommentAdapter(ArrayList<RatingComment> items, Context context) {
        this.items = items;

        this.context = context;
    }

    @NonNull
    @Override
    public RatingCommentAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RatingCommentAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.course_cmt_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RatingCommentAdapter.CustomViewHolder holder, int position) {

        Picasso.get().load(items.get(position).getAvatar()).placeholder(R.drawable.empty23).error(R.drawable.empty23).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.userAvatar);

        holder.ratingBar.setRating(items.get(position).getNumstar());
        holder.username.setText(items.get(position).getUserName());
        holder.cmtContent.setText(items.get(position).getCommentContent());
    }



    @Override
    public int getItemCount() {
        return items.size();
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView cmtContent,username;
        RatingBar ratingBar;
        public CustomViewHolder(View view) {
            super(view);
            userAvatar=view.findViewById(R.id.courseCommentAvatar);
            cmtContent=view.findViewById(R.id.courseCommentContent);
            username=view.findViewById(R.id.courseCommentUserName);
            ratingBar=view.findViewById(R.id.userRating);

        }
    }
}
