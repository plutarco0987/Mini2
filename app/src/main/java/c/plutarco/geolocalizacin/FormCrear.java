package c.plutarco.geolocalizacin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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

    double longitud=0;
    double latitud=0;

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


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }


       btnCrear.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               /*
               String grupo = nombreGrupo.getText().toString();
               String usuario = nombreUsuario.getText().toString();
               String rango = distancia.getText().toString();

               String id = datosBD.push().getKey();

               DatosBD datos = new DatosBD(grupo, usuario, rango);

               datosBD.child("Datos").setValue(datos);

               Toast.makeText(getApplicationContext(), "Chat registrado", Toast.LENGTH_SHORT).show();
               */

               String grupo = nombreGrupo.getText().toString();
               String usuario = nombreUsuario.getText().toString();

               String id = datosBD.push().getKey();

               DatosBD datos = new DatosBD(grupo, usuario, 50, latitud, longitud);

               datosBD.child("Datos").child(id).setValue(datos);

               //Toast.makeText(getApplicationContext(), "Chat registrado", Toast.LENGTH_SHORT).show();

               Intent btnCrear = new Intent(FormCrear.this, ChatGrupal.class);
               btnCrear.putExtra("NombreChat", grupo);
               btnCrear.putExtra("NombreUsuario", usuario);
               btnCrear.putExtra("Administrador", true);
               startActivity(btnCrear);
           }
       });
    }

    //on click
    //layout datos , distancia y roomname
    //gps- latitud y longitud


    /**
     * Latitud y longitud
     */
    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public class Localizacion implements LocationListener {
        FormCrear formCrear;
        public FormCrear getMainActivity() {
            return formCrear;
        }
        public void setMainActivity(FormCrear mainActivity) {
            this.formCrear = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            String Text = "Mi ubicacion actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();
            //mensaje1.setText(Text);
            longitud=loc.getLongitude();
            latitud=loc.getLatitude();
            //Toast.makeText(formCrear, Text, Toast.LENGTH_SHORT).show();
            this.formCrear.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado

        }
        @Override
        public void onProviderEnabled(String provider) {

        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }


}
