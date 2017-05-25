package fischeranthony.com.a_fischer_android_usersanddata.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import fischeranthony.com.a_fischer_android_usersanddata.R;
import fischeranthony.com.a_fischer_android_usersanddata.fragments.LoginFragment;

public class MainActivity extends BaseClass {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup actionbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // Initialize Firebase Auth and Database Reference
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.keepSynced(true);

        // Automatically logs you in if you never log out manually
        if (mFirebaseUser == null) {

            // Not logged in, launch the Log In Fragment
            LoginFragment loginFragment = LoginFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, loginFragment, LoginFragment.TAG)
                    .commit();
        } else {

            String mUserId = mFirebaseUser.getUid();

            // Setup Cart
            mDatabase.child("users").child(mUserId);

            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);

            finish();
        }

    }
}
