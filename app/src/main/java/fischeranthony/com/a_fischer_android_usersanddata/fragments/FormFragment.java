package fischeranthony.com.a_fischer_android_usersanddata.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import fischeranthony.com.a_fischer_android_usersanddata.Utils.NetworkUtil;
import fischeranthony.com.a_fischer_android_usersanddata.activities.MainActivity;
import fischeranthony.com.a_fischer_android_usersanddata.R;
import fischeranthony.com.a_fischer_android_usersanddata.objects.Item;

public class FormFragment extends Fragment {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference mDatabase;
    private String mUserId;

    private Item mItem;

    public static final String TAG = "FormFragment.TAG";

    private static final String ARG_ITEM = "com.fullsail.android.ARG_ITEM";

    public static FormFragment newInstance() {
        return new FormFragment();
    }

    public static FormFragment newInstance(Item itemArg) {
        FormFragment formFragment = new FormFragment();
        Bundle arg = new Bundle();
        arg.putSerializable(ARG_ITEM, itemArg);
        formFragment.setArguments(arg);
        return formFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_form, container, false);

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        final EditText itemNameEditText = (EditText) view.findViewById(R.id.itemNameEditText);
        final EditText itemTypeEditText = (EditText) view.findViewById(R.id.itemTypeEditText);
        final EditText itemQuantityEditText = (EditText) view.findViewById(R.id.itemQuantityEditText);
        Button submitButton = (Button) view.findViewById(R.id.submitButton);

        // If user is editing
        Bundle bundle = getArguments();
        if (bundle != null) {
            mItem = (Item) bundle.getSerializable(ARG_ITEM);

            if (mItem != null) {

                // Lock in item that has values to edit
                itemNameEditText.setText(mItem.getmItemName());
                itemNameEditText.setEnabled(false);
                itemNameEditText.setBackgroundColor(Color.TRANSPARENT);

                // Load with original values
                itemTypeEditText.setText(mItem.getmItemType());
                itemQuantityEditText.setText(String.valueOf(mItem.getmQuantity()));
            }
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemNameEditText.getText().toString().equals("") &&
                        !itemTypeEditText.getText().toString().equals("") &&
                        !itemQuantityEditText.getText().toString().equals("") &&
                        Integer.parseInt(itemQuantityEditText.getText().toString()) > 0) {
                    String name = itemNameEditText.getText().toString();
                    String type = itemTypeEditText.getText().toString();
                    int quantity = Integer.parseInt(itemQuantityEditText.getText().toString());

                    // Initialize Firebase Auth and Database Reference
                    mFirebaseAuth = FirebaseAuth.getInstance();
                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mUserId = mFirebaseUser.getUid();

                    // Update Database with new values
                    DatabaseReference itemReference = mDatabase.child("users").child(mUserId).child("Cart").child(name);
                    itemReference.child("Category").setValue(type);
                    itemReference.child("Quantity").setValue(quantity);

                    // Launch listViewFragment to view
                    ListViewFragment listViewFragment = ListViewFragment.newInstance();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, listViewFragment, ListViewFragment.TAG)
                            .commit();
                } else {
                    Toast.makeText(getActivity(), "Please fill out all fields and ensure quantity is greater than zero.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {

            if (NetworkUtil.hasInternet(getActivity())) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setMessage("No internet connection, cannot sign out. Please try again once internet connection is restored.");
                alert.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
            return true;
        }
        if (item.getItemId() == R.id.action_add_item) {
            FormFragment formFragment = FormFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, formFragment, FormFragment.TAG)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
