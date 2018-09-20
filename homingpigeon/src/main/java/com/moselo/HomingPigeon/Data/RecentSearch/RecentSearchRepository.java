package com.moselo.HomingPigeon.Data.RecentSearch;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Data.HomingPigeonDatabase;

import java.util.List;

public class RecentSearchRepository {
    private RecentSearchDao recentSearchDao;
    private LiveData<List<RecentSearchEntity>> allRecentSearch;

    public RecentSearchRepository(Application application) {
        HomingPigeonDatabase db = HomingPigeonDatabase.getDatabase(application);
        recentSearchDao = db.recentSearchDao();
        allRecentSearch = recentSearchDao.getAllRecentSearchLive();
    }

    public void insert(RecentSearchEntity entity) {
        new Thread(() -> {
            recentSearchDao.insert(entity);
        }).start();
    }

    public void delete(RecentSearchEntity entity) {
        new Thread(() -> {
            recentSearchDao.delete(entity);
        }).start();
    }

    public void update(RecentSearchEntity entity) {
        new Thread(() -> {
            recentSearchDao.update(entity);
        }).start();
    }

    public LiveData<List<RecentSearchEntity>> getAllRecentSearch() {
        return allRecentSearch;
    }
}
