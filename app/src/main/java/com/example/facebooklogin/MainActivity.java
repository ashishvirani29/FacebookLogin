package com.example.facebooklogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.utils.Attributes;
import com.sromku.simple.fb.utils.PictureAttributes;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static SimpleFacebook mSimpleFacebook;
    Permission[] permissions = new Permission[]{
            Permission.USER_PHOTOS,
            Permission.EMAIL,
            Permission.PUBLISH_ACTION
    };
    public static LoginData logindata;
    String loginType = "";
    private ImageView imgProfileImage;
    private TextView txtFirstName;
    private TextView txtLastName;
    private TextView txtEmail;
    private LinearLayout linearLayout;
    private Button btnLogin;
    private Button btnLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        findViews();
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getResources().getString(R.string.facebook_app_id))
                .setNamespace("fblogin")
                .setPermissions(permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);


    }

    private void findViews() {
        imgProfileImage = (ImageView) findViewById(R.id.imgProfileImage);
        txtFirstName = (TextView) findViewById(R.id.txtFirstName);
        txtLastName = (TextView) findViewById(R.id.txtLastName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginType = "facebook";
                mSimpleFacebook.login(onLoginListener);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                btnLogout.setVisibility(View.GONE);
                btnLogin.setVisibility(View.VISIBLE);
            }
        });
    }

    OnProfileListener onProfileListener = new OnProfileListener() {

        @Override
        public void onFail(String reason) {
            super.onFail(reason);
            Log.e(TAG, "onFail: ");


        }

        @Override
        public void onException(Throwable throwable) {
            super.onException(throwable);
            Log.e(TAG, "onException: ");


        }

        @Override
        public void onComplete(Profile profile) {
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
            Log.e(TAG, "onComplete: " + profile);
            Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
            LoginData loginData = new LoginData();
            loginData.setFirst_name(profile.getFirstName());
            loginData.setLast_name(profile.getLastName());
            loginData.setEmail(profile.getEmail());
            loginData.setProfile_pic(profile.getPicture());
            loginData.setUser_id(profile.getId());
            loginData.setLogin_type(loginType);

            logindata = loginData;

            try {
                Picasso.with(MainActivity.this).load(logindata.getProfile_pic()).placeholder(R.mipmap.ic_launcher).into(imgProfileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            txtFirstName.setText("First Name: " + loginData.getFirst_name());
            txtLastName.setText("Last Name: " + loginData.getLast_name());
            txtEmail.setText("Email:  " + loginData.getEmail());
        }

    };
    OnLoginListener onLoginListener = new OnLoginListener() {

        @Override
        public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
            Log.i(TAG, "Logged in");
            PictureAttributes pictureAttributes = Attributes.createPictureAttributes();
            pictureAttributes.setHeight(500);
            pictureAttributes.setWidth(500);
            pictureAttributes.setType(PictureAttributes.PictureType.SMALL);
            Profile.Properties properties = new Profile.Properties.Builder()
                    .add(Profile.Properties.ID)
                    .add(Profile.Properties.FIRST_NAME)
                    .add(Profile.Properties.EMAIL)
                    .add(Profile.Properties.LAST_NAME)
                    .add(Profile.Properties.LINK)
                    .add(Profile.Properties.PICTURE, pictureAttributes).build();

            mSimpleFacebook.getProfile(properties, onProfileListener);
        }

        @Override
        public void onCancel() {
            // user canceled the dialog
        }

        @Override
        public void onFail(String reason) {
            // failed to login
        }

        @Override
        public void onException(Throwable throwable) {
            // exception from facebook
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }
}
