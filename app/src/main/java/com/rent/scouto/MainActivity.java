package com.rent.scouto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AddCarBottomSheetFragment.onSomeEventListener, AdapterListener {

    FirebaseUser user;
    CarAdapter adapter;
    long globalid=0;

    RequestQueue requestQueue;
    final String url = "https://vpic.nhtsa.dot.gov/api/vehicles/getallmakes?format=json";
    List<String> carmakelist;
    HashMap<String, String> carmap = new HashMap<>();
    private StringRequest mStringRequest;
    SharedPreferences prefs ;
    SharedPreferences.Editor myEdit;

    private RecyclerView coursesRV;

    CarModel uni;


    CarDatabase cardb;
    Dao cardao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardb = CarDatabase.getInstance(this);
        cardao = cardb.getDao();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_btn);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        coursesRV = findViewById(R.id.idRVCourses);


        prefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.logout) {
                    GoogleSignInOptions gso = new GoogleSignInOptions.
                            Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                            build();
                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getApplicationContext()), gso);
                    googleSignInClient.signOut();
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(myIntent);
                    return true;
                }
                return false;
            }
        });






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






    }

    private void getdata()
    {
        requestQueue = Volley.newRequestQueue(this);


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

                      myEdit = prefs.edit();
                    for (String s : carmap.keySet()) {
                        myEdit.putString(s, carmap.get(s));
                    }
                    myEdit.apply();



                } catch (JSONException e) {
                    e.printStackTrace();
                }


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
    public void someEvent(String _carMake, String _carModel,String imagepath) {
        CarModel model = new CarModel(_carMake, _carModel,imagepath);
        long autoid = cardao.insert(model);
        Log.i("ID", String.valueOf(autoid));
        model.setId(autoid);
        adapter.addUser(model);

        Toast.makeText(this, "Car Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void OnDelete(long id, int pos) {
        Log.i("Okay2", String.valueOf(id));
    cardao.deleteById(id);
    adapter.removeCar(pos);
    }

    @Override
    public void OnUpdate(String path, long id) {
        Log.i("Okay", String.valueOf(id));
        globalid = id;
        selectImage();


    }

    @Override
    protected void onResume(){
        super.onResume();
        adapter = new CarAdapter(this,this);
        coursesRV.setAdapter(adapter);
        coursesRV.setLayoutManager(new LinearLayoutManager(this));
        coursesRV.setHasFixedSize(true);

        fetchAllData();

    }

    private void fetchAllData(){
        List<CarModel>  carlist = cardao.getAllCourses();
        for(int i =0;i< carlist.size();i++){
            CarModel carmo = carlist.get(i);
            adapter.addUser(carmo);
        }
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(Environment.getExternalStorageDirectory(), "tempx.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Welcome", String.valueOf(resultCode));
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Log.i("Welcome","1");
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("tempx.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Log.i("Welcome","2");

                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    Log.i("Welcome","3");
                    String x = saveToInternalStorage(bitmap,globalid);
                    Log.i("Welcome","4");
                    cardao.update(x,globalid);


                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Log.i("Welcome","pol");
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("path", picturePath);

                String x = saveToInternalStorage(thumbnail, globalid);
                //CarModel carsx = new CarModel(uni.getId(),uni.getMake_Name(),uni.getModel_Name(),x);
                cardao.update(x,globalid);
              //  viewImage.setImageBitmap(thumbnail);
            }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage, long id){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"image"+id+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i("Absoulyte Path",directory.getAbsolutePath());
        return directory.getAbsolutePath();
    }




}