package c.plutarco.geolocalizacin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.database.DatabaseError;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Buscar extends AppCompatActivity {

    private List<DatosBD> lista= new ArrayList<DatosBD>();
    ArrayAdapter<DatosBD> adapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    EditText nombre;
    ListView listadatos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        nombre= findViewById(R.id.nombre);
        listadatos= findViewById(R.id.lista);
        iniciarfirebase();

        listadatos();
    }

    public void listadatos(){
        databaseReference.child("Datos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista.clear();
                for(DataSnapshot objSnapShot: dataSnapshot.getChildren()){
                    DatosBD datos=objSnapShot.getValue(DatosBD.class);
                    lista.add(datos);

                    adapter= new ArrayAdapter<DatosBD>(Buscar.this,android.R.layout.simple_list_item_1,lista);
                    listadatos.setAdapter(adapter);
                }
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
}


