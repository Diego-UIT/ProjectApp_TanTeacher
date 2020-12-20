package Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tutorial_v1.R;

import org.json.JSONException;
import org.json.JSONObject;

import Model.UserAccount;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import Retrofit.RetrofitClient;

public class UserInfoActivity extends AppCompatActivity {

    Toolbar userInfoTB;
    Button updateBtn;
    EditText etHoTen, etSdt, etDiachi, etMota, etGioitinh;
    UserAccount userAccount = new UserAccount();
    String hoten, sdt, diachi, mota, gioitinh;
    IMyService iMyService;
    boolean flag = false;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userAccount = (UserAccount) getIntent().getSerializableExtra("userAcc");
        setUIReference();
        alertDialog = new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);
        etHoTen.setText(userAccount.getHoten());
        etSdt.setText(userAccount.getSdt());

        etDiachi.setText(userAccount.getDiachia());
        etMota.setText(userAccount.getMota());
        etGioitinh.setText(userAccount.getGioitinh());
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidInput()) updateProfile();
            }
        });
        ActionToolBar();
    }

    private boolean checkValidInput() {
        boolean valid = false;
        hoten = etHoTen.getText().toString();
        sdt = etSdt.getText().toString();
        diachi = etDiachi.getText().toString();
        mota = etMota.getText().toString();
        gioitinh = etGioitinh.getText().toString();
        if (hoten.isEmpty() || sdt.isEmpty() || diachi.isEmpty() || gioitinh.isEmpty() || mota.isEmpty()) {
            Toast.makeText(this, "Please full fill your information", Toast.LENGTH_SHORT).show();
            valid = false;
        } else valid = true;
        return valid;
    }

    private void ActionToolBar() {
        setSupportActionBar(userInfoTB);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        userInfoTB.setTitleTextColor(-1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userInfoTB.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setUIReference() {
        userInfoTB = findViewById(R.id.userInfoTB);
        etHoTen = findViewById(R.id.userinfoName);
        etSdt = findViewById(R.id.userinfoPhone);
        etDiachi = findViewById(R.id.userinfoAddress);
        etMota = findViewById(R.id.userinfoDescript);
        etGioitinh = findViewById(R.id.userinfoGender);
        updateBtn = findViewById(R.id.ProfileUpdate);
    }


    private void updateProfile() {
        updateBtn.setClickable(false);
        updateBtn.setEnabled(false);

        alertDialog.show();
        iMyService.changeProfile(hoten, sdt, diachi, mota, gioitinh, userAccount.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<String> response) {


                        if (response.isSuccessful()) {


                            if (response.body().toString().contains("success")) {
                                String responeString = response.body().toString();
                                try {
                                    JSONObject jo = new JSONObject(responeString);
                                    userAccount.setHoten(hoten);
                                    userAccount.setSdt(sdt);
                                    userAccount.setGioitinh(gioitinh);
                                    userAccount.setMota(mota);
                                    userAccount.setDiachia(diachi);
                                    flag = true;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                flag = false;
                            }
                        } else {
                            int a = response.code();
                            Toast.makeText(UserInfoActivity.this, "code: " + a, Toast.LENGTH_LONG).show();
                            flag = false;
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
                        Toast.makeText(UserInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toasty.success(UserInfoActivity.this, "Update info success", Toast.LENGTH_SHORT).show();
                            final Intent data = new Intent();

                            data.putExtra("usernewAcc", userAccount);

                            setResult(Activity.RESULT_OK, data);

                            finish();
                        } else
                            Toast.makeText(UserInfoActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                        updateBtn.setEnabled(true);
                        updateBtn.setClickable(true);
                    }
                });
    }
}