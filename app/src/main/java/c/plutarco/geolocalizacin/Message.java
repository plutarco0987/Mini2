package c.plutarco.geolocalizacin;

/**
 * Clase mensaje para su mapeo correspondiente cuando se resiva en el metodo OnMesage
 */
public class Message {
    private String text; //Texto del mensaje
    private MemberData memberData; // Datos del usuario que envia el mensaje (esto sea de base de datos...)
    private boolean belongsToCurrentUser; // El mensaje es para ese grupo en particular¿?

    public Message(String text, MemberData memberData, boolean belongsToCurrentUser) {
        this.text = text;
        this.memberData = memberData;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public MemberData getMemberData() {
        return memberData;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}
