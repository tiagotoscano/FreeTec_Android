package com.example.tiagotoscano.freetec;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
public class MainActivityFragment extends Fragment {

    private TextWatcher cpfMask;
    private TextWatcher telefoneMask;
    private EditText cpf;
    private EditText email;
    private EditText nome;
    private EditText telefone;
    private Button bntCadastro;

    private registroTask mRegistroTask;

    private String strCpf;
    private String strNome;
    private String strEmail;
    private String strTelefone;

    public MainActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main, container, false);

        bntCadastro = (Button) layout.findViewById(R.id.bntCadastro);

        cpf = (EditText) layout.findViewById(R.id.cpfLogin);;
        telefone = (EditText) layout.findViewById(R.id.telefone);;
        nome = (EditText) layout.findViewById(R.id.txtNome);;
        email = (EditText) layout.findViewById(R.id.txtEmail);;


        cpfMask = Mask.insert("###.###.###-##", cpf);
        telefoneMask = Mask.insert("(##)#####-####", telefone);

        cpf.addTextChangedListener(cpfMask);
        telefone.addTextChangedListener(telefoneMask);


        TextView irAgenda = (TextView) layout.findViewById(R.id.bntAbrirCalendario  );
        irAgenda.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                openAgenda();

            }
        });

        bntCadastro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                clickRegistro();

            }
        });

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public void clickRegistro(){

        bntCadastro.setEnabled(false);

        strCpf = cpf.getText().toString();
        strEmail = email.getText().toString();
        strNome = nome.getText().toString();
        strTelefone = telefone.getText().toString();
        if ((strNome.equals(""))||(strCpf.equals(""))||(strEmail.equals(""))||(strTelefone.equals(""))){

            Toast.makeText(getContext(), "Favor Preenxer todos os campos!", Toast.LENGTH_LONG).show();
            bntCadastro.setEnabled(true);
        }else{
            if (Utility.validateCPF(cpf.getText().toString())) {
                if (mRegistroTask == null || mRegistroTask.getStatus() != AsyncTask.Status.RUNNING) {
                    mRegistroTask = new registroTask();
                    mRegistroTask.execute();
                }
            }else{

                Toast.makeText(getContext(), "CPF Invalido!!", Toast.LENGTH_LONG).show();

            }
        }

    }


    private void openAgenda(){

        Intent list = new Intent(getContext(),TableTimeActivity.class);

        startActivity(list);
    }
    public void retornoInscricao(String retornoStr){

        try {
            JSONArray retornoJson = new JSONArray(retornoStr);
            JSONObject jsonObject = retornoJson.getJSONObject(0);
            if(jsonObject.getInt("status")==000){

                Utility.setCpfSettings(getContext(), cpf.getText().toString());

                Intent list = new Intent(getContext(),TableTimeActivity.class);

                startActivity(list);


            }else{

                Toast.makeText(getContext(), jsonObject.getString("Msg"), Toast.LENGTH_LONG).show();
                bntCadastro.setEnabled(true);
            };


        }catch(Exception e) {


            Toast.makeText(getContext(), "Um erro ocorreu tente novamente!", Toast.LENGTH_LONG).show();
            bntCadastro.setEnabled(true);

        }

    }


    private class registroTask extends AsyncTask<Void, Void, String> {


        //private String strCpf;
        //private TimeTable timeSelect;
        private String retornoFinal;

        public registroTask() {

            //this.strCpf = cpfLogin;
            //this.timeSelect = timeSelect;


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
                        "http://www.unibratec.edu.br/freetec2016/cadastroPessoaFreetec_mobile.php";


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
                parames.add(new Pair("nome",strNome));
                parames.add(new Pair("email",strEmail));
                parames.add(new Pair("telefone",strTelefone));

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
