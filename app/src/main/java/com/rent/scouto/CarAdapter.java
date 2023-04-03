package com.rent.scouto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {


    private List<CarModel> carlist;
    Context context;


    private AdapterListener adapterListener;


    public CarAdapter(Context context, AdapterListener adapterListener) {
        this.context = context;
        this.adapterListener = adapterListener;
        carlist = new ArrayList<>();
    }

    public void addUser(CarModel cars){
        carlist.add(cars);
        notifyDataSetChanged();
    }


    public void removeCar(int position){
        carlist.remove(position);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // below line is use to inflate our layout
        // file for each item of our recycler view.
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_item, parent, false);


        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarModel model = carlist.get(position);
        holder.courseNameTV.setText(model.getMake_Name());
        holder.courseDurationTV.setText(model.getModel_Name());
        if(!Objects.equals(model.getImage_Path(), "")){
            Bitmap b = loadImageFromStorage(model.getImage_Path(),model.getId());
            holder.img.setImageBitmap(b);
        }

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterListener.OnDelete(model.getId(),position);
            }
        });

        holder.addimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterListener.OnUpdate(model.getImage_Path(), model.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return carlist.size();
    }

    private Bitmap loadImageFromStorage(String path, long id)
    {

        try {
            File f=new File(path, "image"+id+".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
//            ImageView img=(ImageView)findViewById(R.id.imgPicker);
//            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }


    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        // view holder class to create a variable for each view.
        TextView courseNameTV, courseDurationTV;
        MaterialButton deleteBtn,addimgbtn;
        ImageView img;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing each view of our recycler view.
            courseNameTV = itemView.findViewById(R.id.idTVCourseName);
            courseDurationTV = itemView.findViewById(R.id.idTVCourseDuration);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            img = itemView.findViewById(R.id.carimageiv);
            addimgbtn = itemView.findViewById(R.id.addimgbtn);


        }
    }







}
