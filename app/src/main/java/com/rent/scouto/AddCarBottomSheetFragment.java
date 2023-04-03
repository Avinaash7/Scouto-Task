package com.rent.scouto;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCarBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCarBottomSheetFragment extends BottomSheetDialogFragment {

    public interface onSomeEventListener {
        public void someEvent(String _carMake,String _carModel,String imagepath);
    }

    onSomeEventListener someEventListener;

    int EXTRA_ID;
    String EXTRA_COURSE_NAME;
    String EXTRA_DESCRIPTION;

    SharedPreferences prefs;
    HashMap<String, String> map;
    RequestQueue requestQueue2;
    private StringRequest mStringRequest2;
    List<String> models_list;
    ArrayAdapter<String> models_adapter;
    AutoCompleteTextView model_tv;
    Bundle bundle;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static AddCarBottomSheetFragment newInstance() {
        return new AddCarBottomSheetFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCarBottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddCarBottomSheetFragment newInstance(String param1, String param2) {
        AddCarBottomSheetFragment fragment = new AddCarBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            EXTRA_ID = getArguments().getInt("Extra_Id",-1);
            EXTRA_COURSE_NAME = getArguments().getString("Course_Name","");
            EXTRA_DESCRIPTION = getArguments().getString("Course_desc","");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_car_bottom_sheet, container, false);

        AutoCompleteTextView carmaketv =(AutoCompleteTextView)v.findViewById(R.id.make_tv);
        model_tv =(AutoCompleteTextView)v.findViewById(R.id.modeltv);
        MaterialButton addbtn = (MaterialButton) v.findViewById(R.id.addcarbtn);

        prefs = requireActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        map = new HashMap<String,String>();


        List<String> carmakelist;
        carmakelist = new ArrayList<String>();
        map = (HashMap<String, String>) prefs.getAll();
        for (String s : map.keySet()) {
            String value=map.get(s);
            carmakelist.add(value);
            //Use Value
        }

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(requireActivity(),android.R.layout.simple_dropdown_item_1line,carmakelist);

        carmaketv.setAdapter(adapter);

        carmaketv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String)adapterView.getItemAtPosition(i);
                for (String s : map.keySet()) {
                    String value=map.get(s);
                    if(Objects.equals(value, selection)){
                        Log.i("ModelNo",s);
                        getmodel("https://vpic.nhtsa.dot.gov/api/vehicles/GetModelsForMakeId/"+s+"?format=json");

                        break;
                    }
                }
            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carmake = carmaketv.getText().toString();
                String carmodel = model_tv.getText().toString();

                if (carmake.isEmpty() || carmodel.isEmpty()) {
                    Toast.makeText(requireActivity(), "Please enter the valid course details.", Toast.LENGTH_SHORT).show();
                }else{
                    sendData(carmake,carmodel);
                }
                // calling a method to save our course.

            }
        });

        return v;
    }

    private void getmodel(String url)
    {

        models_list = new ArrayList<String>();

        requestQueue2 = Volley.newRequestQueue(requireActivity());

        //String Request initialized
        mStringRequest2 = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject array = new JSONObject(response);
                    JSONArray arr = array.getJSONArray("Results");
                    for (int i = 0; i < arr.length(); ++i) {
                        JSONObject rec = arr.getJSONObject(i);
                        String model_name = rec.getString("Model_Name");
                        Log.i("Model",model_name);
                        models_list.add(model_name);

                    }
                    models_adapter =new ArrayAdapter<String>(requireActivity(),android.R.layout.simple_dropdown_item_1line,models_list);
                    model_tv.setAdapter(models_adapter);


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
        mStringRequest2.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue2.add(mStringRequest2);

    }

    private void sendData(String carName, String carModel) {
        someEventListener.someEvent(carName,carModel,"");
        //saveCourse(courseName, courseDesc);
        dismiss();
    }

}