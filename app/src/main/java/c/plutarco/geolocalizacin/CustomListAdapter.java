package c.plutarco.geolocalizacin;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {
    private ArrayList nombreGrupo;
    private ArrayList nombreUsuario;
    private AppCompatActivity activity;

    private int x=0;

    public CustomListAdapter(ArrayList nombreGrupo, ArrayList nombreUsuario, AppCompatActivity activity){
        this.nombreGrupo=nombreGrupo;
        this.nombreUsuario=nombreUsuario;
        this.activity=activity;
    }

    @Override
    public int getCount(){
        return nombreGrupo.size();
    }

    @Override
    public Object getItem(int i){
        return nombreGrupo.get(i);
    }

    @Override
    public long getItemId(int i){
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.activity_elemento__bd, viewGroup, false);
        ((TextView)view.findViewById(R.id.tv_nombreBD)).setText(String.valueOf(nombreGrupo.get(i)));
        ((TextView)view.findViewById(R.id.tv_usuarioBD)).setText(String.valueOf(nombreUsuario.get(i)));
        return view;
    }
}
