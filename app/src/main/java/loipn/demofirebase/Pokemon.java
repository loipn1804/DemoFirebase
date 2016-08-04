package loipn.demofirebase;

/**
 * Created by loipn on 7/30/2016.
 */
public class Pokemon {
    long id;
    String name;


    public Pokemon() {
    }

    public Pokemon(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
