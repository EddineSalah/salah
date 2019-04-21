package AccountActivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adib.iread.MainActivity;
import com.adib.iread.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Search books,Search books, authors, publishers…
public class LoginActivity extends AppCompatActivity {

    private EditText userMail,userPassword;
    private Button userBtn;
    private ProgressBar userPrg;
    private FirebaseAuth userAuth;
    private Intent  homeActivity;
    private TextView SignMsg;
    private TextView RestPass;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userMail = findViewById(R.id.log_Name);
        userPassword=findViewById(R.id.log_Pass);
        userAuth = FirebaseAuth.getInstance();
        homeActivity = new Intent(LoginActivity.this, MainActivity.class);

        SignMsg = findViewById(R.id.log_Signup);
        SignMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signAct = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signAct);
                finish();
            }
        });
        RestPass = findViewById(R.id.log_Rest);
        RestPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetActivity.class));
            }
        });
        userPrg = findViewById(R.id.log_Prog);
        userPrg.setVisibility(View.INVISIBLE);

        userBtn =findViewById(R.id.log_Btn);
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPrg.setVisibility(View.VISIBLE);
                userBtn.setVisibility(View.INVISIBLE);

                final String mail = userMail.getText().toString();
                final  String password = userPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()){
                    ShowMessage("Please verify all fields");
                    userPrg.setVisibility(View.INVISIBLE);
                    userBtn.setVisibility(View.VISIBLE);
                }else{
                    SignIn(mail,password);
                }

            }
        });
    }

    private void SignIn(String mail, String password) {
        userAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    userPrg.setVisibility(View.INVISIBLE);
                    userBtn.setVisibility(View.VISIBLE);
                    updateUI();
                }
                else {
                    ShowMessage(task.getException().getMessage());
                    userPrg.setVisibility(View.INVISIBLE);
                    userBtn.setVisibility(View.VISIBLE);





                }
            }
        });
    }

    private void updateUI() {
        startActivity(homeActivity);
        finish();
    }

    private void ShowMessage(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = userAuth.getCurrentUser();
        if (user != null){
            updateUI();
        }

    }
}
