package io.taptalk.TapTalk.Data.Contact;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

import io.taptalk.TapTalk.Data.TapTalkDatabase;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPMyContactRepository {
    private TAPMyContactDao myContactDao;
    private LiveData<List<TAPUserModel>> myContactListLive;

    public TAPMyContactRepository(Application application) {
        TapTalkDatabase db = TapTalkDatabase.getDatabase(application);
        myContactDao = db.myContactDao();
        myContactListLive = myContactDao.getAllMyContactLive();
    }

    public void insert(TAPUserModel... userModels) {
        new Thread(() -> myContactDao.insert(userModels)).start();
    }

    public void insert(TAPDatabaseListener<TAPUserModel> listener, TAPUserModel... userModels) {
        new Thread(() -> {
            myContactDao.insert(userModels);
            listener.onInsertFinished();
        }).start();
    }

    public void insert(List<TAPUserModel> userModels) {
        new Thread(() -> myContactDao.insert(userModels)).start();
    }

    public void checkContactAndInsert(TAPUserModel userModel) {
        new Thread(() -> {
            userModel.setIsContact(myContactDao.checkUserInMyContacts(userModel.getUserID()));
            myContactDao.insert(userModel);
        }).start();
    }

    public void getUserWithXcUserID(String xcUserID, TAPDatabaseListener<TAPUserModel> listener) {
        new Thread(() -> {
            TAPUserModel userModel = myContactDao.checkUserWithXcUserID(xcUserID);
            listener.onSelectFinished(userModel);
        }).start();
    }

    public void insertAndGetContact(List<TAPUserModel> userModels, TAPDatabaseListener<TAPUserModel> listener) {
        new Thread(() ->{
            myContactDao.insert(userModels);
            List<TAPUserModel> myContactList = myContactDao.getAllMyContact();
            listener.onSelectFinished(myContactList);
        }).start();
    }

    public void delete(TAPUserModel... userModels) {
        new Thread(() -> myContactDao.delete(userModels)).start();
    }

    public void delete(List<TAPUserModel> userModels) {
        new Thread(() -> myContactDao.delete(userModels)).start();
    }

    public void deleteAllContact() {
        new Thread(() -> myContactDao.deleteAllContact()).start();
    }

    public void update(TAPUserModel userModel) {
        new Thread(() -> myContactDao.update(userModel)).start();
    }

    public void getAllMyContactList(TAPDatabaseListener<TAPUserModel> listener) {
        new Thread(() -> {
            List<TAPUserModel> myContactList = myContactDao.getAllMyContact();
            listener.onSelectFinished(myContactList);
        }).start();
    }

    public LiveData<List<TAPUserModel>> getMyContactListLive() {
        return myContactListLive;
    }

    public void searchAllMyContacts(String keyword, TAPDatabaseListener<TAPUserModel> listener) {
        new Thread(() -> {
            String queryKeyword = '%' + keyword
                    .replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_") + '%';
            List<TAPUserModel> myContactList = myContactDao.searchAllMyContacts(queryKeyword);
            listener.onSelectFinished(myContactList);
        }).start();
    }

    public void checkUserInMyContacts(String userID, TAPDatabaseListener<TAPUserModel> listener) {
        new Thread(() -> listener.onContactCheckFinished(myContactDao.checkUserInMyContacts(userID))).start();
    }

    public void getAllUserData(TAPDatabaseListener<TAPUserModel> listener) {
        new Thread(() -> listener.onSelectFinished(myContactDao.getAllUserData())).start();
    }
}
