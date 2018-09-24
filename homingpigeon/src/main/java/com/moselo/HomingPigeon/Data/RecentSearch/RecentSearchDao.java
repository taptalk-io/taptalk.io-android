package com.moselo.HomingPigeon.Data.RecentSearch;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RecentSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RecentSearchEntity... entities);

    @Delete
    void delete(RecentSearchEntity entity);

    @Delete
    void delete(List<RecentSearchEntity> entities);

    @Update
    void update(RecentSearchEntity entity);

    @Query("select * from recent_search order by Created desc")
    LiveData<List<RecentSearchEntity>> getAllRecentSearchLive();
}
