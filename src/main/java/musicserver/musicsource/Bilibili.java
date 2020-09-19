package musicserver.musicsource;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Bilibili extends Player {
    private static final int limit = 5;

    public Bilibili() {
        name = "Bilibili";
    }

    public static void main(String[] args) throws IOException {

        //System.out.println(bl.put_url(0));
    }
    public String search(String keywords) throws IOException {
        int i=0;
        String title, av;
        String search_url = "https://search.bilibili.com/all?keyword="+keywords+"&from_source=banner_search";
        Connection c = Jsoup.connect(search_url).ignoreContentType(true);

        Document doc = c.get();
        Elements elements = doc.select("li.video-item.matrix");
        //System.out.println(elements);
        musics.clear();
        for(Element element : elements){
            if(i >= limit)
                break;
            Elements e = element.select("a[href]");
            title = e.attr("title");
            String href = e.attr("href");
            av = href.substring(href.indexOf("av") + 2, href.indexOf("?"));
            musics.add(new Music(title, av, "",""));
            i++;
        }
        return toJson();
    }
    public String put_url(String id) throws IOException {
        String url = "https://www.xbeibeix.com/api/bilibili.php";
        Connection c = Jsoup.connect(url);
        c.data("urlav","https://www.bilibili.com/video/av"+ id+"/");
        Document doc = c.post();
        Elements element = doc.select("ul.list-group");
        element = element.select("a[href]");
        return element.attr("href");
    }
}

