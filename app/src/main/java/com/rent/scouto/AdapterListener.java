package com.rent.scouto;

public interface AdapterListener {

    void OnDelete(long id, int pos);

    void OnUpdate(String path , long id);
}
