package musicserver.musicsource;


public class Music {
    public String name;
    public String id;
    public String singer;
    public String album;
    public Music(String name, String id, String songer, String album){
        this.name = name;
        this.id = id;
        this.singer = songer;
        this.album = album;
    }
    public String toJson(){
        return "{\"name\":"+"\""+name+"\""+","+"\"id\":"+"\""+id+"\""+","+"\"singer\":"+"\""+singer+"\""+","+"\"album\":"+"\""+album+"\"" + "}";
    }

}