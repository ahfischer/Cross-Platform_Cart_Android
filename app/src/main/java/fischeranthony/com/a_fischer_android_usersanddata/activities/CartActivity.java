package fischeranthony.com.a_fischer_android_usersanddata.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fischeranthony.com.a_fischer_android_usersanddata.R;
import fischeranthony.com.a_fischer_android_usersanddata.fragments.FormFragment;
import fischeranthony.com.a_fischer_android_usersanddata.fragments.ListViewFragment;
import fischeranthony.com.a_fischer_android_usersanddata.objects.Item;

public class CartActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference mDatabase;
    private String mUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mUserId = mFirebaseUser.getUid();

        // pull data from database, if none, launch form fragment
        mDatabase.child("users").child(mUserId).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    // cart does exist, launch list fragment
                    ListViewFragment listViewFragment = ListViewFragment.newInstance();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, listViewFragment, ListViewFragment.TAG)
                            .commit();
                } else {

                    // cart does not exist, launch form fragment
                    FormFragment formFragment = FormFragment.newInstance();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, formFragment, FormFragment.TAG)
                            .commit();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {

                // Failed to read value
                Log.w("Failed to read value.", error.toException());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);

        return true;
    }
}
