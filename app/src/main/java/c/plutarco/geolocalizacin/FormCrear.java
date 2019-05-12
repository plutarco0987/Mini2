package c.plutarco.geolocalizacin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    Button btnCrear;
    EditText nombreGrupo, nombreUsuario, distancia;

    private DatabaseReference datosBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_crear);

        datosBD = FirebaseDatabase.getInstance().getReference();

        nombreGrupo = (EditText)findViewById(R.id.et_nombreG);
        nombreUsuario = (EditText)findViewById(R.id.et_nombreU);
        distancia = (EditText)findViewById(R.id.et_dist);
        btnCrear = (Button)findViewById(R.id.btn_servidor);

       btnCrear.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String grupo = nombreGrupo.getText().toString();
               String usuario = nombreUsuario.getText().toString();
               String rango = distancia.getText().toString();

               String id = datosBD.push().getKey();

               DatosBD datos = new DatosBD(grupo, usuario, rango);

               datosBD.child("Datos").setValue(datos);

               Toast.makeText(getApplicationContext(), "Chat registrado", Toast.LENGTH_SHORT).show();
           }
       });
    }


    //on click
    //layout datos , distancia y roomname
    //gps- latitud y longitud
}
