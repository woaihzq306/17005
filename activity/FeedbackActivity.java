package cn.yunhu.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.yunhu.R;
import cn.yunhu.adapter.CommonAdapter;
import cn.yunhu.adapter.ViewHolder;
import cn.yunhu.api.AppInfoApi;
import cn.yunhu.api.FeedBackApi;
import cn.yunhu.api.LoginApi;
import cn.yunhu.api.UploadPictureApi;
import cn.yunhu.http.Rest;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.Constants;
import cn.yunhu.utils.ImageTool;
import cn.yunhu.utils.ToastUitil;
import me.shaohui.advancedluban.Luban;

/**
 * 反馈
 * todo 1 上传后点击删除无反应
 * todo 3 反馈列表待开发
 */
public class FeedbackActivity extends BaseActivity implements TakePhoto.TakeResultListener, InvokeListener {


    private static final String              IMG_ADD_TAG   = "add";
    private              List<String>        imagePathList = new ArrayList<>();
    private              Map<String, String> uploadTmpList = new HashMap<>();

    private GridView                  uploadImage;
    private CommonAdapter<String>     imageAdapter;
    private PopupWindow               mPopWindow;
    private EditText                  say;
    private EditText                  contact;
    private TextView                  account;
    private LinearLayout              verifyBox;
    private ImageView                 verifyImage;
    private AppInfoApi.FeedbackConfig feedbackConfig;
    private Button                    feedbackBtn;
    private TakePhoto                 takePhoto;
    private InvokeParam               invokeParam;
    private LinearLayout              main;
    private String                    imagePath;


