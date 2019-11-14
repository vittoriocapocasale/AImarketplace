package ai.marketplace.server.structures;

import java.util.ArrayList;
//interface defined to overcome index difficulties.
// Uniqueness of tagName is required in the archives document,
//but archives are also stored as subdocument in users document
// (and here the uniqueness creates problems):
public interface Archive
{
    String getId();

    Boolean isDeleted();

    void setDeleted(Boolean deleted);
    String getTagName();
    void setTagName(String tagName) ;

    ArrayList<Position> getPositions();

     void setPositions(ArrayList<Position> positions);

     Long getSoldTimes() ;

    void setSoldTimes(Long soldTimes) ;

     String getOwner() ;

     void setOwner(String owner) ;

    int getPrice() ;

    void setPrice(int price);
}
