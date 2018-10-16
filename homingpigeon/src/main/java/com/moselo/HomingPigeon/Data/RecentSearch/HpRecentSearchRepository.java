package com.moselo.HomingPigeon.Data.RecentSearch;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Data.HomingPigeonDatabase;

import java.util.List;

public class HpRecentSearchRepository {
    private HpRecentSearchDao recentSearchDao;
    private LiveData<List<HpRecentSearchEntity>> allRecentSearch;

    public HpRecentSearchRepository(Application application) {
        HomingPigeonDatabase db = HomingPigeonDatabase.getDatabase(application);
        recentSearchDao = db.recentSearchDao();
        allRecentSearch = recentSearchDao.getAllRecentSearchLive();
    }

    public void insert(HpRecentSearchEntity entity) {
        new Thread(() -> recentSearchDao.insert(entity)).start();
    }

    public void delete(HpRecentSearchEntity entity) {
        new Thread(() -> recentSearchDao.delete(entity)).start();
    }

    public void delete(List<HpRecentSearchEntity> entities) {
        new Thread(() -> recentSearchDao.delete(entities)).start();
    }

    public void update(HpRecentSearchEntity entity) {
        new Thread(() -> recentSearchDao.update(entity)).start();
    }

    public LiveData<List<HpRecentSearchEntity>> getAllRecentSearch() {
        return allRecentSearch;
    }
}