    @Override
    protected int getLayoutContent() {
        return R.layout.activity_feedback;
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        setHeaderTitle("投诉/反馈");

        feedbackConfig = AppInfoApi.getFeedbackConfig();
        uploadImage = (GridView) findViewById(R.id.uploadImage);
        say = (EditText) findViewById(R.id.say);
        say.setHint(feedbackConfig.getContentPlaceholder());
        contact = (EditText) findViewById(R.id.contact);
        contact.setHint(feedbackConfig.getTelPlaceholder());
        account = (TextView) findViewById(R.id.account);
        account.setText("账号：" + LoginApi.getUsername(nowContext));
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.copy(nowContext, account.getText().toString().trim().substring(3), "账号复制成功");
            }
        });
        main = (LinearLayout) findViewById(R.id.main);
        imagePath = Common.getSDCardPath() + "images/";


        // 设置验证码
        verifyImage = (ImageView) findViewById(R.id.verifyImage);
        verifyBox = (LinearLayout) findViewById(R.id.verifyBox);
        Common.setVerifyImageUrl(verifyImage, Common.getRandUrl(feedbackConfig.getVerifyUrl()));
        verifyImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Common.setVerifyImageUrl(verifyImage, Common.getRandUrl(feedbackConfig.getVerifyUrl()));
            }
        });


        // 是否显示验证码
        if (feedbackConfig.getVerifyStatus()) {
            verifyBox.setVisibility(View.VISIBLE);
        } else {
            verifyBox.setVisibility(View.GONE);
        }


        // 提交反馈
        feedbackBtn = (Button) findViewById(R.id.feedbackBtn);
        feedbackBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FeedBackApi feedBackApi = new FeedBackApi(nowContext);
                feedBackApi.setContact(contact.getText().toString().trim());
                feedBackApi.setSay(say.getText().toString().trim());

                for (Map.Entry<String, String> entry : uploadTmpList.entrySet()) {
                    feedBackApi.addSayImage(entry.getValue());
                    Log.d("提交素材", entry.getValue());
                }

                feedBackApi.request(new Rest.RestCallback() {

                    @Override
                    public void onSuccess(String message, JSONObject data) {
                        ToastUitil.showShort(message);
                        finish();
                    }


                    @Override
                    public void onError(String message, String field, int code) {
                        ToastUitil.showShort(message);
                    }
                });
            }
        });


        imageAdapter = new CommonAdapter<String>(nowContext, imagePathList, R.layout.activity_upload_image_item) {

            @Override
            public void convert(ViewHolder holder, final String string) {
                if (!string.equals(IMG_ADD_TAG)) {
                    holder.setImageBitmap(R.id.image, ImageTool.createImageThumbnail(string));
                    holder.setVisible(R.id.delete, true);
                } else {
                    holder.setImageResource(R.id.image, R.mipmap.icon_upload_image);
                    holder.setVisible(R.id.delete, false);
                }

                holder.setOnClickListener(R.id.image, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (string.equals(IMG_ADD_TAG)) {
                            showPopupWindow();
                        } else {
                            removeImagePath(string);
                        }
                    }
                });
            }
        };

        uploadImage.setAdapter(imageAdapter);
        addImagePath("");
    }


    private void refreshAdapter() {
        if (imagePathList == null) {
            imagePathList = new ArrayList<>();
        }

        Log.d("list:", imagePathList.toString());
        imageAdapter.notifyDataSetChanged();
    }


    private void addImagePath(String path) {
        path = Common.string(path);

        // 移除ADD
        for (int i = 0; i < imagePathList.size(); i++) {
            if (imagePathList.get(i).equals(IMG_ADD_TAG)) {
                imagePathList.remove(i);
            }
        }

        if (!path.equals("")) {
            imagePathList.add(path);
        }

        imagePathList.add(IMG_ADD_TAG);
        refreshAdapter();
    }


    private void removeImagePath(String string) {
        for (int i = 0; i < imagePathList.size(); i++) {
            if (imagePathList.get(i).equals(string)) {
                imagePathList.remove(i);
            }
        }

        uploadTmpList.remove(string);
        Log.d("删除", uploadTmpList.toString());
        addImagePath("");
        refreshAdapter();
    }


    private void showPopupWindow() {
        View contentView = LayoutInflater.from(FeedbackActivity.this).inflate(R.layout.item_popupwindows, null);
        mPopWindow = new PopupWindow(contentView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        mPopWindow.showAtLocation(main, Gravity.BOTTOM, 0, 0);

        Button bt1 = (Button) contentView.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) contentView.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) contentView.findViewById(R.id.item_popupwindows_cancel);

        getTakePhoto().onEnableCompress(new CompressConfig.Builder().setMaxSize(512 * 1024).setMaxPixel(1000).create(), true);

        bt1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                File image = new File(imagePath + "camera_" + System.currentTimeMillis() + ".jpg");
                if (!image.getParentFile().exists()) {
                    image.getParentFile().mkdirs();
                }
                getTakePhoto().onPickFromCapture(Uri.fromFile(image));
                mPopWindow.dismiss();
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getTakePhoto().onPickFromGallery();
                mPopWindow.dismiss();
            }
        });


        bt3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }


    @Override
    public void takeSuccess(TResult result) {
        final String path = result.getImage().getCompressPath();
        Log.e("takePhone", "takeSuccess：" + path);
        if (path.equals("")) {
            ToastUitil.showShort("图片选择失败");
            return;
        }


        File file = new File(path);
        if (!file.exists()) {
            ToastUitil.showShort("图片选择失败");
            return;
        }


        addImagePath(path);
        UploadPictureApi uploadPictureApi = new UploadPictureApi(nowContext);
        uploadPictureApi.setPendingMessage("上传中...");
        uploadPictureApi.setImage(file);
        uploadPictureApi.request(new Rest.RestCallback() {

            @Override
            public void onSuccess(String message, JSONObject data) {
                ToastUitil.showShort("上传成功");
                uploadTmpList.put(path, data.optString("file_id"));
            }


            @Override
            public void onError(String message, String field, int code) {
                ToastUitil.showShort(message);
            }
        });
    }


    @Override
    public void takeFail(TResult result, String msg) {
        ToastUitil.showShort(msg);
    }


    @Override
    public void takeCancel() {
        ToastUitil.showShort(getResources().getString(R.string.msg_operation_canceled));
    }


    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }


    /**
     * 获取TakePhoto实例
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }
}
