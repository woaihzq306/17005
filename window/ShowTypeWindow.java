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
import cn.yunhu.api.AppInfoApi;

/**
 * 呼叫中心显号方式选项
 */
public class ShowTypeWindow extends PopupWindow {

    private Context                         context;
    private CallTypeWindow.SelectCallback   callback;
    private LayoutInflater                  inflater;
    private ListView                        listView;
    private List<AppInfoApi.CallConfigList> list;


    public ShowTypeWindow(Context context, CallTypeWindow.SelectCallback callback) {
        super(context);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.list = AppInfoApi.getCallConfig().getShowTypeList();
        this.callback = callback;
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
            public void convert(cn.yunhu.adapter.ViewHolder holder, AppInfoApi.CallConfigList callConfigList) {
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
}
