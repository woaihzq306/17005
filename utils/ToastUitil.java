package cn.yunhu.utils;

import android.content.Context;
import android.widget.Toast;


/**
 * Toast统一管理 *
 * @author way
 */
public class ToastUitil {

    // Toast
    private static Toast toast;

    private static Context mContext;


    public static void init(Context context) {
        mContext = context;
    }


    /**
     * 短时间显示Toast
     * @param message
     */
    public static void showShort(CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.CENTER, 0, 0);
            // TextView t = new TextView(context);
            // toast.setView(t);
        } else {
            toast.setText(message);
        }
        toast.show();
    }


    /**
     * 短时间显示Toast
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.CENTER, 0, 0);
            // TextView t = new TextView(context);
            // toast.setView(t);
        } else {
            toast.setText(message);
        }
        toast.show();
    }


    /**
     * 短时间显示Toast
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message) {
        if (null == toast) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }


    /**
     * 长时间显示Toast
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        if (null == toast) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }


    /**
     * 长时间显示Toast
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message) {
        if (null == toast) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }


    /**
     * 自定义显示Toast时间
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration) {
        if (null == toast) {
            toast = Toast.makeText(context, message, duration);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }


    /**
     * 自定义显示Toast时间
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration) {
        if (null == toast) {
            toast = Toast.makeText(context, message, duration);
            // toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(message);
        }
        toast.show();
    }


    /** Hide the toast, if any. */
    public static void hideToast() {
        if (null != toast) {
            toast.cancel();
        }
    }


    //    /**
    //     * 短暂显示Toast消息
    //     * @param context
    //     * @param message
    //     */
    //    public static void showShortToast(Context context, String message) {
    //        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //        View     view = inflater.inflate(R.layout.custom_toast, null);
    //        TextView text = (TextView) view.findViewById(R.id.toast_message);
    //        text.setText(message);
    //        Toast toast = new Toast(context);
    //        toast.setDuration(Toast.LENGTH_SHORT);
    //        toast.setGravity(Gravity.BOTTOM, 0, 300);
    //        toast.setView(view);
    //        toast.show();
    //    }

}
