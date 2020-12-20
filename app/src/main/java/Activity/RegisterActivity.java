package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutorial_v1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Model.UserAccount;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import Retrofit.RetrofitClient;


public class RegisterActivity extends AppCompatActivity {

    EditText TaiKhoanText, NgaySinhText,SdtText, HoTenText, MatKhauText, XacNhanText,DiaChiText,GioiTinhText,MoTaText;
    TextView logText;
    Button RegButton;
    String myDateOfBirth;
    String[] temp;
    String name = "";
    String failText="";
    String username = "";
    String mobile = "";
    String password = "";
    String DateOfBirth ="";
    public static final String URL = "http://149.28.24.98:9000/register";
    String reEnterPassword = "";
    String DiaChi="",MoTa="",GioiTinh="",token="";
    UserAccount userAccount;
    private DatePickerDialog.OnDateSetListener birthdayListener;
    CompositeDisposable compositeDisposable =new CompositeDisposable();
    IMyService iMyService;
    Boolean flag=false;
    AlertDialog alertDialog;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setUIReference();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        alertDialog= new SpotsDialog.Builder().setContext(this).build();
        iMyService=retrofitClient.create(IMyService.class);

        logText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        RegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckValidInput()) {

                    SignUp();
                }

            }
        });

    }

    private void SignUp() {

        RegButton.setClickable(false);
        RegButton.setEnabled(false);

        alertDialog.show();
        iMyService.registerUser(name,username,password,mobile,DiaChi,MoTa,GioiTinh)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {

                        if (stringResponse.isSuccessful()) {

                            if (stringResponse.body().toString().contains("name")) {
                                String responseString = stringResponse.body().toString();

                                try {
                                    JSONObject jo = new JSONObject(responseString);
                                    userAccount = new UserAccount(name, "", mobile, "", username, stringResponse.headers().get("auth-token"), GioiTinh, MoTa, DiaChi, password, jo.getString("_id"));
                                    flag = true;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                                flag = false;
                            }
                        } else {
                            failText = "cc";
                            String responseString = null;
                            try {
                                responseString = stringResponse.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            try {
                                JSONObject parent = new JSONObject(responseString);
                                JSONArray jo = parent.getJSONArray("errors");
                                for (int i = 0; i < jo.length(); i++) {
                                    JSONObject jsonObject = jo.getJSONObject(i);
                                    failText = jsonObject.getString("msg");
                                }
                                flag = false;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                failText = e.toString();
                            }
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
                        Toast.makeText(RegisterActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                        flag = false;

                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            alertDialog.dismiss();

                                        }
                                    }, 500);


                            if (flag == true) {
                                Intent intent = new Intent(RegisterActivity.this, ActiveAccountActivity.class);
                                intent.putExtra("userAcc", userAccount);
                                startActivity(intent);

                            } else {
                                Toast.makeText(RegisterActivity.this, failText, Toast.LENGTH_SHORT).show();
                                RegButton.setClickable(true);
                                RegButton.setEnabled(true);
                        }

                    }
                });

    }
    private boolean CheckValidInput() {
        boolean valid = true;
        name = HoTenText.getText().toString();
        username = TaiKhoanText.getText().toString().trim();
        mobile = SdtText.getText().toString().trim();
        password = MatKhauText.getText().toString().trim();
        //DateOfBirth = NgaySinhText.getText().toString();
        reEnterPassword = XacNhanText.getText().toString().trim();
        DiaChi=DiaChiText.getText().toString();
        MoTa=MoTaText.getText().toString();
        GioiTinh=GioiTinhText.getText().toString();
        if(name.isEmpty()||name.length()<3 || name.length()>40)
        {
            HoTenText.setError("Name must from 3 to 40 characters");
            valid = false;
        } else {
            HoTenText.setError(null);
        }

        if(username.isEmpty() || username.length() < 3 || username.length() >40 )
        {
            TaiKhoanText.setError("Must from 6 to 40 characters");
            valid = false;
        } else {
            TaiKhoanText.setError(null);
        }



        if(mobile.isEmpty())
        {
            SdtText.setError("Invalid phone number");
            valid = false;
        } else {
            SdtText.setError(null);
        }

        if(password.isEmpty() || password.length() <8 || password.length()>16 )
        {
            MatKhauText.setError("Must from 8 to 16 characters");
            valid = false;
        } else {
            MatKhauText.setError(null);
        }

        if(reEnterPassword.isEmpty() || reEnterPassword.length() < 8 || reEnterPassword.length()>16 || !reEnterPassword.equals(password) )
        {
            XacNhanText.setError("Wrong confirm password");
            valid = false;
        } else{
            XacNhanText.setError(null);
        }

        return valid;


    }
    private void setUIReference() {

        TaiKhoanText=findViewById(R.id.taikhoanText);

        SdtText=findViewById(R.id.sdtText);
        HoTenText=findViewById(R.id.HoTenText);
        MatKhauText=findViewById(R.id.matkhauText);
        XacNhanText=findViewById(R.id.xacnhanText);
        logText=findViewById(R.id.logText);
        RegButton=findViewById(R.id.regisBtn);
        GioiTinhText=findViewById(R.id.gioitinhText);
        DiaChiText=findViewById(R.id.diachiText);
        MoTaText=findViewById(R.id.motaText);
    }

}