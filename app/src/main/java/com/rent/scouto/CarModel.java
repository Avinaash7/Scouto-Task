package com.rent.scouto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "car_table")
public class CarModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    String Make_Name;
    String Model_Name;
    String Image_Path;

    public CarModel(String make_Name, String model_Name, String image_Path) {
        Make_Name = make_Name;
        Model_Name = model_Name;
        Image_Path = image_Path;
    }

    public String getImage_Path() {
        return Image_Path;
    }

    public void setImage_Path(String image_Path) {
        Image_Path = image_Path;
    }


    public CarModel(){

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
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
