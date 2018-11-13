package com.moselo.HomingPigeon.Data.Contact;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Data.TapTalkDatabase;
import com.moselo.HomingPigeon.Listener.TAPDatabaseListener;
import com.moselo.HomingPigeon.Model.HpUserModel;

import java.util.List;

public class TAPMyContactRepository {
    private TAPMyContactDao myContactDao;
    private LiveData<List<HpUserModel>> myContactListLive;

    public TAPMyContactRepository(Application application) {
        TapTalkDatabase db = TapTalkDatabase.getDatabase(application);
        myContactDao = db.myContactDao();
        myContactListLive = myContactDao.getAllMyContactLive();
    }

    public void insert(HpUserModel... userModels) {
        new Thread(() -> myContactDao.insert(userModels)).start();
    }

    public void insert(TAPDatabaseListener<HpUserModel> listener, HpUserModel... userModels) {
        new Thread(() -> {
            myContactDao.insert(userModels);
            listener.onInsertFinished();
        }).start();
    }

    public void insert(List<HpUserModel> userModels) {
        new Thread(() -> myContactDao.insert(userModels)).start();
    }

    public void insertAndGetContact(List<HpUserModel> userModels, TAPDatabaseListener<HpUserModel> listener) {
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

    public void deleteAllContact() {
        new Thread(() -> myContactDao.deleteAllContact()).start();
    }

    public void update(HpUserModel userModel) {
        new Thread(() -> myContactDao.update(userModel)).start();
    }

    public void getAllMyContactList(TAPDatabaseListener<HpUserModel> listener) {
        new Thread(() -> {
            List<HpUserModel> myContactList = myContactDao.getAllMyContact();
            listener.onSelectFinished(myContactList);
        }).start();
    }

    public LiveData<List<HpUserModel>> getMyContactListLive() {
        return myContactListLive;
    }

    public void searchAllMyContacts(String keyword, TAPDatabaseListener<HpUserModel> listener) {
        new Thread(() -> {
            String queryKeyword = "%" + keyword + '%';
            List<HpUserModel> myContactList = myContactDao.searchAllMyContacts(queryKeyword);
            listener.onSelectFinished(myContactList);
        }).start();
    }

    public void checkUserInMyContacts(String userID, TAPDatabaseListener<HpUserModel> listener) {
        new Thread(() -> listener.onContactCheckFinished(myContactDao.checkUserInMyContacts(userID))).start();
    }
}
