package com.example.tiagotoscano.freetec;


import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class TimesGridAdapter extends ArrayAdapter<TimeTable> {


    public TimesGridAdapter(Context context, List<TimeTable> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Log.e("GetView","Entrou");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_day_grid, parent, false);
        //if(cursor.getPosition()==1)
        //  view.setSelected(true);
        //Log.e("GetView","Pegou View");
        ViewHolder viewHolder = new ViewHolder(view);



        view.setTag(viewHolder);
        //Log.e("GetView", "View Holder");
        TimeTable time = getItem(position);


        //viewHolder.dayView.setText(time.data);
        viewHolder.horaView.setText(time.horariodesc);
        Log.e("GridViewAdapter", time.horariodesc);

        return  view;
    }

    public static class ViewHolder {

        //public final TextView dayView;
        public final TextView horaView;

        public ViewHolder(View view) {
            //dayView = (TextView) view.findViewById(R.id.labDay);
            horaView = (TextView) view.findViewById(R.id.labHora);

        }
    }
}