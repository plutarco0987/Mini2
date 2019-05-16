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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.database.DatabaseError;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ValueEventListener;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Buscar extends AppCompatActivity {

    private List<DatosBD> lista= new ArrayList<DatosBD>();
    private List<DatosBD> listaEnRango = new ArrayList<DatosBD>();
    ArrayAdapter<DatosBD> adapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    EditText nombre;
    ListView listadatos;
    Button actualizar;
    double latitudActual;
    double longitudActual;
    TextView numCapsulas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        actualizar = (Button) findViewById(R.id.btn_refrescar);


        nombre= findViewById(R.id.nombre);
        listadatos= findViewById(R.id.lista);
        numCapsulas=(TextView)findViewById(R.id.txt_capsulas);

        iniciarfirebase();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Me diste click ", Toast.LENGTH_SHORT).show();
                listadatos();
            }
        });

        listadatos();
    }

    public void listadatos(){
        databaseReference.child("Datos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista.clear();
                listaEnRango.clear();

                int iter=0;
                for(DataSnapshot objSnapShot: dataSnapshot.getChildren()){
                    final DatosBD datos = objSnapShot.getValue(DatosBD.class);
                    lista.add(datos);

                    Location locationA = new Location("punto A");

                    locationA.setLatitude(datos.getLatitud());
                    locationA.setLongitude(datos.getLongitud());

                    Location locationB = new Location("punto B");

                    locationB.setLatitude(latitudActual);
                    locationB.setLongitude(longitudActual);

                    float distance = locationA.distanceTo(locationB);
                    iter++;
                    Toast.makeText(getApplicationContext(), "Distancia: "+ distance +" Count: "+iter,Toast.LENGTH_SHORT).show();

                    if(distance<=100){
                        listaEnRango.add(datos);
                    }
                    numCapsulas.setText(String.valueOf("Capsulas disponibles: "+listaEnRango.size()+"/"+lista.size()));


                }
                adapter= new ArrayAdapter<DatosBD>(Buscar.this,android.R.layout.simple_list_item_1,listaEnRango);
                listadatos.setAdapter(adapter);
                listadatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(getApplicationContext(),"Me diste click! Pos: "+view.toString(), Toast.LENGTH_SHORT).show();
                        if(nombre.length()<0){
                            Toast.makeText(Buscar.this, "Debes incluir un nombre de usuario", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //List<DatosBD>
                            Toast.makeText(Buscar.this, String.valueOf(view.), Toast.LENGTH_SHORT).show();
                            //Intent btnCrear = new Intent(Buscar.this, ChatGrupal.class);
                            //btnCrear.putExtra("NombreChat", grupo);
                            //btnCrear.putExtra("NombreUsuario", nombre.toString());
                            //btnCrear.putExtra("Administrador", true);
                            //startActivity(btnCrear);
                            //finish();
                        }

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void iniciarfirebase(){
        FirebaseApp.initializeApp(this);
         firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference();
    }

    /*
    * Metodos para obtener la geolocalización y se almacenan en los valores al principio
    * */
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
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);;
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
                    //mensaje2.setText("Mi direccion es: \n"
                     //       + DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public class Localizacion implements LocationListener {
        Buscar mainActivity;
        public Buscar getMainActivity() {
            return mainActivity;
        }
        public void setMainActivity(Buscar mainActivity) {
            this.mainActivity = mainActivity;
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
            longitudActual=loc.getLongitude();
            latitudActual=loc.getLatitude();
            this.mainActivity.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            //mensaje1.setText("GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            //mensaje1.setText("GPS Activado");
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

    public void validarRango(List<DatosBD> listaDeCam){

        for (int i =0; i < lista.size(); i ++){

        }

        /*
        Location locationA = new Location("punto A");

        locationA.setLatitude(latA);
        locationA.setLongitude(lngA);

        Location locationB = new Location("punto B");

        locationB.setLatitude(latB);
        locationB.setLongitude(lngB);

        float distance = locationA.distanceTo(locationB);
         */


    }


}


