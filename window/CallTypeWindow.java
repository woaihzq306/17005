package cn.yunhu.window;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.List;

import cn.yunhu.R;
import cn.yunhu.adapter.CommonAdapter;
import cn.yunhu.adapter.ViewHolder;
import cn.yunhu.api.AppInfoApi;

/**
 * 呼叫中心挂断方式选项
 */
public class CallTypeWindow extends PopupWindow {

    private SelectCallback                  callback;
    private LayoutInflater                  inflater;
    private ListView                        listView;
    private List<AppInfoApi.CallConfigList> list;
    private Context                         context;


    public CallTypeWindow(Context context, SelectCallback callback) {
        super(context);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.callback = callback;
        list = AppInfoApi.getCallConfig().getCallTypeList();
        init();
    }


    private void init() {
        View view = inflater.inflate(R.layout.popup_window_select, null);
        setContentView(view);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);


        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(new CommonAdapter<AppInfoApi.CallConfigList>(context, list, R.layout.popup_window_select_item) {

            @Override
            public void convert(ViewHolder holder, AppInfoApi.CallConfigList callConfigList) {
                holder.setText(R.id.name, callConfigList.getName());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callback.onSelected(list.get(position).getName(), list.get(position).getId());
                dismiss();
            }
        });
    }


    public String getFirstId() {
        return list.get(0).getId();
    }


    abstract public static class SelectCallback {

        public abstract void onSelected(String name, String id);
    }

}
