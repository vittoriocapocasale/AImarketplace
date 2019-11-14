package ai.marketplace.server.structures;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;

//implementation of archive that is stored as subdocument
public class SubdocumentArchive implements Archive {

    @Id
    private ObjectId archiveId;
    private String tagName;
    private ArrayList<Position> positions;
    private Long soldTimes;
    private String owner;
    private int price;
    private Boolean deleted;

    public SubdocumentArchive()
    {
        this.positions=new ArrayList<>();
        this.soldTimes=0L;
        this.owner=null;
        this.tagName=null;
        this.archiveId=null;
        this.price=0;
        this.deleted=false;
    }

    public SubdocumentArchive(String tagName, String owner, ArrayList<Position> positions, int price)
    {
        this.tagName=tagName;
        this.positions=positions;
        this.soldTimes=0L;
        this.owner=owner;
        this.archiveId=null;
        this.price=price;
        this.deleted=false;
    }

    public String getId()
    {
        if(this.archiveId!=null) {
            return this.archiveId.toString();
        }
        return null;
    }

    public Boolean isDeleted()
    {
        return this.deleted;
    }

    public void setDeleted(Boolean deleted)
    {
        this.deleted=deleted;
    }


    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public  ArrayList<Position> getPositions()
    {
        return  this.positions;
    }

    public void setPositions (ArrayList<Position> positions)
    {
        this.positions=positions;

    }

    public Long getSoldTimes() {
        return soldTimes;
    }

    public void setSoldTimes(Long soldTimes) {
        this.soldTimes = soldTimes;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
