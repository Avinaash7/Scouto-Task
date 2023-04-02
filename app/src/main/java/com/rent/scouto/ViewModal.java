package com.rent.scouto;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModal extends AndroidViewModel {
    // creating a new variable for course repository.
    private CarRepository repository;

    // below line is to create a variable for live
    // data where all the courses are present.
    private LiveData<List<CarModel>> allCourses;

    public ViewModal(@NonNull Application application) {
        super(application);
        repository = new CarRepository(application);
        allCourses = repository.getAllCourses();
    }
    public void insert(CarModel model) {
        repository.insert(model);
    }

    // below line is to update data in our repository.
    public void update(CarModel model) {
        repository.update(model);
    }

    // below line is to delete the data in our repository.
    public void delete(CarModel model) {
        repository.delete(model);
    }

    // below method is to delete all the courses in our list.
    public void deleteAllCourses() {
        repository.deleteAllCourses();
    }

    // below method is to get all the courses in our list.
    public LiveData<List<CarModel>> getAllCourses() {
        return allCourses;
    }
}
