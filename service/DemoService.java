package cn.yunhu.service;

import android.content.Intent;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;

import com.allenliu.versionchecklib.core.AVersionService;

import cn.yunhu.R;
import cn.yunhu.api.AppInfoApi;

public class DemoService extends AVersionService {
    public DemoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onResponses(AVersionService service, String response) {
        AppInfoApi.VersionConfig version    = AppInfoApi.getVersionConfig();

        String string = Html.fromHtml(version.getRemark())+"";
        service.showVersionDialog(version.getUrl(), "升级提示",string);

    }
}
