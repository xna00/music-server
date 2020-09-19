package musicserver.musicsource;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


public class Netease extends Player {

    private static final int limit = 5;

    public Netease() {
        name = "nt";
    }

    public static void main(String[] args) {
        Netease nt = new Netease();
        int i;
        try {
            nt.search("爱到永远");
            //
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String search(String s) throws IOException {
        int i=0;
        String name, id, 歌手, 专辑, img_url;
        Jsonparse js = new Jsonparse();
        String search_url = "http://music.163.com/api/search/pc";
        Connection c = Jsoup.connect(search_url);
        c.data("s",s);
        c.data("type","1");
        c.data("limit",String.valueOf(limit));
        Connection.Response response = c.execute();

//        System.out.println(response.body());
        String text = response.body();
//        System.out.println(text);
        text = js.parse2("/result/",text);
        for(i = 0; i < limit; i++) {
            String song = js.parse2("/songs[" + i + "]/", text);
            name = js.parse2("/name/", song);
            id = js.parse2("/id/", song);
            String tmp;
            歌手 = "";
            for (int j = 0; (tmp = js.parse2("/artists[" + j + "]/name/", song)) != null; j++) {
                歌手 += (tmp + "/");
            }
            if (!歌手.isEmpty()) {
                歌手 = 歌手.substring(0, 歌手.length() - 1);
            }
            专辑 = js.parse2("/album/name/", song);
            img_url = js.parse2("/album/blurPicUrl/", song);
            musics.add(new Music(name, id, 歌手, 专辑));
        }
        return toJson();
    }

    @Override
//    public String musicInfo(String id) throws IOException {
//        return infoToJson(put_url(id), put_img_url(id), url_lrc(put_lrc_url(id)));
//    }
    public String put_url(String id){
        return "https://music.163.com/song/media/outer/url?id="+ id +".mp3";
    }
    public String put_img_url(String id) throws IOException {
        String url = "http://music.163.com/api/song/detail/?id="+id+"&ids=%5B"+id+"%5D";
        Connection c = Jsoup.connect(url);
        Document doc = c.get();
        String text = doc.body().text();
        String image_url = new Jsonparse().parse2("/songs/[0]/album/blurPicUrl/",text);
        System.out.println(text+":::::::"+image_url);
        return image_url;
    }
    public String put_lrc_url(String id){
        return "http://music.163.com/api/song/lyric?os=pc&id="+id+"&lv=-1&kv=-1&tv=-1";
    }
    public String url_lrc(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        String text = document.body().text();
        return new Jsonparse().parse2("/lrc/lyric/",text);
    }
}

