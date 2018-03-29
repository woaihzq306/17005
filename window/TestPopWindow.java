package cn.yunhu.window;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.yunhu.R;


public class TestPopWindow extends BaseWindow {

    private onPictureCallBack callBack;


    public void setOnPicture(onPictureCallBack callBack) {
        this.callBack = callBack;
    }


    @Override
    protected int getContentLayout() {
        return R.layout.item_picture_popupwindows;
    }


    @Override
    protected void initDialog() {
        TextView camera = (TextView) findId(R.id.item_popupwindows_camera);
        TextView Photo  = (TextView) findId(R.id.item_popupwindows_Photo);

        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                TestPopWindow.this.dismiss();
                callBack.onClickPhone();
            }
        });

        Photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TestPopWindow.this.dismiss();
                callBack.onClickRGPS();
            }
        });
    }


    public abstract static class onPictureCallBack {

        public abstract void onClickPhone();


        public abstract void onClickRGPS();
    }
}
