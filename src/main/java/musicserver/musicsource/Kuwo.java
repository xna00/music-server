package musicserver.musicsource;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.DecimalFormat;

public class Kuwo extends Player {
    private static final int limit = 5;

    public Kuwo() {
        name = "kw";
    }

    public static void main(String[] args) {
        Kuwo kw = new Kuwo();
        try {
            System.out.println(kw.search("最佳损友"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String search(String keywords) throws IOException {
        int i=0;
        String result = "Kuwo:\n";
        String name, id, 歌手, 专辑;
        Jsonparse js = new Jsonparse();
        String search_url = "http://search.kuwo.cn/r.s" +
                "?SONGNAME=" + keywords + "&ft=music&rformat=json&encoding=utf8&rn=10&callback=song&vipver=MUSIC_8.0.3.1";
        System.out.println(search_url);
        Connection c = Jsoup.connect(search_url).ignoreContentType(true);

        Document doc = c.get();
        String json = doc.body().text();
        json = json.replace("try{var jsondata=","");
        json = json.replace(" ; song(jsondata);}catch(e){jsonError(e)}","");
        json = json.replace("'","\"");
        System.out.println(json);
        System.out.println(js.parse2("/",json));
        String text = json;
        musics.clear();
        for(i = 0; i < limit; i++){
            String song = js.parse2("/abslist["+ i +"]/",text);
            name = js.parse2("/SONGNAME/", song);
            id = js.parse2("/MUSICRID/", song);
            String tmp;
            歌手 = js.parse2("/ARTIST/", song);

            专辑 = js.parse2("/ALBUM/",song);

            result = result + i +',';
            musics.add(new Music(name, id,歌手, 专辑));
        }
        return toJson();
    }
    public String put_url(String id) throws IOException {
        Jsonparse js = new Jsonparse();
        String rid = id;
        String ret;
        String details_url = "http://antiserver.kuwo.cn/anti.s" +
                "?response=url&rid="+ rid +"&format=mp3&type=convert_url";
        Connection c = Jsoup.connect(details_url);
        c.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");
        Document doc = c.get();
        System.out.println(doc.body().text());
        ret = doc.body().text();
        return ret;
    }
    public String put_img_url(String id) throws IOException {
        id = id.substring(id.indexOf('_') + 1);
        String url = "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId="+id+"&httpsStatus=1&reqId=da3ce2d0-ddd4-11ea-9717-19e58cc26384";
        System.out.println(url);
        Connection c = Jsoup.connect(url);
        c.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");
        Document doc = c.get();
        String text = doc.body().text();
        String ret = new Jsonparse().parse2("/data/songinfo/pic/", text);
        System.out.println(ret);
        return ret;
    }
    public String put_lrc_url(String id) {
        return id;
    }

    public String url_lrc(String url) throws IOException {
        String id = url;
        id = id.substring(id.indexOf('_') + 1);
        String urll = "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId="+id+"&httpsStatus=1&reqId=da3ce2d0-ddd4-11ea-9717-19e58cc26384";
        System.out.println(urll);
        Connection c = Jsoup.connect(urll);
        c.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");
        Document doc = c.get();
        String text = doc.body().text();
        Jsonparse js = new Jsonparse();
        jsonArray lyrics = new jsonArray(js.parse2("/data/lrclist/", text));
        String ret = "";
        String time;
        float time2;
        int minutes;
        DecimalFormat decimalFormat=new DecimalFormat("00.00");
        for (int i = 0; i < lyrics.lenth(); i++){
            time = js.parse2("/time/", lyrics.getvalue(i));
            time2 = Float.parseFloat(time);
            minutes = 0;
            while (time2 >= 60){
                minutes++;
                time2 -= 60;
            }
            time = (minutes < 10 ? "0"+minutes : ""+minutes) + ":";
            time += decimalFormat.format(time2);
            ret = ret + "[" + time + "]" + js.parse2("/lineLyric/", lyrics.getvalue(i)) + "\\n";
        }

        System.out.println(ret);
        return ret;
    }
}
