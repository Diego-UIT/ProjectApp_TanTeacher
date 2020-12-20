package Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;
import com.example.tutorial_v1.R;

import org.json.JSONException;
import org.json.JSONObject;

import Model.UserAccount;
import Retrofit.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import retrofit2.Response;
import retrofit2.Retrofit;




public class LoginActivity extends AppCompatActivity {
    Button loginBtn;
    TextView regisTextView,forgotPassword;
    EditText tkEditText, mkEditText;
    CompositeDisposable compositeDisposable =new CompositeDisposable();
    IMyService iMyService;
    String TaiKhoan, MatKhau;
    AlertDialog alertDialog;
    UserAccount userAccount;
    SharedPreferences sharedPreferences;
    boolean checkLoginStatus = false;
    boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        checkLoginStatus = sharedPreferences.getBoolean("already_login", false);
        setContentView(R.layout.activity_login);
        if (checkLoginStatus){
            Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
            startActivity(intent);
        }
        setUIReference();

        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();

        regisTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckValidInput())
                    Login();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ConfirmEmailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Login() {
        loginBtn.setClickable(false);
        loginBtn.setEnabled(false);

        try {
            // alertDialog.show();
            iMyService.loginUser(TaiKhoan, MatKhau)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<String>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Response<String> stringResponse) {
                            if(stringResponse.isSuccessful()){


                                if(stringResponse.body().toString().contains("name"))
                                {
                                    String responseString=stringResponse.body().toString();
                                    try {
                                        JSONObject jo=new JSONObject(responseString);
                                        userAccount=new UserAccount(jo.getString("name"),"",
                                                jo.getString("phone"),
                                                jo.getString("image"),
                                                jo.getString("email"),
                                                stringResponse.headers().get("Auth-token"),
                                                jo.getString("gender"),
                                                jo.getString("description"),
                                                jo.getString("address"),
                                                MatKhau,
                                                jo.getString("_id")
                                        );

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("name",userAccount.getHoten());
                                        editor.putString("phone",userAccount.getSdt());
                                        editor.putString("image",userAccount.getAva());
                                        editor.putString("email",userAccount.getMail());
                                        editor.putString("token",userAccount.getToken());
                                        Log.v("token", userAccount.getToken());
                                        editor.putString("gender",userAccount.getGioitinh());
                                        editor.putString("description",userAccount.getMota());
                                        editor.putString("address",userAccount.getDiachia());
                                        editor.putString("password",MatKhau);
                                        editor.putString("id",userAccount.getID());
                                        editor.putBoolean("already_login", true);
                                        editor.commit();
                                        flag=true;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                                else{
                                    flag=false;
                                }}
                            else{
                                flag=false;
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
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            loginBtn.setClickable(true);
                            loginBtn.setEnabled(true);
                        }

                        @Override
                        public void onComplete() {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            alertDialog.dismiss();

                                        }
                                    }, 500);


                            if(flag==true) {
                                Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                                intent.putExtra("userAcc", userAccount);
                                intent.putExtra("change",0);
                                startActivity(intent);
                                //CustomIntent.customType(LoginActivity.this, "right-to-left");

                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                                loginBtn.setClickable(true);
                                loginBtn.setEnabled(true);
                            }
                        }
                    });
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void setUIReference() {
        loginBtn=findViewById(R.id.loginBtn);
        regisTextView=findViewById(R.id.regText);
        tkEditText=findViewById(R.id.TaiKhoanEditText);
        mkEditText=findViewById(R.id.MatKhauEditText);
        forgotPassword=findViewById(R.id.forgotPass);
    }
    private boolean CheckValidInput() {
        Boolean valid=true;
        TaiKhoan=tkEditText.getText().toString();
        MatKhau=mkEditText.getText().toString();
        if(TaiKhoan.isEmpty() ||TaiKhoan.length() < 6 || TaiKhoan.length() >40 )
        {
            tkEditText.setError("Must from 6 to 40 characters");
            valid = false;
        } else {
            tkEditText.setError(null);
        }
        if(MatKhau.isEmpty() || MatKhau.length() <8 ||MatKhau.length()>16 )
        {
            mkEditText.setError("Must from 8 to 16 characters");
            valid = false;
        } else {
            mkEditText.setError(null);
        }
        return valid;
    }
}