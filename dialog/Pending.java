package cn.yunhu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.yunhu.R;
import cn.yunhu.utils.Common;
import cn.yunhu.utils.Radius;


/**
 * 加载等待框
 * @version $Id: 2017/7/24 下午4:31 Pending.java $
 */
public class Pending extends Dialog {


    private TextView     messageText;
    private LinearLayout pendingDialog;


    public Pending(Context context) {
        super(context, R.style.BAUtil_Model_Pending);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bautil_modal_pending);

        // 圆角实现
        Radius radius = new Radius(Common.dpToPx(getContext(), 5), false, 0x99000000);
        pendingDialog = (LinearLayout) findViewById(R.id.BAUtil_Model_pending);
        Common.setBackgroundDrawable(pendingDialog, radius);

        // 消息文本
        messageText = (TextView) findViewById(R.id.BAUtil_Model_Pending_message);
    }


    /**
     * 设置加载提示内容
     */
    public void setMessage(String message) {
        if (message.equals("")) {
            messageText.setVisibility(View.GONE);
            pendingDialog.setMinimumWidth(90);
        } else {
            messageText.setVisibility(View.VISIBLE);
            messageText.setText(message);
        }
    }
}
