package Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import com.example.tutorial_v1.R;

import Model.courseItem;

public class CartCourseAdapter extends RecyclerView.Adapter<CartCourseAdapter.CustomViewHolder> {


    private ArrayList<courseItem> items;
    private Context context;


    public CartCourseAdapter(ArrayList<courseItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CartCourseAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartCourseAdapter.CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.cart_course_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartCourseAdapter.CustomViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.author.setText("Tác giả: "+items.get(position).getAuthor());
        Picasso.get().load(items.get(position).getUrl()).placeholder(R.drawable.empty23).error(R.drawable.empty23).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView);
        NumberFormat formatter = new DecimalFormat("#,###");
        double price=(double)items.get(position).getPrice();
        String formattedNumber1 = formatter.format(price);
        if(formattedNumber1.equals("0")) holder.price.setText("Miễn phí");
        else
            holder.price.setText(formattedNumber1+" đ");
        if(items.get(position).getDiscount()!=0)
        {

            holder.discount.setPaintFlags(holder.price.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            price=price-(price*items.get(position).getDiscount())/100;
            String formattedNumber2 = formatter.format(price);
            holder.price.setText(formattedNumber2+" đ");
            holder.discount.setText(formattedNumber1);
        }
        else holder.discount.setVisibility(View.GONE);
        holder.deleteFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Remove(position);
            }
        });


    }

    private void Remove( int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
        JSONArray cartArray=new JSONArray();
        for(int i=0;i<items.size();i++)
        {
            JSONObject jo=new JSONObject();
            try {
                jo.put("courseImage",items.get(i).getUrl());
                jo.put("author",items.get(i).getAuthor());
                jo.put("courseID",items.get(i).getID());
                jo.put("title",items.get(i).getTitle());
                jo.put("price",items.get(i).getPrice());
                jo.put("discount",items.get(i).getDiscount());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            cartArray.put(jo);
        }
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cartArray",cartArray.toString());
        editor.apply();

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

        private TextView title,author, price, discount;
        private ImageView imageView,deleteFromCart;

        public CustomViewHolder(View view) {
            super(view);
            title=view.findViewById(R.id.cartCourseName);
            author=view.findViewById(R.id.cartCourseAuthor);
            imageView=view.findViewById(R.id.cartCourseImage);
            price=view.findViewById(R.id.cartCoursePrice);
            discount=view.findViewById(R.id.cartCourseDiscount);
            deleteFromCart=view.findViewById(R.id.deleteFromCart);

        }
    }
}
