package musicserver.musicsource;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;


public class Xiami extends Player {
    private static final int limit = 5;
    private String a;
    private Map<String, String> cookies;
    public Xiami(){
        name = "虾米";
    }
    public static void main(String[] args) throws IOException {
        String url = "https://gitee.com/xie_01/Xmusic/raw/master/apks/output.json";
        Connection c = Jsoup.connect(url);
        System.out.println(c.get().body().text());
    }
    public String search(String keywords) throws IOException {
        int i;
        String result = "Xiami:\n";
        String songname, songid;
        Jsonparse js = new Jsonparse();
        String search_url = "https://www.xiami.com/search?key="+keywords;
        Connection c1 = Jsoup.connect(search_url);

        //c1.method(Connection.Method.GET);
        //System.out.println(c1.request().cookies());
        //这一行有问题！！！ 没有关闭gzip
        //Connection.Response re = c1.execute();
        //cookies = re.cookies();
        //System.out.println(cookies);
        /*String xm_sg_tk = re.cookie("xm_sg_tk");
        String t, _q;
        _q = "{\"key\":\""+ keywords +"\",\"pagingVO\":{\"page\":1,\"pageSize\":5}}";
        System.out.println(_q);
        a = xm_sg_tk.split("_")[0];
        t = a + "_xmMain_/api/search/searchSongs_"+ _q;
        String _s = md5(t);
        String searchSongs_url = "https://www.xiami.com/api/search/searchSongs?_q="+ URLEncoder.encode(_q,"UTF-8")+ "&_s=" + _s;
        System.out.println(searchSongs_url);
        Connection c = Jsoup.connect(searchSongs_url).ignoreContentType(true);
        c.cookies(cookies);
        Document doc = c.get();
        String json = doc.body().text();
        System.out.println(json);*/
        musics.clear();
        musics.add(new Music("空空如也~~", "", "", ""));
        /*
        for(i = 0; i < limit; i++){
            result = result + i +',';
            songname = js.parse2("/result/data/songs["+i+"]/songName/", json);
            songid = js.parse2("/result/data/songs["+i+"]/songId/", json);
            if(songname == null || songid == null)
                break;
            musics.add(new Music(songname, songid, "", "", 5));
        }*/
        return toJson();
    }

    public String put_url(String id) throws IOException {
        /*
        String ret = "";
        Connection c = Jsoup.connect("https://www.xiami.com/api/song/getPlayInfo?_q=%7B%22songIds%22:["+id+"]%7D&_s="
                +md5(a+"_xmMain_/api/song/getPlayInfo_"+"{\"songIds\":["+id+"]}")).ignoreContentType(true);
        c.cookies(cookies);
        String json = c.get().body().text();
        System.out.println(json);
        ret = new Jsonparse().parse2("/result/data/songPlayInfos[0]/playInfos[0]/listenFile/",json);
        System.out.println(ret);

         */
        return "";

    }
    private static String md5(String plainText) {
        //定义一个字节数组
        byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(plainText.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        //将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

}
