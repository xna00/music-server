package musicserver.musicsource;


import java.io.IOException;
import java.util.ArrayList;


public class Player {
    String name;
    int 编号;

    public String getName() {
        return name;
    }

    ArrayList<Music> musics = new ArrayList<>();
    public String search(String s) throws IOException {
        return null;
    }
    public String musicInfo(String id) throws IOException {
        return infoToJson(put_url(id), put_img_url(id), url_lrc(put_lrc_url(id)));
    }
    public String put_url(String id) throws IOException {
        return null;
    }
    public String put_img_url(String id) throws IOException {
        return null;
    }
    public String put_lrc_url(String id) {
        return null;
    }
    public String url_lrc(String url) throws IOException {
        return null;
    }
    String toJson() {
        String ret = "";
        for (int i = 0; i < musics.size(); i++) {
            ret += (musics.get(i).toJson() + (i != musics.size()-1 ? "," : ""));
        }
        musics.clear();
        return "{\"result\":[" + ret +"]}";
    }
    String infoToJson(String musicUrl, String imgUrl, String lrc){
        return "{\"musicUrl\":" + "\""+musicUrl+"\"" + ",\"imgUrl\":" + "\""+imgUrl+"\"" + ",\"lrc\":" + "\""+lrc+"\"" + "}";
    }
}
