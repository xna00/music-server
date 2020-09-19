package musicserver.musicsource;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Kugou extends Player {
    private static final int limit = 5;

    public Kugou() {
        name = "kg";
    }

    public static void main(String[] args) {
        Kugou kg = new Kugou();
        try {
            System.out.println(kg.search("明天你好"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String search(String keywords) throws IOException {
        int i=0;
        String name, id,歌手 = "",专辑="";
        Jsonparse js = new Jsonparse();
        String search_url = "http://mobilecdn.kugou.com/api/v3/search/song" +
                "?format=json&keyword="+ keywords +"&page=1&pagesize=10&showtype=1";
        System.out.println(search_url);
        Connection c = Jsoup.connect(search_url).ignoreContentType(true);

        Document doc = c.get();
        String text = doc.body().text();
        System.out.println(text);
        text = js.parse2("/data/",text);
        musics.clear();
        for(i = 0; i < limit; i++){
            String song = js.parse2("/info["+ i +"]/",text);
            name = js.parse2("/songname/", song);
            id = js.parse2("/hash/", song);
            String tmp;
            歌手 = js.parse2("/singername/",song);
            专辑 = js.parse2("/album_name/",song);



            musics.add(new Music(name, id,歌手, 专辑));
        }
        return toJson();
    }
    public String put_url(String id) throws IOException {
        Jsonparse js = new Jsonparse();
        System.out.println(id);
        String ret;
        String details_url = "http://www.kugou.com/yy/index.php?r=play/getdata&hash=" + id;
        System.out.println(details_url);
        Connection c = Jsoup.connect(details_url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");

        c.cookie("kg_dfid","3dguXl48XThX0IhrVQ3wT3ow");
        c.cookie("kg_mid","a0d44246b04cef1c08f3ff0551e67ddb");
        Document doc = c.get();
        System.out.println(doc.body().text());
        ret = js.parse2("/data/play_url/", doc.body().text());
        if(ret != null) {
            ret = ret.replace("\\", "");
        }
        System.out.println(ret);
        return ret;
    }


    public String put_img_url(String id) throws IOException {
        Jsonparse js = new Jsonparse();
        String ret;
        String details_url = "http://www.kugou.com/yy/index.php?r=play/getdata&hash=" + id;
        Connection c = Jsoup.connect(details_url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");

        c.cookie("kg_dfid","3dguXl48XThX0IhrVQ3wT3ow");
        c.cookie("kg_mid","a0d44246b04cef1c08f3ff0551e67ddb");
        Document doc = c.get();

        ret = js.parse2("/data/img/", doc.body().text());
        if(ret != null) {
            ret = ret.replace("\\", "");
        }
        System.out.println(ret);
        return ret;
    }


    public String put_lrc_url(String id) {
        return id;
    }

    public String url_lrc(String url) throws IOException {
        Jsonparse js = new Jsonparse();
        String ret;
        String details_url = "http://www.kugou.com/yy/index.php?r=play/getdata&hash=" + url;
        Connection c = Jsoup.connect(details_url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");

        c.cookie("kg_dfid","3dguXl48XThX0IhrVQ3wT3ow");
        c.cookie("kg_mid","a0d44246b04cef1c08f3ff0551e67ddb");
        Document doc = c.get();
        String text = doc.body().text();
        text = decodeUnicode(text);
        ret = js.parse2("/data/lyrics/", text);
        //BUG
        if(ret != null) {
            ret = ret.replace("\\", "");
        }
        System.out.println(ret);
        return ret;
    }
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuilder outBuffer = new StringBuilder(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
}
