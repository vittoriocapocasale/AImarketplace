package ai.marketplace.server.structures;

//class used to overcome unwind transformation of a list to a single object.
// The single object is not mapped as a list in the users class, so this class is used instead
public class SingleArchive {
    SingleArchive(){};
    private SubdocumentArchive archive;

    public SubdocumentArchive getArchive() {
        return archive;
    }

    public void setArchive(SubdocumentArchive archive) {
        this.archive = archive;
    }
}
