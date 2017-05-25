package fischeranthony.com.a_fischer_android_usersanddata.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.database.FirebaseDatabase;

public class NetworkUtil {

//    private static FirebaseDatabase mDatabase;
//
//    public static FirebaseDatabase getDatabase() {
//        if (mDatabase == null) {
//            mDatabase = FirebaseDatabase.getInstance();
//            mDatabase.setPersistenceEnabled(true);
//        }
//        return mDatabase;
//    }

    public static boolean hasInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
}
