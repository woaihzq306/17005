package cn.yunhu.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.yunhu.R;
import cn.yunhu.bean.FeedBack;

/**
 * Created by Administrator on 2017\10\25 0025.
 */

public class AnswerAdapter extends BaseAdapter {
    private Context context;
    private List<FeedBack> list;

    public AnswerAdapter(Context context, List<FeedBack> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /*书中详细解释该方法*/
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        //观察convertView随ListView滚动情况

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.answer_list_item,null);
            holder = new ViewHolder();
                     /*得到各个控件的对象*/

            holder.title = (TextView) convertView.findViewById(R.id.headerTitle);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.isAnswer = (TextView) convertView.findViewById(R.id.isAnswer);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
        }
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
        holder.title.setText(list.get(position).getSay());
        holder.time.setText(list.get(position).getSay_time());
        if (list.get(position).isIs_answer()){
            holder.isAnswer.setText("已回复");
        }else {
            holder.isAnswer.setText("未回复");
        }
        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        public TextView title;
        public TextView time;
        public TextView isAnswer;
    }


}


