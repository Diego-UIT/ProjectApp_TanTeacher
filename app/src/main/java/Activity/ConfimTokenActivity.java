package Activity;

import android.app.AlertDialog;
import android.content.Context;
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
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import retrofit2.Response;
import retrofit2.Retrofit;

public class ConfimTokenActivity extends AppCompatActivity {
    Button updatePassButton;
    TextView mailTextView;
    EditText tokenEditText, newPasswordEditText, confirmPasswordEditText;
    String token, newPassword, confirmPassword;
    IMyService iMyService;
    AlertDialog alertDialog;
    SharedPreferences sharedPreferences;
    String mail;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.activity_confirm_token);

        setUIReference();

        mail = sharedPreferences.getString("account_forgot_pass", "");
        mailTextView.setText(mailTextView.getText() + mail);

        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();

        updatePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckValidInput())
                    ResetPassword();
            }
        });
    }

    private void ResetPassword() {
        updatePassButton.setClickable(false);
        updatePassButton.setEnabled(false);

        try {
            iMyService.resetPassword(mail, token, newPassword)
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
                            else
                                flag = false;
                        }

                        @Override
                        public void onError(Throwable e) {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            alertDialog.dismiss();

                                        }
                                    }, 500);
                            Toast.makeText(ConfimTokenActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            updatePassButton.setClickable(true);
                            updatePassButton.setEnabled(true);
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
                                Intent intent = new Intent(ConfimTokenActivity.this, LoginActivity.class);
                                startActivity(intent);
                                Toasty.success(ConfimTokenActivity.this, "Reset password success", Toasty.LENGTH_LONG).show();
                            }
                            else {
                                Toasty.error(ConfimTokenActivity.this, "Error! Please check your token!", Toasty.LENGTH_LONG).show();
                                updatePassButton.setClickable(true);
                                updatePassButton.setEnabled(true);
                            }
                        }
                    });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private boolean CheckValidInput() {
        boolean valid = true;
        token = tokenEditText.getText().toString();
        newPassword = newPasswordEditText.getText().toString();
        confirmPassword = confirmPasswordEditText.getText().toString();

        if(newPassword.isEmpty() || newPassword.length() <8 || newPassword.length()>16 )
        {
            newPasswordEditText.setError("Mật khẩu có 8 đến 16 ký tự");
            valid = false;
        } else {
            newPasswordEditText.setError(null);
        }

        if(confirmPassword.isEmpty() || confirmPassword.length() < 8 || confirmPassword.length()>16 || !confirmPassword.equals(newPassword) )
        {
            confirmPasswordEditText.setError("Mật khẩu không khớp");
            valid = false;
        } else{
            confirmPasswordEditText.setError(null);
        }
        return valid;
    }

    private void setUIReference() {
        mailTextView = findViewById(R.id.accountForgotPass);
        updatePassButton = findViewById(R.id.updateNewPassBtn);
        tokenEditText = findViewById(R.id.resetPassTokenText);
        newPasswordEditText = findViewById(R.id.resetNewPasswordText);
        confirmPasswordEditText = findViewById(R.id.resetConfirmNewPasswordText);
    }
}

