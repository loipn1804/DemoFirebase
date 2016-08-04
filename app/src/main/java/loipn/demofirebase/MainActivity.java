package loipn.demofirebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAdd;
    private Button btnLoad;
    private Button btnUpdate;

    private TextView txtMessage;

    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnLoad = (Button) findViewById(R.id.btnLoad);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        txtMessage = (TextView) findViewById(R.id.txtMessage);

        btnAdd.setOnClickListener(this);
        btnLoad.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        txtMessage.setText("message");
    }

    private void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                addPokemon();
                break;
            case R.id.btnLoad:
                loadLocation();
                break;
            case R.id.btnUpdate:
                updatePokemon("-KNw67W3bGwaQHzqLaxy");
                break;
        }
    }

    private void addPokemon() {
        num++;
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("pokemon");
        DatabaseReference newPostRef = myRef.push();
        newPostRef.setValue(new Pokemon(num, "abcdef" + num));
        newPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(MainActivity.this, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                addLocation(dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addLocation(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("geofire");
        GeoFire geoFire = new GeoFire(myRef);

        geoFire.setLocation(key, new GeoLocation(10.761766, 106.668674), new GeoFire.CompletionListener() {

            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Toast.makeText(MainActivity.this, "" + "" + error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "" + "Location saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadLocation() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("geofire");
        GeoFire geoFire = new GeoFire(myRef);

        final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(10.778513, 106.680583), 1);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("LOCATION", String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                loadPokemon(key);
            }

            @Override
            public void onKeyExited(String key) {
                Log.d("LOCATION", String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d("LOCATION", String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                Log.d("LOCATION", "All initial data has been loaded and events have been fired!");
                geoQuery.removeAllListeners();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d("LOCATION", "There was an error with this query: " + error);
            }
        });
    }

    private void loadPokemon(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference myRef = database.getReference("pokemon");
        myRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pokemon pokemon = dataSnapshot.getValue(Pokemon.class);
                Log.d("LOCATION", pokemon.getId() + " " + pokemon.getName());
                Log.d("LOCATION", "KEY " + dataSnapshot.getKey());
                myRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updatePokemon(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("pokemon");
        Map<String, Object> pokemon = new HashMap<String, Object>();
        pokemon.put("name", "phan ngoc loi");

        myRef.child(key).updateChildren(pokemon, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(MainActivity.this, "" + "" + databaseError, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "" + "Pokemon updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void installFireBase(final Context context, FirebaseAuth mAuth, final DatabaseReference mDatabase) {

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            mAuth.signInAnonymously();
        }

        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("FireBaseManagement", "onAuthStateChanged:signed_in:" + user.getUid());
//                    writeNewUser(context, user.getUid(), mDatabase);
                } else {
                    // User is signed out
                    Log.d("FireBaseManagement", "onAuthStateChanged:signed_out");
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }
}
