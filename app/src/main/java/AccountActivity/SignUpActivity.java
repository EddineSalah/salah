package AccountActivity;

import android.Manifest;
import android.accounts.Account;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adib.iread.MainActivity;
import com.adib.iread.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUpActivity extends AppCompatActivity {

    ImageView ImgUserPhoto;
    static int PreCode =1;
    static  int ReqCode =1;
    Uri pickedImgUri;

    private EditText userName,userMail,userPass1,userPass2;
    private ProgressBar loadingprocess;
    private Button userClikBtn;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = findViewById(R.id.signName);
        userMail = findViewById(R.id.signMail);
        userPass1 = findViewById(R.id.signPass1);
        userPass2 = findViewById(R.id.signPass2);
        userClikBtn = findViewById(R.id.signBtn);
        loadingprocess =findViewById(R.id.signProg);
        loadingprocess.setVisibility(View.INVISIBLE);
        userClikBtn.setVisibility(View.VISIBLE);
        userClikBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name=userName.getText().toString();
                final  String email=userMail.getText().toString();
                final  String pass1=userPass1.getText().toString();
                final  String pass2=userPass2.getText().toString();
                loadingprocess.setVisibility(View.VISIBLE);
                userClikBtn.setVisibility(View.INVISIBLE);


                if (name.isEmpty()||email.isEmpty()||pass1.isEmpty()||pass2.isEmpty()|| !pass2.equals(pass1)){
                        showMessage("Please verify all fields");
                        userClikBtn.setVisibility(View.VISIBLE);
                        loadingprocess.setVisibility(View.INVISIBLE);

                    }else{
                        CreatUserAccount(name,email,pass1);
                    }

                }
            });


        ImgUserPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Build.VERSION.SDK_INT >= 22){
                        VerfyAndReqPermission();
                    }else{
                        OpenImage();
                    }
                }
            });
        }


        private void CreatUserAccount(final String name, String email, String pass1) {

            myAuth.createUserWithEmailAndPassword(email,pass1)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                showMessage("Account created");
                                UpdateUserInfo(name,pickedImgUri,myAuth.getCurrentUser());
                            }else {
                                showMessage("Account creation failed" + task.getException().getMessage());
                                userClikBtn.setVisibility(View.VISIBLE);
                                loadingprocess.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
        }

        private  void UpdateUserInfo(final String name, Uri pickedImgUri , final FirebaseUser currentUser){
            StorageReference myStorage = FirebaseStorage.getInstance().getReference().child("userPhotos");
            final StorageReference imageFilePath =myStorage.child(pickedImgUri.getLastPathSegment());
            imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UserProfileChangeRequest profilUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .setPhotoUri(uri)
                                    .build();
                            currentUser.updateProfile(profilUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                // user info updated successfully
                                                showMessage("Register Complete");
                                                UpdateUI();
                                            }
                                        }
                                    });
                        }
                    });
                }
            });
        }

        private void UpdateUI() {
            Intent homeActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(homeActivity);
            finish();
        }

        private void showMessage(String msg) {
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
        }

        private void OpenImage() {
            //open galary intent and wait for the user to pick image
            Intent gallaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            gallaryIntent.setType("image/*");
            startActivityForResult(gallaryIntent,ReqCode);
        }

        private void VerfyAndReqPermission() {
            if(ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(SignUpActivity.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PreCode);
                }
            }
            else{
                OpenImage();
            }

        }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode,data);
            if (resultCode == RESULT_OK && requestCode == ReqCode && data != null) {
                //the user has successfuly picked an image
                //we need to share
                pickedImgUri = data.getData();
                ImgUserPhoto.setImageURI(pickedImgUri);
            }
        }
    }
