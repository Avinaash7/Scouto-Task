package com.rent.scouto;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@androidx.room.Dao
public interface Dao {
    // below method is use to
    // add data to database.
    @Insert
    long insert(CarModel model);

    // below method is use to update
    // the data in our database.
    @Query("UPDATE car_table SET Image_Path=:path WHERE id=:id")
    void update(String path,long id);

    // below line is use to delete a
    // specific course in our database.
    @Delete
    void delete(CarModel model);

    @Query("DELETE FROM car_table WHERE id=:id")
    void deleteById(long id);

    // on below line we are making query to
    // delete all courses from our database.
    @Query("DELETE FROM car_table")
    void deleteAllCourses();

    // below line is to read all the courses from our database.
    // in this we are ordering our courses in ascending order
    // with our course name.
    @Query("SELECT * FROM car_table ORDER BY Make_Name ASC")
    List<CarModel> getAllCourses();
}
