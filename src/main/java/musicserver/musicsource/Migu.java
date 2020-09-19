package musicserver.musicsource;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Migu extends Player {
    private static final int limit = 5;
    //不能用了
    public Migu() {
        name = "咪咕";
    }

    public static void main(String[] args) {
        Migu mg = new Migu();
        try {
            System.out.println(mg.search("青花瓷"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(mg.put_url(1));
    }
    public String search(String keywords) throws IOException {
        int i=0;
        String name, id, 歌手 = "", 专辑 = "";
        Jsonparse js = new Jsonparse();
        String search_url = "http://migu.w0ai1uo.org/search?keyword=" + keywords;
        System.out.println(search_url);
        Connection c = Jsoup.connect(search_url).ignoreContentType(true);

        Document doc = c.get();
        String json = doc.body().text();
        System.out.println(json);
        json = js.parse2("/data/",json);
        musics.clear();

        for(i = 0; i < limit; i++){
            String song = js.parse2("/list["+i+"]/",json);
            name = js.parse2("/name/", song);
            id = js.parse2("/id/", song);
            id += ("/"+js.parse2("/cid/",song));
            String tmp;
            歌手 = "";
            for (int j = 0; (tmp= js.parse2("/artists["+j  +"]/name/",song))!=null; j++) {
                歌手 +=(tmp+"/") ;
            }
            if (!歌手.isEmpty()) {
                歌手 = 歌手.substring(0,歌手.length()-1);
            }
            专辑 = js.parse2("/album/name/",song);
            musics.add(new Music(name, id,歌手, 专辑));
        }
        return toJson();
    }
    public String put_url(String id){
        int i;
        i = id.indexOf('/');
        String url = "http://migu.w0ai1uo.org/song?id="+id.substring(0,i)+"&cid="+id.substring(i+1);
        Connection c = Jsoup.connect(url).ignoreContentType(true);
        Document doc = null;
        try {
            doc = c.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = doc.body().text();

        Jsonparse js = new Jsonparse();
        return js.parse2("/data/320k/",json);
    }
}
