package com.rent.scouto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AddCarBottomSheetFragment.onSomeEventListener, CarAdapter.DeleteListener {

    FirebaseUser user;
    CarAdapter adapter;

    RequestQueue requestQueue;
    final String url = "https://vpic.nhtsa.dot.gov/api/vehicles/getallmakes?format=json";
    List<String> carmakelist;
    HashMap<String, String> carmap = new HashMap<>();
    private StringRequest mStringRequest;
    SharedPreferences prefs ;
    SharedPreferences.Editor myEdit;

    private RecyclerView coursesRV;
    private static final int ADD_COURSE_REQUEST = 1;
    private static final int EDIT_COURSE_REQUEST = 2;
    private ViewModal viewmodal;

    CarDatabase cardb;
    Dao cardao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardb = CarDatabase.getInstance(this);
        cardao = cardb.Dao();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_btn);
        coursesRV = findViewById(R.id.idRVCourses);


        prefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);




//        GoogleSignInOptions gso = new GoogleSignInOptions.
//                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
//                build();
//        GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(Objects.requireNonNull(getApplicationContext()),gso);
//        googleSignInClient.signOut();
//        FirebaseAuth.getInstance().signOut();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        if(user== null){
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
        }
        else{

        }



        carmakelist = new ArrayList<String>();

        getdata();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddCarBottomSheetFragment addcardialog = AddCarBottomSheetFragment.newInstance();
                addcardialog.show(getSupportFragmentManager(), "add_car");
            }
        });

        coursesRV.setLayoutManager(new LinearLayoutManager(this));
        coursesRV.setHasFixedSize(true);
        adapter = new CarAdapter(this,this);
        coursesRV.setAdapter(adapter);

        // passing a data from view modal.
        viewmodal = ViewModelProviders.of(this).get(ViewModal.class);

        viewmodal.getAllCourses().observe(this, new Observer<List<CarModel>>() {
            @Override
            public void onChanged(List<CarModel> models) {
                // when the data is changed in our models we are
                // adding that list to our adapter class.
                adapter.submitList(models);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // on recycler view item swiped then we are deleting the item of our recycler view.
                viewmodal.delete(adapter.getCourseAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Course deleted", Toast.LENGTH_SHORT).show();
            }
        }).
                // below line is use to attach this to recycler view.
                        attachToRecyclerView(coursesRV);

        adapter.setOnItemClickListener(new CarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CarModel model) {

            }
        });



    }

    private void getdata()
    { //RequestQueue initialized
        requestQueue = Volley.newRequestQueue(this);

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject array = new JSONObject(response);
                    JSONArray arr = array.getJSONArray("Results");
                    for (int i = 0; i < arr.length(); ++i) {
                        JSONObject rec = arr.getJSONObject(i);
                        String makeid = rec.getString("Make_ID");
                        String carmake = rec.getString("Make_Name");
                        carmakelist.add(carmake);
                        carmap.put(makeid,carmake);

                    }

//                    Gson gson = new Gson();
//                    String jsonText = gson.toJson(carmakelist);
                      myEdit = prefs.edit();
//                    myEdit.putString("key",jsonText);
                    for (String s : carmap.keySet()) {
                        myEdit.putString(s, carmap.get(s));
                    }
                    myEdit.apply();



                } catch (JSONException e) {
                    e.printStackTrace();
                }



                //Toast.makeText(getApplicationContext(),"Response :" + response.toString(), Toast.LENGTH_LONG).show();//display the response on screen

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("TAG","Error :" + error.toString());
            }
        });
        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(mStringRequest);

    }

    @Override
    public void someEvent(String _carMake, String _carModel) {
        CarModel model = new CarModel(_carMake, _carModel);
        viewmodal.insert(model);
        Toast.makeText(this, "Car Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void OnDelete(int id, int pos) {
    cardao.deleteById(id);
    adapter.removeCar(pos);
    }
}