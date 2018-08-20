package pk.merite.koha.webui.bookshelf;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Bookshelf {

    @Id
    private Long id;
    private String callNo;
    private String bookshelf;
    private String floorRoom;
    private String description;
    private String imageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallNo() {
        return callNo;
    }

    public void setCallNo(String callNo) {
        this.callNo = callNo;
    }

    public String getBookshelf() {
        return bookshelf;
    }

    public void setBookshelf(String bookshelf) {
        this.bookshelf = bookshelf;
    }

    public String getFloorRoom() {
        return floorRoom;
    }

    public void setFloorRoom(String floorRoom) {
        this.floorRoom = floorRoom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
