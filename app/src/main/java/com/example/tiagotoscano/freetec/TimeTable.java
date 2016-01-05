package com.example.tiagotoscano.freetec;


import java.io.Serializable;
/**
 * Created by tiagotoscano on 24/12/15.
 */
public class TimeTable implements Serializable {
    public String tema;
    public String horariodesc;
    public String eixo;
    public String eixo_color;
    public String data;
    public String horario;
    public String dataHora;
    public String urlImg;
    public String matriculado;

    public int id_curso;
    public int id_eixo;
    public int id_horario;

    public TimeTable() {
    }

    public TimeTable( String tema,
             String horariodesc,
             String eixo,
             String eixo_color,
             String data,
             String horario,
             String dataHora,
             String urlImg,
             String matriculado,
             int id_curso,
             int id_eixo,
             int id_horario) {

        this.tema =tema;
        this.horariodesc =horariodesc;
        this.eixo =eixo;
        this.eixo_color= eixo_color;
        this.data =data;
        this.horario =horario;
        this.dataHora= dataHora;
        this.urlImg =urlImg;
        this.matriculado= matriculado;
        this.id_curso= id_curso;
        this.id_eixo =id_eixo;
        this. id_horario = id_horario;

    }

    @Override
    public String toString() {
        return this.tema;
    }


}