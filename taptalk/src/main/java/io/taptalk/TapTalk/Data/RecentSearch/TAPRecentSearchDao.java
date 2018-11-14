package io.taptalk.TapTalk.Data.RecentSearch;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TAPRecentSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TAPRecentSearchEntity... entities);

    @Delete
    void delete(TAPRecentSearchEntity entity);

    @Delete
    void delete(List<TAPRecentSearchEntity> entities);

    @Query("delete from Recent_Search")
    void deleteAllRecentSearch();

    @Update
    void update(TAPRecentSearchEntity entity);

    @Query("select * from Recent_Search order by created desc")
    LiveData<List<TAPRecentSearchEntity>> getAllRecentSearchLive();
}
