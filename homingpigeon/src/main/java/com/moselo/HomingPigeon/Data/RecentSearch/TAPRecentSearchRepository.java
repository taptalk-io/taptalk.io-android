package com.moselo.HomingPigeon.Data.RecentSearch;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Data.TapTalkDatabase;

import java.util.List;

public class TAPRecentSearchRepository {
    private TAPRecentSearchDao recentSearchDao;
    private LiveData<List<TAPRecentSearchEntity>> allRecentSearch;

    public TAPRecentSearchRepository(Application application) {
        TapTalkDatabase db = TapTalkDatabase.getDatabase(application);
        recentSearchDao = db.recentSearchDao();
        allRecentSearch = recentSearchDao.getAllRecentSearchLive();
    }

    public void insert(TAPRecentSearchEntity entity) {
        new Thread(() -> recentSearchDao.insert(entity)).start();
    }

    public void delete(TAPRecentSearchEntity entity) {
        new Thread(() -> recentSearchDao.delete(entity)).start();
    }

    public void delete(List<TAPRecentSearchEntity> entities) {
        new Thread(() -> recentSearchDao.delete(entities)).start();
    }

    public void deleteAllRecentSearch() {
        new Thread(() -> recentSearchDao.deleteAllRecentSearch()).start();
    }

    public void update(TAPRecentSearchEntity entity) {
        new Thread(() -> recentSearchDao.update(entity)).start();
    }

    public LiveData<List<TAPRecentSearchEntity>> getAllRecentSearch() {
        return allRecentSearch;
    }
}
