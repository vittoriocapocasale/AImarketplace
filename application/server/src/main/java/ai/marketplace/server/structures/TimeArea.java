package ai.marketplace.server.structures;


import java.util.ArrayList;

//interface class to exchange data with the client
public class TimeArea
{
    private Long from;
    private Long to;
    private ArrayList<Position> polygon;
    private ArrayList<String> users;

    public TimeArea(){}

    public TimeArea(Long from, Long to, ArrayList<Position> polygon, ArrayList<String> users) {
        this.from = from;
        this.to = to;
        this.polygon = polygon;
        this.users=users;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public ArrayList<Position> getPolygon() {
        return polygon;
    }

    public void setPolygon(ArrayList<Position> polygon) {
        this.polygon = polygon;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }
}
