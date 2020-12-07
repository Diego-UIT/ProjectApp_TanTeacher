package Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tutorial_v1.R;

import Retrofit.IMyService;
import Retrofit.RetrofitClient;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ConfirmEmailActivity extends AppCompatActivity {
    Button submitBtn;
    EditText mailEditText;
    IMyService iMyService;
    String mail;
    AlertDialog alertDialog;
    SharedPreferences sharedPreferences;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.activity_confirm_email);

        submitBtn = findViewById(R.id.findEmailSubmitBtn);
        mailEditText = findViewById(R.id.findEmailText);

        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(this).build();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckValidInput())
                    ConfirmMail();
            }
        });
    }

    private boolean CheckValidInput() {
        boolean valid=true;
        mail = mailEditText.getText().toString();

        if(mail.isEmpty() || mail.length() < 6 || mail.length() >40)
        {
            mailEditText.setError("Must from 6 to 40 characters");
            valid = false;
        }
        else {
            mailEditText.setError(null);
        }

        return valid;
    }

    private void ConfirmMail() {
        submitBtn.setClickable(false);
        submitBtn.setEnabled(false);

        try {
            iMyService.forgotPassword(mail)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<String>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Response<String> stringResponse) {
                            if (stringResponse.isSuccessful()) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("account_forgot_pass", mail);
                                editor.commit();
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
                            Toast.makeText(ConfirmEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            submitBtn.setClickable(true);
                            submitBtn.setEnabled(true);
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
                                Intent intent = new Intent(ConfirmEmailActivity.this, ConfimTokenActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toasty.error(ConfirmEmailActivity.this, "This account doesn't exist", Toasty.LENGTH_LONG).show();
                                submitBtn.setClickable(true);
                                submitBtn.setEnabled(true);
                            }
                        }
                    });
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}