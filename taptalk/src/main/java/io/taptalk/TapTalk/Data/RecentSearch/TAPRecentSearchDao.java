package io.taptalk.TapTalk.Data.RecentSearch;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
