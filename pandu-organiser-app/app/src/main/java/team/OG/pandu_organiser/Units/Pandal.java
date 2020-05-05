package team.OG.pandu_organiser.Units;


public class Pandal implements Comparable<Pandal> {
    String name;
    int publicCount;
    String location;
    String theme;
    String picture;

    public Pandal(String name, int publicCount, String location, String theme, String picture) {
        this.name = name;
        this.publicCount = publicCount;
        this.location = location;
        this.theme = theme;
        this.picture = picture;
    }

    public Pandal() {
        this.name = "[ Empty ]";
        this.publicCount = 0;
        this.location = "[ Empty ]";
        this.theme = "[ Empty ]";
        this.picture = "https://semantic-ui.com/images/wireframe/image.png";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPublicCount() {
        return publicCount;
    }

    public void setPublicCount(int publicCount) {
        this.publicCount = publicCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public int compareTo(Pandal o) {
        return this.publicCount - o.publicCount;
    }
}
