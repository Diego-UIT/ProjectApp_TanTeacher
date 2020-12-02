package Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tutorial_v1.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import Adapter.CartCourseAdapter;
import Model.courseItem;

import static android.app.Activity.RESULT_OK;

public class cartFragment extends Fragment {



    RecyclerView recyclerView;
    ArrayList<courseItem> courseItems = new ArrayList<>();
    CartCourseAdapter cartCourseAdapter;
    Button payBtn;

    public cartFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView= inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = rootView.findViewById(R.id.cartRecyclerView);
        payBtn=rootView.findViewById(R.id.cartFragmentPayBtn);
        cartCourseAdapter=new CartCourseAdapter(courseItems,getContext());
        recyclerView.setAdapter(cartCourseAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));

        LoadCourseInCart();
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intent=new Intent(getContext(), PayActivity.class);

              //  startActivityForResult(intent,1111);

            }
        });

        return  rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK&&requestCode==1111)
        {
            if(data.getBooleanExtra("isPaid",false)){
                recyclerView.setVisibility(View.GONE);
                SharedPreferences sharedPreferences;
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(("cartArray"));
                editor.commit();
            }

        }

    }

    private void LoadCourseInCart() {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        JSONArray cartArray;
        try {
            // Toast.makeText(getContext(), "here", Toast.LENGTH_SHORT).show();
            cartArray= new JSONArray(sharedPreferences.getString("cartArray", "[]"));
            if(cartArray.length()==0) {payBtn.setText("Giỏ hàng rỗng");payBtn.setEnabled(false);}
            for(int i=0;i<cartArray.length();i++){
                JSONObject jo=cartArray.getJSONObject(i);
                courseItems.add(new courseItem(jo.getString("courseImage"),jo.getString("title"),jo.getString("author"),jo.getString("courseID"),
                        Float.valueOf(jo.getString("price")),Float.valueOf(jo.getString("discount"))));
            }
            cartCourseAdapter.notifyDataSetChanged();




        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}
