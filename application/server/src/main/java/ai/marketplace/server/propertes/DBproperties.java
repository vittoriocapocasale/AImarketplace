package ai.marketplace.server.propertes;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//imports properties defined in application.properties as class

@Component
@ConfigurationProperties //
public class DBproperties
{

    private String DBhost;

    private int DBport;

    private String DBusername;

    private String DBpassword;

    private String DBdatabase;

    private String DBuri;

    public DBproperties() {
    }

    public DBproperties(String DBhost, int DBport, String DBusername, String DBpassword, String DBdatabase, String DBuri) {
        this.DBhost = DBhost;
        this.DBport = DBport;
        this.DBusername = DBusername;
        this.DBpassword = DBpassword;
        this.DBdatabase = DBdatabase;
        this.DBuri=DBuri;
    }

    public String getDBhost() {
        return DBhost;
    }

    public void setDBhost(String DBhost) {
        this.DBhost = DBhost;
    }

    public int getDBport() {
        return DBport;
    }

    public void setDBport(int DBport) {
        this.DBport = DBport;
    }

    public String getDBusername() {
        return DBusername;
    }

    public void setDBusername(String DBusername) {
        this.DBusername = DBusername;
    }

    public String getDBpassword() {
        return DBpassword;
    }

    public void setDBpassword(String DBpassword) {
        this.DBpassword = DBpassword;
    }

    public String getDBdatabase() {
        return DBdatabase;
    }

    public void setDBdatabase(String DBdatabase) {
        this.DBdatabase = DBdatabase;
    }

    public String getDBuri() {
        return DBuri;
    }

    public void setDBuri(String DBuri) {
        this.DBuri = DBuri;
    }
}
