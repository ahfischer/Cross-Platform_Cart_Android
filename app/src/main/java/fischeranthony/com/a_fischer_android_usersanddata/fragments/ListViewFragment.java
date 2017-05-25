package fischeranthony.com.a_fischer_android_usersanddata.fragments;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fischeranthony.com.a_fischer_android_usersanddata.Utils.NetworkUtil;
import fischeranthony.com.a_fischer_android_usersanddata.activities.MainActivity;
import fischeranthony.com.a_fischer_android_usersanddata.R;
import fischeranthony.com.a_fischer_android_usersanddata.objects.Item;

public class ListViewFragment extends ListFragment {

    public static final String TAG = "ListViewFragment.TAG";

    private static final String ARG_ITEMS = "com.fullsail.android.ARG_ITEMS";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference mDatabase;
    private String mUserId;

    private ArrayList<Item> mCartItems;

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    public static ListViewFragment newInstance(ArrayList<Item> itemsArg) {
        ListViewFragment listViewFragment = new ListViewFragment();
        Bundle arg = new Bundle();
        arg.putSerializable(ARG_ITEMS, itemsArg);
        listViewFragment.setArguments(arg);
        return listViewFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mUserId = mFirebaseUser.getUid();
        mCartItems = new ArrayList<>();

        mDatabase.child("users").child(mUserId).child("Cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!NetworkUtil.hasInternet(getActivity())) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setMessage("No internet connection, will sync all database items once internet connection restored.");
                    alert.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                }

                if (snapshot.getValue() != null) {

                    // cart exists, grab all values to display in list
                    for(final DataSnapshot dsp : snapshot.getChildren()){

                        // Get all cart item names
                        String itemName = dsp.getKey();
                        String itemType = "";
                        int itemQuantity = 0;

                        // Loop through all cart items
                        for(DataSnapshot itemDataSnapshot : dsp.getChildren()) {
                            if (itemDataSnapshot.getValue() instanceof String) {
                                itemType = (String) itemDataSnapshot.getValue();
                            } else {

                                // Typecast long to int
                                itemQuantity = (int) ( (long) itemDataSnapshot.getValue() );
                            }
                        }
                        mCartItems.add(new Item(itemName, itemType, itemQuantity));
                    }

                    if (mCartItems != null && mCartItems.size() > 0) {

                        // Setting Up HashMap for Simple Adapter
                        String fromArray[] = {"itemName", "itemQuantity"};
                        List<HashMap<String, String>> hashMapData = new ArrayList<>();

                        for (Item _item : mCartItems) {
                            HashMap<String, String> tempHashMap = new HashMap<>();

                            tempHashMap.put("itemName", _item.getmItemName());
                            tempHashMap.put("itemQuantity", String.valueOf(_item.getmQuantity()));
                            hashMapData.add(tempHashMap);
                        }

                        // Simple Adapter
                        final SimpleAdapter simpleAdapter = new SimpleAdapter(
                                getActivity(),
                                hashMapData,
                                android.R.layout.simple_list_item_2,
                                fromArray,
                                new int[]{android.R.id.text1, android.R.id.text2}
                        );

                        setListAdapter(simpleAdapter);
                    }
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

        setEmptyText("No data Available");

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> av, View v, final int position, long id) {
                //Get your item here with the position

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setMessage("Are you sure you want to delete this entry?");
                alert.setCancelable(true);
                alert.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mDatabase.child("users").child(mUserId).child("Cart").child(mCartItems.get(position).getmItemName()).removeValue();
                                mCartItems.remove(mCartItems.get(position));
                                // Setting Up HashMap for Simple Adapter
                                String fromArray[] = {"itemName", "itemQuantity"};
                                List<HashMap<String, String>> hashMapData = new ArrayList<>();

                                if (mCartItems.size() > 0) {
                                    for (Item _item : mCartItems) {
                                        HashMap<String, String> tempHashMap = new HashMap<>();

                                        tempHashMap.put("itemName", _item.getmItemName());
                                        tempHashMap.put("itemQuantity", String.valueOf(_item.getmQuantity()));
                                        hashMapData.add(tempHashMap);
                                    }
                                }

                                // Simple Adapter
                                final SimpleAdapter simpleAdapter = new SimpleAdapter(
                                        getActivity(),
                                        hashMapData,
                                        android.R.layout.simple_list_item_2,
                                        fromArray,
                                        new int[]{android.R.id.text1, android.R.id.text2}
                                );

                                setListAdapter(simpleAdapter);
                                setEmptyText("No Cart Items");

                                if (NetworkUtil.hasInternet(getActivity())) {
                                    Toast.makeText(getActivity(), "Entry deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Will delete this entry from database once internet connection is restored.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                alert.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();

                return true;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        super.onListItemClick(l, v, position, id);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("Would you like to edit this entry?");
        alert.setCancelable(true);
        alert.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FormFragment formFragment = FormFragment.newInstance(mCartItems.get(position));
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, formFragment, FormFragment.TAG)
                                .commit();
                    }
                });
        alert.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
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
