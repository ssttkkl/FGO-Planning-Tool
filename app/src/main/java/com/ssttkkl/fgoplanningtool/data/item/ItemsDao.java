package com.ssttkkl.fgoplanningtool.data.item;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ItemsDao {
    @Query("SELECT * FROM item")
    List<ItemDatabaseEntity> getAllItems();

    @Query("SELECT * FROM item")
    LiveData<List<ItemDatabaseEntity>> getAllItemsAsLiveData();

    @Query("SELECT * FROM item WHERE codename=:codename")
    ItemDatabaseEntity getItemByName(String codename);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateItem(ItemDatabaseEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateItems(List<ItemDatabaseEntity> item);
}
