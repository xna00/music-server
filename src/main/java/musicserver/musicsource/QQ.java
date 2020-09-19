package musicserver.musicsource;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class QQ extends Player {
    private static final int limit = 10;
    public QQ(){
        name = "qq";
    }


    public static void main(String[] args) {
        QQ qq = new QQ();
        try {
            System.out.println(qq.search("你是我的女朋友"));

            //qq.put_url(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public String search(String s) throws IOException {
        String search_url = "http://c.y.qq.com/soso/fcgi-bin/client_search_cp?aggr=1&cr=1&flag_qc=0&p=1&n="+limit+"&w=" + s;
        System.out.println(search_url);
        String text, id, name, 歌手, 专辑;
        Jsonparse js = new Jsonparse();
        Connection c = Jsoup.connect(search_url).ignoreContentType(true);
        Document doc = c.get();
        text = doc.body().text();
        text = text.substring(text.indexOf('(')+1, text.length()-1);
        int i;
        for(i =0; i<limit;i++) {
            id = js.parse2("/data/song/list["+i+"]/songmid/", text) + "|" + js.parse2("/data/song/list["+i+"]/albumid/", text);
            name = js.parse2("/data/song/list["+i+"]/songname/",text);
            歌手 = js.parse2("/data/song/list["+i+"]/singer[0]/name/",text);
            专辑 = js.parse2("/data/song/list["+i+"]/albumname/",text);
            musics.add(new Music(name, id,歌手, 专辑));
        }
        return toJson();
    }
    public String put_url(String id) throws IOException {
        id = id.substring(0, id.indexOf('|'));
        String token_url = "https://u.y.qq.com/cgi-bin/musicu.fcg?" +
                "-=getplaysongvkey8903794139311827&g_tk=5381&loginUin=0&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq.json&needNewCode=0&data=%7B%22" +
                "req%22%3A%7B%22module%22%3A%22CDN.SrfCdnDispatchServer%22%2C%22method%22%3A%22GetCdnDispatch%22%2C%22param%22%3A%7B%22guid%22%3A%226363169124%22%2C%22calltype%22%3A0%2C%22" +
                "userip%22%3A%22%22%7D%7D%2C%22req_0%22%3A%7B%22module%22%3A%22vkey.GetVkeyServer%22%2C%22method%22%3A%22CgiGetVkey%22%2C%22param%22%3A%7B%22guid%22%3A%226363169124%22%2C%22" +
                "songmid%22%3A%5B%22"+id+"%22%5D%2C%22songtype%22%3A%5B0%5D%2C%22uin%22%3A%220%22%2C%22" +
                "loginflag%22%3A1%2C%22platform%22%3A%2220%22%7D%7D%2C%22comm%22%3A%7B%22uin%22%3A0%2C%22format%22%3A%22json%22%2C%22ct%22%3A24%2C%22cv%22%3A0%7D%7D";
        System.out.println(token_url);
        String res_url;
        Connection c = Jsoup.connect(token_url).ignoreContentType(true);
        Document doc = c.get();
        String text = doc.body().text();
        System.out.println(text);

        Jsonparse js = new Jsonparse();
        String purl = js.parse2("/req_0/data/midurlinfo[0]/purl/", text);
        res_url = "http://ws.stream.qqmusic.qq.com/" + purl;
        System.out.println(res_url);

        return res_url;
    }
    public String put_img_url(String id) throws IOException {
        id = id.substring(id.indexOf('|') + 1);
        return id.equals("0") ? "https://y.gtimg.cn/mediastyle/music_v11/extra/default_300x300.jpg?max_age=31536000"
                :"https://imgcache.qq.com/music/photo/album_300/76/300_albumpic_"+id+"_0.jpg";
    }

    public String put_lrc_url(String id) {
        id = id.substring(0, id.indexOf('|'));
        return "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid="+id+"&format=json&nobase64=1";
    }

    public String url_lrc(String url) throws IOException {
        Connection c = Jsoup.connect(url);
        c.referrer("https://y.qq.com/portal/player.html");
        Document document = c.get();
        String text = document.text();
        System.out.println(text);
        return new Jsonparse().parse2("/lyric/", text);
    }
}
