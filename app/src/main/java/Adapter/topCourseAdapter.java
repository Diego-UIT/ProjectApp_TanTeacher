package Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.example.tutorial_v1.R;

import Activity.CourseDetail;
import Model.courseItem;

public class topCourseAdapter extends RecyclerView.Adapter<topCourseAdapter.CustomViewHolder>{

    private ArrayList<courseItem> items;
    private Context context;
    public topCourseAdapter(ArrayList<courseItem> items, Context context) {
        this.items = items;
        this.context = context;
    }
    @NonNull
    @Override
    public topCourseAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new topCourseAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.top_courses_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull topCourseAdapter.CustomViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        NumberFormat formatter = new DecimalFormat("#,###");
        double price=(double)items.get(position).getPrice();
        String formattedNumber1 = formatter.format(price);
        if(formattedNumber1.equals("0")) holder.fee.setText("Miễn phí");
        else
            holder.fee.setText(formattedNumber1+" đ");

        Picasso.get().load(items.get(position).getUrl()).placeholder(R.drawable.empty23).error(R.drawable.empty23).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView);
        holder.ratingBar.setRating( items.get(position).getRating());
        holder.totalVote.setText("("+(int)items.get(position).getTotalVote()+")");
        if(items.get(position).getDiscount()!=0)
        {

            holder.discount.setPaintFlags(holder.fee.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            price=price-(price*items.get(position).getDiscount())/100;
            String formattedNumber2 = formatter.format(price);
            holder.fee.setText(formattedNumber2+" đ");
            holder.discount.setText(formattedNumber1);
        }
        else holder.discount.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView title,fee,discount,totalVote;
        private ImageView imageView;
        private RatingBar ratingBar;
        public CustomViewHolder(View view) {
            super(view);
            title=view.findViewById(R.id.top_course_title);
            fee=view.findViewById(R.id.top_cousers_fee);
            imageView=view.findViewById(R.id.top_course_image);
            ratingBar=view.findViewById(R.id.top_course_rating);
            discount=view.findViewById(R.id.top_cousers_discount);
            totalVote=view.findViewById(R.id.totalVote);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent=new Intent(context, CourseDetail.class);
                    intent.putExtra("course",items.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }
}
