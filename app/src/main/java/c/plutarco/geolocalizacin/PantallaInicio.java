package c.plutarco.geolocalizacin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PantallaInicio extends AppCompatActivity {

    /**
     * Pagina principal donde el usuario decidira o unirse a un grupo cercano o crear un grupo para usuarios sercanos
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicio);
    }
}
