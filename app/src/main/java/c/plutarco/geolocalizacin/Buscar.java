package c.plutarco.geolocalizacin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class Buscar extends AppCompatActivity {

    private DatabaseReference myDatabaseReference;
    private String grupoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        myDatabaseReference=FirebaseDatabase.getInstance().getReference("Datos");
        grupoId = myDatabaseReference.push().getKey();

        readData();

        // OCL

    }
    //edit text de su no nombre no este vacio

    private void addGroup(String nombreG, String nombreU){
        DatosBD datos = new DatosBD(nombreG, nombreU, 50,0,0);
        myDatabaseReference.child(grupoId).setValue(datos);
    }

    private void updateGrupo(String nombreG, String nombreU){
        myDatabaseReference.child(grupoId).child("nombreG").setValue(nombreG);
        myDatabaseReference.child(grupoId).child("nombreU").setValue(nombreU);
    }

    private void removeGroup(String nombreG){
        myDatabaseReference.child(grupoId).removeValue();
    }

    private void readData(){
        final ArrayList nombreG = new ArrayList<>();
        final ArrayList nombreU = new ArrayList<>();
        myDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable snapshotIterator = dataSnapshot.getChildren();
                Iterator iterator = snapshotIterator.iterator();
                while((iterator.hasNext())){
                    //http://www.universoandroidhn.com/2018/05/android-database-firebase-example.html
                    DatosBD value = iterator.next().getValue(DatosBD.class);
                    nombreG.add(value.getNombreG());
                    nombreU.add(value.getNombreU());
                    ((CustomListAdapter)(((ListView)findViewById(R.id.listViewX)).getAdapter())).notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ((ListView)findViewById(R.id.listViewX)).setAdapter(new CustomListAdapter(nombreG, nombreU, this));
    }

    private void findGrupo(String nombreG){
        Query deteleQuery = myDatabaseReference.orderByChild("nombreG").equalTo(nombreG);
        deteleQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Iterable snapshotIterator = dataSnapshot.getChildren();
                Iterator iterator = snapshotIterator.iterator();

                while((iterator.hasNext())){
                    Log.d("Group found: ", iterator.next().getValue().toString()+"---");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Group not found:","this group is not in the list");
            }

        });
    }
}


