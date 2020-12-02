package Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tutorial_v1.R;
import com.google.android.material.textfield.TextInputEditText;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import Retrofit.RetrofitClient;
import Retrofit.IMyService;

import static android.nfc.NfcAdapter.EXTRA_DATA;

public class PayActivity extends AppCompatActivity {
    CardInputWidget cardInputWidget;
    TextInputEditText emailEditText, nameEditText;
    Button payBtn;

    SharedPreferences sharedPreferences;
    JSONArray cartArray=new JSONArray();
    JSONObject sendJO=new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        cardInputWidget=findViewById(R.id.cardInput);
        emailEditText=findViewById(R.id.payEmail);
        nameEditText=findViewById(R.id.payName);
        payBtn=findViewById(R.id.payBtn);
        emailEditText.setVisibility(View.GONE);
        nameEditText.setVisibility(View.GONE);
        cardInputWidget.setPostalCodeEnabled(false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
                //load cart from share preferences
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray cart=new JSONArray();
        for(int i=0;i<cartArray.length();i++)
        {
           //put object into cart
        }
        try {
            sendJO.put("cart",cart);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            sendJO.put("idUser",sharedPreferences.getString("id",null));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card card=cardInputWidget.getCard();
                Stripe stripe=new Stripe(PayActivity.this,"pk_test_y8urHXEikr7ysm3tk7uRilcp00aTSdh57w");

                stripe.createCardToken(
                        card,
                        new ApiResultCallback<Token>() {
                            public void onSuccess(@NonNull Token token) {
                                JSONObject tokenJo=new JSONObject();
                                JSONObject cardJo=new JSONObject();
                                try {
                                    //21 record
                                    //cardJo.put("id",token.getCard().getId());
                                   // cardJo.put("object","card");

                                   // cardJo.put("name","caohoangtu1357@gmail.com");
                                   // cardJo.put("tokenization_method",token.getCard().getTokenizationMethod());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    //put information for token object

                                 //   tokenJo.put("email","caohoangtu1357@gmail.com");


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    sendJO.put("token",tokenJo);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                emailEditText.setVisibility(View.GONE);
                                nameEditText.setVisibility(View.GONE);
                                Pay();
                            }
                            public void onError(@NonNull Exception error) {


                            }
                        }
                );
            }
        });



    }

    String resultTemp="";
    boolean flag=false;
    private void Pay() {
        IMyService iMyService;
        AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), sendJO.toString());
        iMyService.pay(body).
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
                        resultTemp=response;

                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(PayActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


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
                            final Intent data = new Intent();


                            data.putExtra(EXTRA_DATA, "success");
                            data.putExtra("isPaid",true);

                            setResult(Activity.RESULT_OK, data);
                            finish();
                            Toasty.success(PayActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();


                        }
                        else
                            Toast.makeText(PayActivity.this, "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}