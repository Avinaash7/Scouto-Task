package com.rent.scouto;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CarAdapter extends ListAdapter<CarModel, CarAdapter.ViewHolder> {

    // creating a variable for on item click listener.
    private OnItemClickListener listener;
    private DeleteListener dellistener;
    private List<CarModel> carlist;
    Context context;
    CarModel carm;
    private ViewModal viewmodal;

    // creating a constructor class for our adapter class.
    CarAdapter() {
        super(DIFF_CALLBACK);
    }

    public CarAdapter(Context mcontext,DeleteListener mdellistener){
        this();
        context = mcontext;
        carlist = new ArrayList<>();
        dellistener = mdellistener;
    }

    public void removeCar(int position){
        carlist.remove(position);
        notifyDataSetChanged();
    }



    // creating a call back for item of recycler view.
    private static final DiffUtil.ItemCallback<CarModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<CarModel>() {
        @Override
        public boolean areItemsTheSame(CarModel oldItem, CarModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(CarModel oldItem, CarModel newItem) {
            // below line is to check the course name, description and course duration.
            return oldItem.getMake_Name().equals(newItem.getMake_Name()) &&
                    oldItem.getModel_Name().equals(newItem.getModel_Name()) ;
        }
    };

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
        //carm = ViewModelProviders.of(holder.co).get(ViewModal.class);


        CarModel model = getCourseAt(position);
        holder.courseNameTV.setText(model.getMake_Name());
        holder.courseDurationTV.setText(model.getModel_Name());
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dellistener.OnDelete(model.getId(),position);
              //  userModel.deleteItem(users);
            }
        });
    }

    // creating a method to get course modal for a specific position.
    public CarModel getCourseAt(int position) {

        return getItem(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // view holder class to create a variable for each view.
        TextView courseNameTV, courseDurationTV;
        MaterialButton deleteBtn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing each view of our recycler view.
            courseNameTV = itemView.findViewById(R.id.idTVCourseName);
            courseDurationTV = itemView.findViewById(R.id.idTVCourseDuration);
            deleteBtn = itemView.findViewById(R.id.delete_btn);



            // adding on click listener for each item of recycler view.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // inside on click listener we are passing
                    // position to our item of recycler view.
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(CarModel model);
    }

    public interface DeleteListener {

        void OnDelete(int id,int pos);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
