package com.ssttkkl.fgoplanningtool.data.item;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "Item")
public class ItemDatabaseEntity {
    @PrimaryKey
    @NotNull
    public String codename;
    public Integer count;

    public ItemDatabaseEntity(@NotNull String codename, Integer count) {
        this.codename = codename;
        this.count = count;
    }

    public ItemDatabaseEntity(Item item) {
        this(item.getCodename(), item.getCount());
    }

    public Item getItem() {
        return new Item(codename, count);
    }
}
