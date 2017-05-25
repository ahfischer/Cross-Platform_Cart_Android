package fischeranthony.com.a_fischer_android_usersanddata.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class BaseClass extends AppCompatActivity {

    private static String TAG = "BaseActivity";

    static boolean isInitialized = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Have to do this because .setPersistenceEnabled is terrible
        try{
            if(!isInitialized){
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                isInitialized = true;
            }else {
                Log.i(TAG,"Already Initialized");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
