package com.moselo.HomingPigeon.Data.Contact;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Data.HomingPigeonDatabase;
import com.moselo.HomingPigeon.Listener.HpDatabaseListener;
import com.moselo.HomingPigeon.Model.HpUserModel;

import java.util.List;

public class HpMyContactRepository {
    private HpMyContactDao myContactDao;
    private LiveData<List<HpUserModel>> myContactListLive;

    public HpMyContactRepository(Application application) {
        HomingPigeonDatabase db = HomingPigeonDatabase.getDatabase(application);
        myContactDao = db.myContactDao();
        myContactListLive = myContactDao.getAllMyContactLive();
    }

    public void insert(HpUserModel... userModels) {
        new Thread(() -> myContactDao.insert(userModels)).start();
    }

    public void insert(List<HpUserModel> userModels) {
        new Thread(() -> myContactDao.insert(userModels)).start();
    }

    public void insertAndGetContact(List<HpUserModel> userModels, HpDatabaseListener<HpUserModel> listener) {
        new Thread(() ->{
            myContactDao.insert(userModels);
            List<HpUserModel> myContactList = myContactDao.getAllMyContact();
            listener.onSelectFinished(myContactList);
        }).start();
    }

    public void delete(HpUserModel... userModels) {
        new Thread(() -> myContactDao.delete(userModels)).start();
    }

    public void delete(List<HpUserModel> userModels) {
        new Thread(() -> myContactDao.delete(userModels)).start();
    }

    public void update(HpUserModel userModel) {
        new Thread(() -> myContactDao.update(userModel)).start();
    }

    public void getAllMyContactList(HpDatabaseListener<HpUserModel> listener) {
        new Thread(() -> {
            List<HpUserModel> myContactList = myContactDao.getAllMyContact();
            listener.onSelectFinished(myContactList);
        });
    }

    public LiveData<List<HpUserModel>> getMyContactListLive() {
        return myContactListLive;
    }
}
