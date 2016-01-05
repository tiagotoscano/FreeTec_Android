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
import android.widget.Filter;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TimesListAdapter extends ArrayAdapter<TimeTable> {

    private List<TimeTable> itens_exibicao;
    private List<TimeTable> itens;


    public TimesListAdapter(Context context, List<TimeTable> objects) {
        super(context, 0, objects);
        this.itens = objects;
        this.itens_exibicao = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.e("GetView","Entrou");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_table_time_list, parent, false);
        //if(cursor.getPosition()==1)
        //  view.setSelected(true);
        //Log.e("GetView","Pegou View");
        ViewHolder viewHolder = new ViewHolder(view);



        view.setTag(viewHolder);
        //Log.e("GetView", "View Holder");
        TimeTable time = getItem(position);

        if(time.matriculado.equalsIgnoreCase("true"))
            view.setBackgroundColor(
                    Color.parseColor("#CCCCCC")
            );

        viewHolder.temaView.setTextColor(Color.parseColor(time.eixo_color));
        viewHolder.temaView.setText(time.tema);
        viewHolder.horarioView.setText(time.horariodesc);
        //Log.e("GetView", "Valores ");
        String UrlImg = "http://www.unibratec.edu.br/freetec2016/getImg.php?ID_CURSO="+time.id_curso;
        Log.e("ListViewAdapter", UrlImg);
        Picasso.with(getContext()).load(UrlImg).into(viewHolder.imgCapa);

        return  view;
    }


    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence filtro) {
                FilterResults results = new FilterResults();
                //se não foi realizado nenhum filtro insere todos os itens.
                if (filtro == null || filtro.length() == 0) {
                    results.count = itens.size();
                    results.values = itens;
                } else {
                    //cria um array para armazenar os objetos filtrados.
                    List<TimeTable> itens_filtrados = new ArrayList<TimeTable>();

                    //percorre toda lista verificando se contem a palavra do filtro na descricao do objeto.
                    for (int i = 0; i < itens.size(); i++) {
                        TimeTable data = itens.get(i);

                        Log.e("Filter", data.horariodesc);
                        if(filtro=="Todos os dias")
                            filtro="";
                        filtro = filtro.toString().toLowerCase();
                        String condicao = data.horariodesc.toLowerCase();

                        if (condicao.contains(filtro)) {
                            //se conter adiciona na lista de itens filtrados.
                            itens_filtrados.add(data);
                        }
                    }
                    // Define o resultado do filtro na variavel FilterResults
                    results.count = itens_filtrados.size();
                    results.values = itens_filtrados;
                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                itens_exibicao = (List<TimeTable>) results.values; // Valores filtrados.
                notifyDataSetChanged();  // Notifica a lista de alteração
            }

        };
        return filter;
    }
    @Override
    public int getCount() {
        return itens_exibicao.size();
    }

    @Override
    public TimeTable getItem(int arg0) {
        return itens_exibicao.get(arg0);
    }

    public static class ViewHolder {

        public final TextView temaView;
        public final TextView horarioView;
        public final ImageView imgCapa;


        public ViewHolder(View view) {
            temaView = (TextView) view.findViewById(R.id.labTema);
            horarioView = (TextView) view.findViewById(R.id.labHorariodesc);
            imgCapa = (ImageView) view.findViewById(R.id.imgCapa);

        }
    }
}