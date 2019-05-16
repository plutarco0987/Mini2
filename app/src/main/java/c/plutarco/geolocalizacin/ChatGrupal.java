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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Message;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ChatGrupal extends AppCompatActivity  implements RoomListener {

    /**
    * Datos quE NOS LLEGAN
    **/
    //String Nombrechat=getIntent().getStringExtra("NombreChat");


    /**
     * EL channelID no lo deben cambiar esta ligado a mi cuenta y es unico, con el podemos munitorear el control de
     * mensajeria.
     */
    private String channelID = "o5jPqEXLi04iOkp7";
    /**
     * private String channelID = "BvIqQwlC4R5UN4c9";
     * RoomName.- este valor debe ser dado por el usuario y almasenado en la base de datos, en el activiti anterior
     * debe seleccionar el grupo de chat al que desea ingresar. (Marco mucha atencion), este nombre esta
     * hardcodeado, se debe obtener de la base de datos cuando se cree okkey? para dejar en claro que habra muchos chats
     * por ahora que se creen.
     * private String roomName = "observable-Chat_Privado";
     */
    private String roomName = "ComputoChat";
    private EditText editText;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    double longitud=0;
    double latitud=0;
    String Nombrechat;
    boolean admin;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_grupal);
        locationStart();

        /**
         * Resivimos valores, Nombre chat, nombre usuario, si es o no administrador
         */
        Nombrechat=getIntent().getStringExtra("NombreChat");
        roomName="observable-"+Nombrechat;

        String NombreUsuario=getIntent().getStringExtra("NombreUsuario");
        /**
         * Inicialización de datos
         */
        editText = (EditText) findViewById(R.id.editText);
        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        MemberData data = new MemberData(NombreUsuario, getRandomColor());
        admin=getIntent().getBooleanExtra("Administrador",false);


        /**
         * Este valor compone las condicioens para conectarse al servidor de chat (Scaledron)
         */
        scaledrone = new Scaledrone(channelID, data);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connection open");
                scaledrone.subscribe(roomName, ChatGrupal.this);
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
                System.out.print("conexion fallida dentro");
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });


    }

    public ChatGrupal(String roomName, String nombrechat) {
        this.roomName = roomName;
        Nombrechat = nombrechat;
    }

    public void ciclo() throws InterruptedException {

        for(int i=0;i<5;i++){
            Thread.sleep(5000);
            Toast.makeText(this,"Entro", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Metodo que se emplea cuando se envia el mensaje al servidor (nube)
     * @param view
     */
    public void sendMessage(View view) {
        String message = editText.getText().toString();
        if (message.length() > 0) {
            scaledrone.publish(roomName, message);
            editText.getText().clear();
        }
    }

    /**
     * si la conexion esta habierta
     * @param room
     */
    @Override
    public void onOpen(Room room) {
        System.out.println("Conneted to room");
    }

    /**
     * Si la conexion es fallida
     * @param room
     * @param ex
     */
    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.err.println("conexion fallida fuera");
    }

    /**
     * Cuando se envia un mensaje este resive una retroalimentación del servidor, asi que regresa el mensaje enviado
     * de igual forma regresa el mensaje que un miembro envia
     * @param room Resive el cuarto de chat
     * @param receivedMessage Recive el mensaje se se envio o el que recivio
     */
    @Override
    public void onMessage(Room room,com.scaledrone.lib.Message receivedMessage) {

        //objeto mapper para mapear...
        final ObjectMapper mapper = new ObjectMapper();
        try {
            //Creamos MemberData es el objeto mapeado con lo que nos llega del mensaje
            //NOTA: en esta parte es donde se debe obtener el nombre y color (Base de datos...)
            final MemberData data = mapper.treeToValue(receivedMessage.getMember().getClientData(), MemberData.class);




            //este regresa si el mensaje tiene un id, si no viene nada en el mensaje este sera false, el idcliente no depende
            //de los datos que se resivan.
            boolean belongsToCurrentUser = receivedMessage.getClientID().equals(scaledrone.getClientID());

            //Creamos una instancia del mensaje que nos llega (esta clase nosotros la creamos), despues lo corremos en un hilo
            //en ves de NULL debe ir data.- se usa null para pruebas, data seran los datos de arriba una ves se use base de datos.
            final c.plutarco.geolocalizacin.Message message = new c.plutarco.geolocalizacin.Message(receivedMessage.getData().asText(),data, belongsToCurrentUser);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //enviamos el mensaje
                    messageAdapter.add(message);
                    //Scrol para los mensajes!!!
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        } catch (Exception e) {
            System.err.print(e);
        }
    }

    /**
     * Este es una clase provisional para los mensajes (aunque no la use)
     * @return
     */
    private String getRandomName() {
        String[] adjs = {"autumn", "hidden", "bitter", "misty", "silent", "empty", "dry", "dark", "summer", "icy", "delicate", "quiet", "white", "cool", "spring", "winter", "patient", "twilight", "dawn", "crimson", "wispy", "weathered", "blue", "billowing", "broken", "cold", "damp", "falling", "frosty", "green", "long", "late", "lingering", "bold", "little", "morning", "muddy", "old", "red", "rough", "still", "small", "sparkling", "throbbing", "shy", "wandering", "withered", "wild", "black", "young", "holy", "solitary", "fragrant", "aged", "snowy", "proud", "floral", "restless", "divine", "polished", "ancient", "purple", "lively", "nameless"};
        String[] nouns = {"waterfall", "river", "breeze", "moon", "rain", "wind", "sea", "morning", "snow", "lake", "sunset", "pine", "shadow", "leaf", "dawn", "glitter", "forest", "hill", "cloud", "meadow", "sun", "glade", "bird", "brook", "butterfly", "bush", "dew", "dust", "field", "fire", "flower", "firefly", "feather", "grass", "haze", "mountain", "night", "pond", "darkness", "snowflake", "silence", "sound", "sky", "shape", "surf", "thunder", "violet", "water", "wildflower", "wave", "water", "resonance", "sun", "wood", "dream", "cherry", "tree", "fog", "frost", "voice", "paper", "frog", "smoke", "star"};
        return (
                adjs[(int) Math.floor(Math.random() * adjs.length)] +
                        "_" +
                        nouns[(int) Math.floor(Math.random() * nouns.length)]
        );
    }

    /**
     * Clase para los colores, esta debe usarse al momento que la persona elija su nombre (Marco mucho ojo)
     * @return
     */
    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }














    /**
     * Para ir actualizando el GPS en la base de datos
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 100000);
            return;
        }
        //recordar cual es o 10,000 o 100,000
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 100000, (LocationListener) Local);
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
        ChatGrupal chatGrupal;
        public ChatGrupal getMainActivity() {
            return chatGrupal;
        }
        public void setMainActivity(ChatGrupal chatGrupal) {
            this.chatGrupal = chatGrupal;
        }

        private DatabaseReference datosBD;
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
            if(admin==true){
                //funcionalidad
                datosBD = FirebaseDatabase.getInstance().getReference();
                datosBD.child("Datos").child(Nombrechat).child("latitud").setValue(latitud);
                datosBD.child("Datos").child(Nombrechat).child("longitud").setValue(longitud);

                Toast.makeText(chatGrupal, Text, Toast.LENGTH_SHORT).show();
            }
            this.chatGrupal.setLocation(loc);
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

class MemberData {
    private String name;
    private String color;

    public MemberData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public MemberData() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}



