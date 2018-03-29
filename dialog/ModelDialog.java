package cn.yunhu.dialog;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import cn.yunhu.R;
import cn.yunhu.utils.Common;

public class ModelDialog extends Dialog {


    public ModelDialog(Context context) {
        super(context);
    }


    public ModelDialog(Context context, int theme) {
        super(context, theme);
    }


    public static class Builder {

        private Context                         context;
        private String                          title;
        private Spanned                         message;
        private String                          confirmBtn;
        private String                          cancelBtnText;
        private View                            contentView;
        private DialogInterface.OnClickListener confirmClickListener;
        private DialogInterface.OnClickListener cancelClickListener;
        private LinearLayout                    model;
        private Timer                           timer;
        private Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    if (model.getHeight() != 0) {
                        int   tabQQHeight = model.getHeight();
                        int[] windows     = Common.getScreenSize(context);
                        int   maxHeight   = (int) (windows[1] / 1.5);
                        if (tabQQHeight > maxHeight) {
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) model.getLayoutParams();
                            layoutParams.height = maxHeight;
                            model.setLayoutParams(layoutParams);
                        }

                        timer.cancel();
                    }
                }
            }
        };


        public Builder(Context context) {
            this.context = context;
        }


        public Builder setMessage(Spanned message) {
            this.message = message;
            return this;
        }


        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }


        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }


        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }


        public Builder setConfirmBtn(int positiveButtonText, DialogInterface.OnClickListener listener) {
            this.confirmBtn = (String) context.getText(positiveButtonText);
            this.confirmClickListener = listener;
            return this;
        }


        public Builder setConfirmBtn(String positiveButtonText, DialogInterface.OnClickListener listener) {
            this.confirmBtn = positiveButtonText;
            this.confirmClickListener = listener;
            return this;
        }


        public Builder setCancelBtn(int negativeButtonText, DialogInterface.OnClickListener listener) {
            this.cancelBtnText = (String) context.getText(negativeButtonText);
            this.cancelClickListener = listener;
            return this;
        }


        public Builder setCancelBtn(String negativeButtonText, DialogInterface.OnClickListener listener) {
            this.cancelBtnText = negativeButtonText;
            this.cancelClickListener = listener;
            return this;
        }


        @SuppressLint("WrongViewCast")
        public ModelDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final ModelDialog dialog = new ModelDialog(context, R.style.Dialog);

            //点击屏幕不消失
            dialog.setCanceledOnTouchOutside(false);
            View layout = inflater.inflate(R.layout.model_dialog, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));


            // 容器
            model = (LinearLayout) layout.findViewById(R.id.model);

            // 设置标题
            ((TextView) layout.findViewById(R.id.titleBarText)).setText(title);

            // 设置确认按钮
            if (confirmBtn != null) {
                ((TextView) layout.findViewById(R.id.confirmBtn)).setText(confirmBtn);


                if (confirmClickListener != null) {
                    ((TextView) layout.findViewById(R.id.confirmBtn)).setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            confirmClickListener.onClick(
                                    dialog,
                                    DialogInterface.BUTTON_POSITIVE
                            );
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.confirmBtn).setVisibility(View.GONE);
            }


            // 设置取消按钮
            if (cancelBtnText != null) {
                layout.findViewById(R.id.cancelBtn).setVisibility(View.VISIBLE);
                ((TextView) layout.findViewById(R.id.cancelBtnText)).setText(cancelBtnText);

                if (cancelClickListener != null) {
                    ((TextView) layout.findViewById(R.id.cancelBtnText)).setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            cancelClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.cancelBtn).setVisibility(View.GONE);
            }


            // 设置消息
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
                ((TextView) layout.findViewById(R.id.message)).setMovementMethod(ScrollingMovementMethod.getInstance());
            } else if (contentView != null) {
                ((LinearLayout) layout.findViewById(R.id.message)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.message)).addView(contentView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            }

            dialog.setContentView(layout);



            // QQ高度
            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }, 10);


            return dialog;
        }
    }


    public static ModelDialog alert(Context context, String title, String message, final ModelDialogCallBack confirmCallback) {
        ModelDialog.Builder builder = new ModelDialog.Builder(context);
        builder.setMessage(Html.fromHtml(message));
        builder.setTitle(title);
        builder.setConfirmBtn("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (confirmCallback != null && !confirmCallback.onCallback(dialog, which)) {
                    return;
                }

                dialog.dismiss();

            }
        });

        ModelDialog dialog = builder.create();

        dialog.show();


        return dialog;
    }


    public static ModelDialog alert(Context context, String message, ModelDialogCallBack confirmCallback) {
        return alert(context, "提示", message, confirmCallback);
    }


    public static ModelDialog alert(Context context, String message) {
        return alert(context, "提示", message, null);
    }


    public static ModelDialog confirm(Context context, String title, String message, final ModelDialogCallBack confirmCallback, final ModelDialogCallBack cancelCallback, String confirmBtn, String cancelBtn) {
        confirmBtn = Common.string(confirmBtn);
        cancelBtn = Common.string(cancelBtn);

        ModelDialog.Builder builder = new ModelDialog.Builder(context);
        builder.setMessage(Html.fromHtml(message));
        builder.setTitle(title);
        builder.setConfirmBtn(confirmBtn.equals("") ? "确定" : confirmBtn, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (confirmCallback != null && !confirmCallback.onCallback(dialog, which)) {
                    return;
                }

                dialog.dismiss();
            }
        });

        builder.setCancelBtn(cancelBtn.equals("") ? "取消" : cancelBtn, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (cancelCallback != null && !cancelCallback.onCallback(dialog, which)) {
                    return;
                }
                dialog.dismiss();
            }
        });

        ModelDialog dialog = builder.create();

            dialog.show();

        return dialog;
    }


    public static ModelDialog confirm(Context context, String message, ModelDialogCallBack confirmCallback, ModelDialogCallBack cancelCallback) {
        return confirm(context, "确认提示", message, confirmCallback, cancelCallback, null, null);
    }


    public static ModelDialog confirm(Context context, String message, ModelDialogCallBack confirmCallback) {
        return confirm(context, "确认提示", message, confirmCallback, null, null, null);
    }


    abstract static public class ModelDialogCallBack {

        protected abstract boolean onCallback(DialogInterface dialog, int which);
    }
}

