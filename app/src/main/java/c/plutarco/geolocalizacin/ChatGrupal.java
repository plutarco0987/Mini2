package c.plutarco.geolocalizacin;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Message;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

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
    private String channelID = "BvIqQwlC4R5UN4c9";
    /**
     * RoomName.- este valor debe ser dado por el usuario y almasenado en la base de datos, en el activiti anterior
     * debe seleccionar el grupo de chat al que desea ingresar. (Marco mucha atencion), este nombre esta
     * hardcodeado, se debe obtener de la base de datos cuando se cree okkey? para dejar en claro que habra muchos chats
     * por ahora que se creen.
     */
    private String roomName = "observable-Chat_Privado";
    private EditText editText;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_grupal);

        /**
         * Resivimos valores, Nombre chat, nombre usuario, si es o no administrador
         */
        String Nombrechat=getIntent().getStringExtra("NombreChat");
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