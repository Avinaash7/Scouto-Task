package com.rent.scouto;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "car_table")
public class CarModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    String Make_Name;
    String Model_Name;

    public CarModel(String make_Name, String model_Name) {
        Make_Name = make_Name;
        Model_Name = model_Name;
    }

    public CarModel(){

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel_Name() {
        return Model_Name;
    }

    public void setModel_Name(String model_Name) {
        Model_Name = model_Name;
    }


    public String getMake_Name() {
        return Make_Name;
    }

    public void setMake_Name(String make_Name) {
        Make_Name = make_Name;
    }
}
