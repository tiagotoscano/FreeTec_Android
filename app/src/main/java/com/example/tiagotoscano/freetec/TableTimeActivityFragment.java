package com.example.tiagotoscano.freetec;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TableTimeActivityFragment extends Fragment {

    GetTimeTask mTask;
    inscricaoTask mTaskInscricao;
    List<TimeTable> mTimes;
    ListView mListView;
    List<TimeTable> mDays;
    GridView mGridView;
    TextView mTextMensagem;
    ProgressBar mProgressBar;
    ArrayAdapter<TimeTable> mAdapter;
    ArrayAdapter<TimeTable> mAdapterDays;
    FrameLayout frameAlert;
    TextView labTema;
    TextView labHorario;
    TextView labMsgRetorno;
    Button bntInscricao;
    private String cpfLogin;
    private String confirmUpdHorario;


    TimeTable timeSelected;

    public TableTimeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cpfLogin = Utility.getCpfSettings(getContext());
        confirmUpdHorario = "false";
        //return inflater.inflate(R.layout.fragment_table_time, container, false);
        View layout = inflater.inflate(R.layout.fragment_table_time, container, false);

        frameAlert = (FrameLayout) layout.findViewById(R.id.framAlert);
        labTema = (TextView) layout.findViewById(R.id.labCursodesc);
        labHorario = (TextView) layout.findViewById(R.id.labHorariodesc);
        labMsgRetorno = (TextView) layout.findViewById(R.id.labMsgRetorno);

        mListView = (ListView) layout.findViewById(R.id.listView);
        mListView.setEmptyView(mTextMensagem);

        mGridView = (GridView) layout.findViewById(R.id.gridView);
        mGridView.setEmptyView(mTextMensagem);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {

                TimeTable timeselec = (TimeTable) adapterView.getItemAtPosition(position);

                mAdapter.getFilter().filter(timeselec.horariodesc);
                //mAdapter.notifyDataSetChanged();

            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {

                if(cpfLogin!="") {
                    TimeTable timeselec = (TimeTable) adapterView.getItemAtPosition(position);
                    if (timeselec.matriculado.equalsIgnoreCase("false")) {
                        timeSelected = timeselec;
                        showAlertFrame();
                    }
                }
                //mAdapter.getFilter().filter(timeselec.horariodesc);
                //mAdapter.notifyDataSetChanged();

            }
        });



        Button button = (Button) layout.findViewById(R.id.bntClose);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                hiddenAlertFrame();

            }
        });

        bntInscricao = (Button) layout.findViewById(R.id.bntInscricao);
        bntInscricao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                clickInscricao();

            }
        });


        return  layout;
    }

    public void showAlertFrame(){
        mListView.setEnabled(false);
        mGridView.setEnabled(false);
        frameAlert.setVisibility(View.VISIBLE);
        labTema.setText(timeSelected.tema);
        labHorario.setText(timeSelected.horariodesc);
        bntInscricao.setEnabled(true);

    }

    public void clickInscricao(){

        bntInscricao.setEnabled(false);
        if (mTaskInscricao == null ||  mTaskInscricao.getStatus() != AsyncTask.Status.RUNNING) {
            mTaskInscricao = new inscricaoTask(this.timeSelected);
            mTaskInscricao.execute();
        }

    }





    public void retornoInscricao(String retornoStr){
        labMsgRetorno.setVisibility(View.VISIBLE);
        try {
            JSONArray retornoJson = new JSONArray(retornoStr);
            JSONObject jsonObject = retornoJson.getJSONObject(0);
            if(jsonObject.getInt("status")==000){

                labMsgRetorno.setText(jsonObject.getString("Msg"));
                iniciarDownload();

            }else if(jsonObject.getInt("status")==102){

                labMsgRetorno.setText(jsonObject.getString("Msg"));


            }else if(jsonObject.getInt("status")==103){

                labMsgRetorno.setText(jsonObject.getString("Msg"));
                bntInscricao.setText("Deseja mudar? Click para mudar de curso");
                confirmUpdHorario = "true";
                bntInscricao.setEnabled(true);
            }else{

                labMsgRetorno.setText(jsonObject.getString("Msg"));

            };


        }catch(Exception e) {


            labMsgRetorno.setText("Erro, favor tentar novamente");

        }

    }
    public  void hiddenAlertFrame(){
        labMsgRetorno.setVisibility(View.INVISIBLE);
        mListView.setEnabled(true);
        mGridView.setEnabled(true);

        frameAlert.setVisibility(View.INVISIBLE);
        bntInscricao.setEnabled(true);
        confirmUpdHorario = "false";
        bntInscricao.setText("Inscrever-se");

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mTimes == null) {
            mTimes = new ArrayList<TimeTable>();
        }
        if (mDays == null) {
            mDays = new ArrayList<TimeTable>();
        }



        mAdapter = new TimesListAdapter(getActivity(), mTimes);
        mAdapterDays = new TimesGridAdapter(getActivity(), mDays);
        mListView.setAdapter(mAdapter);
        mGridView.setAdapter(mAdapterDays);
        if (mTask == null) {
            if (Utility.temConexao(getActivity())) {
                iniciarDownload();
            } else {
              //  mTextMensagem.setText("Sem conexão");
            }
        } else if (mTask.getStatus() == AsyncTask.Status.RUNNING) {
           // exibirProgress(true);
        }


    }

    public void iniciarDownload() {
        if (mTask == null ||  mTask.getStatus() != AsyncTask.Status.RUNNING) {
            mTask = new GetTimeTask();
            mTask.execute();
        }
    }


    class GetTimeTask extends AsyncTask<Void, Void, List> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //exibirProgress(true);
        }

        @Override
        protected List <TimeTable> doInBackground(Void... params) {


            return Utility.syncTime(cpfLogin);

        }

        @Override
        protected void onPostExecute(List times) {
            super.onPostExecute(times);
            //exibirProgress(false);
            if (times != null) {
                mTimes.clear();
                mTimes.addAll(times);
                mDays.clear();
                for (Object time: times
                     ) {

                    TimeTable inTime = (TimeTable)time;
                    TimeTable outTime = new TimeTable();

                    outTime.horario = inTime.horario;
                    outTime.data = inTime.data;
                    outTime.horariodesc = inTime.horariodesc;
                    boolean existe = false;
                    for(TimeTable contTime:mDays){
                        //Log.e("Dentro do each1",contTime.horariodesc);
                        //Log.e("Dentro do each2",inTime.horariodesc);

                        if(contTime.horariodesc.equalsIgnoreCase(inTime.horariodesc))
                        {   existe =true; break;}

                    }
                    if(!existe) {
                        Log.e("Dentro do if",outTime.horariodesc);
                        mDays.add(outTime);
                    }
                }
                for(TimeTable contTime:mDays){
                    Log.e("Dentro do each1",contTime.horariodesc);
                    //Log.e("Dentro do each2",inTime.horariodesc);


                }
                TimeTable outTime = new TimeTable();
                outTime.horariodesc="Todos os dias";
                mDays.add(outTime);
                mAdapterDays.notifyDataSetChanged();
                mAdapter.notifyDataSetChanged();
            } else {
                mTextMensagem.setText("Falha ao obter livros");
            }
        }
    }

    private class inscricaoTask extends AsyncTask<Void, Void, String> {


        private String strCpf;
        private TimeTable timeSelect;
        private String retornoFinal;

        public inscricaoTask(TimeTable timeSelect) {

            this.strCpf = cpfLogin;
            this.timeSelect = timeSelect;


        }

        protected String doInBackground(Void... params) {

            Log.e("doinBackGround", "entrou");
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String retornoStr;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String BASE_URL =
                        "http://www.unibratec.edu.br/freetec2016/cadastroFreetec_mobile.php";


                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q="+city[0]+"&mode=json&units=metric&cnt=7");

                //Log.d("URL", city[0]);


                Uri uri = Uri.parse(BASE_URL)
                        .buildUpon()
                                //.appendQueryParameter("ID_CPF", strCpf)
                                //.appendQueryParameter("ID_CULT", Utility.BASE_URL)

                        .build();

                URL url = new URL(uri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                Log.d("URL", uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                List<Pair> parames = new ArrayList<Pair>();

                parames.add(new Pair("cpf",strCpf));
                parames.add(new Pair("horario_codigo",String.valueOf(timeSelect.id_horario)));
                parames.add(new Pair("confirmUpdHorario",confirmUpdHorario));
                parames.add(new Pair("curso_codigo",String.valueOf(timeSelect.id_curso)));

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(Utility.getQuery(parames));

                writer.flush();
                writer.close();
                os.close();


                urlConnection.connect();
                Log.d("URL", uri.toString());
                Log.d("doinBackGround", "Connect");

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                Log.d("URL", inputStream.toString());

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    retornoStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    retornoStr = null;


                }


                retornoStr = buffer.toString();


                Log.d("Retorno", retornoStr);


                //retornoStr = retornoJson.getString("retorno");

            } catch (Exception e) {

                Log.e("Retorno Url", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                retornoStr = "[{status:\"101\",Msg:\"Erro Conexão\"}]";

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }


            Log.d("Retorno:", "." + retornoStr + ".");
            retornoFinal = retornoStr;

            return retornoStr;

        }

        @Override
        protected void onPostExecute(String retorno) {

            super.onPostExecute(retorno);

            Log.e("Retorno PostExucete",retorno);
            retornoInscricao(retorno);


        }

    }
}
