package cn.yunhu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import cn.yunhu.IMyAidlInterface;
import cn.yunhu.activity.HomeActivity;
import cn.yunhu.fragment.HomeFragment;


/**
 * Created by Administrator on 2017\12\8 0008.
 */

public class AIDLService extends Service {

    private IBinder binder = new GetMyName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class GetMyName extends IMyAidlInterface.Stub{

        @Override
        public String getEncryptionString() throws RemoteException {
            return HomeFragment.getEncryption();
        }
    }


}
