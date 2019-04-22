package c.plutarco.geolocalizacin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FormCrear extends AppCompatActivity {

    /**
     * MUCHO OJO !!!!!! MUY IMPORTANTE!!!!
     *
     * NOTA: la latitud y longitud deben estar cambiando en la base de datos cada 5 minutos aprxo, (puede variar)
     *
     * Esta clase es donde una persona crea el Grupo de chat, ese grupo de chat es el que se estara usando en chatgrupal al ingresar
     * Cuando una persona quiere crear un chat tambien se debe guardar en Firebase (NombreGrupo,Latitud,longitud)
     * el nomrbe de usuario y su color del circulo deben estar, de esta a grupo chat sele deve enviar su nombre y un color randum
     * esto para el metodo OnMesage !!!!!!!
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_crear);
    }


    //on click
    //layout datos , distancia y roomname
    //gps- latitud y longitud
}
