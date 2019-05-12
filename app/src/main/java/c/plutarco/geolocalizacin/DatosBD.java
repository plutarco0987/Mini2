package c.plutarco.geolocalizacin;

public class DatosBD {
    String nombreG, nombreU, dist;

    public DatosBD(){

    }

    public DatosBD(String nombreG, String nombreU, String dist){
        this.nombreG=nombreG;
        this.nombreU=nombreU;
        this.dist=dist;
    }

    public String getNombreG() {
        return nombreG;
    }

    public String getNombreU() {
        return nombreU;
    }

    public String getDist() {
        return dist;
    }
}
