package c.plutarco.geolocalizacin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PantallaInicio extends AppCompatActivity {

    /**
     * Pagina principal donde el usuario decidira o unirse a un grupo cercano o crear un grupo para usuarios sercanos
     * @param savedInstanceState
     */
    Button crear;
    Button cliente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicio);

        crear= (Button) findViewById(R.id.botoncreargrupo);
        cliente= (Button) findViewById(R.id.botonbuscargrupo);

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(PantallaInicio.this,FormCrear.class);
                startActivity(i);
            }
        });
        cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(PantallaInicio.this,Buscar.class);
                startActivity(i);

            }
        });

    }
}
