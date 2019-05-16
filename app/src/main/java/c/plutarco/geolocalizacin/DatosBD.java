package c.plutarco.geolocalizacin;

public class DatosBD {
    String nombreG, nombreU;
    int dist;
    double latitud, longitud;

    public DatosBD(){

    }

    public DatosBD(String nombreG, String nombreU, int dist, double latitud, double longitud){
        this.nombreG=nombreG;
        this.nombreU=nombreU;
        this.dist=dist;
        this.latitud=latitud;
        this.longitud=longitud;

    }

    public String getNombreG() {
        return nombreG;
    }

    public String getNombreU() {
        return nombreU;
    }

    public int getDist() {
        return dist;
    }

    public double getLatitud(){
        return latitud;
    }

    public double getLongitud(){
        return longitud;
    }

    @Override
    public String toString() {
        return "Grupo: "+nombreG+"\n Distancia Limite:"+dist;
    }
}
