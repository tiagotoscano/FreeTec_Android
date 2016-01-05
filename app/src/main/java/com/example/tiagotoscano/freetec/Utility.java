package com.example.tiagotoscano.freetec;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tiagotoscano on 24/12/15.
 */
public class Utility {

    private static final String LOG_TAG = "LOG CONEXAO";

    public static String getCpfSettings(Context ctx) {


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String cpf = prefs.getString(ctx.getString(R.string.keyCpf)
                , "");

        return cpf;


    }

    public static String setCpfSettings(Context ctx, String cpfStr) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(ctx.getString(R.string.keyCpf), cpfStr);
        editor.commit();


        String cpf = prefs.getString(ctx.getString(R.string.keyCpf)
                , "");


        return cpf;


    }

    public static List<TimeTable> syncTime(String cpfGet){


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String retornoPortalStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String BASE_URL =
                    "http://www.unibratec.edu.br/freetec2016/agendaFreetec_ios.php?";
            final String QUERY_PARAM = "CPF_INSCRITO";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, cpfGet)

                    .build();
            Log.e("Url", BASE_URL);
            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            Log.e("Url", "antes conexao");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Log.e("Url", "conexao");

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
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
                return null;
            }
            retornoPortalStr = buffer.toString();
            Log.e("Retorno Url", retornoPortalStr);
            return  loadBasedataFromJson(retornoPortalStr);
           // loadBasedataFromJson(retornoPortalStr, cpfRest);


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.

        } catch (Exception e) {
            Log.e("Exception", e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        //sendBroadcast(new Intent("DOWNLOADEND"));
        return null;


    }

    public static List<TimeTable> loadBasedataFromJson(String jsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.


        try {

            List<TimeTable> timeList = new ArrayList<TimeTable>();


            Log.e("Json tema","Inicio");
            JSONArray arrayTime = new JSONArray(jsonStr);
            //portalJson = portalJson.getJSONArray("");
            Log.e("Json tema","Primeira Leitura");

//            JSONArray arrayTime = portalJson.getJSONArray("");
            Log.e("Json tema","Segunda Leitura");


            for (int i = 0; i < arrayTime.length(); i++) {

                JSONObject Json = arrayTime.getJSONObject(i);
                Log.e("Json tema",Json.getString("tema"));
                TimeTable time = new TimeTable(Json.getString("tema"),
                        Json.getString("horariodesc"),
                        Json.getString("eixo"),
                        Json.getString("eixo_color"),
                        Json.getString("data"),
                        Json.getString("horario"),
                        Json.getString("dataHora"),
                        Json.getString("urlImg"),
                        Json.getString("matriculado"),
                        Json.getInt("id_curso"),
                        Json.getInt("id_eixo"),
                        Json.getInt("id_horario")
                        );
                timeList.add(time);
            }

            return timeList;



        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        ;
        return  null;

    }

    public static boolean temConexao(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static boolean validateCPF(String CPF) {
        CPF = Mask.unmask(CPF);
        if (CPF.equals("00000000000") || CPF.equals("11111111111")
                || CPF.equals("22222222222") || CPF.equals("33333333333")
                || CPF.equals("44444444444") || CPF.equals("55555555555")
                || CPF.equals("66666666666") || CPF.equals("77777777777")
                || CPF.equals("88888888888") || CPF.equals("99999999999")) {
            return false;
        }
        char dig10, dig11;
        int sm, i, r, num, peso;
        try {
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }
            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else
                dig10 = (char) (r + 48);
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (CPF.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }
            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else
                dig11 = (char) (r + 48);
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
                return (true);
            else
                return (false);
        } catch (Exception erro) {
            return (false);
        }
    }

    public static String getQuery(List<Pair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode((String) pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) pair.second, "UTF-8"));
        }

        return result.toString();
    }

}
