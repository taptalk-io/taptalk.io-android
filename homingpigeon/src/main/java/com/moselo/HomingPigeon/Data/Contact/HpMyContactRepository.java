package com.moselo.HomingPigeon.Data.Contact;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.moselo.HomingPigeon.Data.HomingPigeonDatabase;
import com.moselo.HomingPigeon.Model.HpUserModel;

import java.util.List;

public class HpMyContactRepository {
    private HpMyContactDao myContactDao;

    public HpMyContactRepository(Application application) {
        HomingPigeonDatabase db = HomingPigeonDatabase.getDatabase(application);
        myContactDao = db.myContactDao();
    }

    public void insert(HpUserModel... userModels) {
        new Thread(() -> myContactDao.insert(userModels)).start();
    }

    public void insert(List<HpUserModel> userModels) {
        new Thread(() -> myContactDao.insert(userModels)).start();
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

    public void getAllMyContactList() {
        new Thread(() -> {
            List<HpUserModel> myContactList = myContactDao.getAllMyContact();
        });
    }
}
