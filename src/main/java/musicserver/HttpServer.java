package musicserver;

import musicserver.musicsource.PlayerList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


/**
 * @Description: //TODO  简单的HTTP服务器， 其实就是接受HTTP请求，并解析请求，
 * @Author: sunfch
 * @Date: 2019/1/3 16:35
 * @Param:
 * @Return:
 */
public class HttpServer {

    public static void main(String[] args) {
        String errorMsg = "{\"msg\":\"参数错误\",\"code\":400}",
                response;
        try {
            /*监听端口号，只要是8888就能接收到*/
            ServerSocket ss = new ServerSocket(8888);
            Map<String, String> params = new HashMap<>();
            while (true) {
                /*实例化客户端，固定套路，通过服务端接受的对象，生成相应的客户端实例*/
                Socket socket = ss.accept();
                /*获取客户端输入流，就是请求过来的基本信息：请求头，换行符，请求体*/
                BufferedReader bd = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                params.clear();
                /**
                 * 接受HTTP请求，并解析数据
                 */
                String requestHeader;
                int contentLength = 0;

                while ((requestHeader = bd.readLine()) != null && !requestHeader.isEmpty()) {

//                    System.out.println(requestHeader);
                    /**
                     * 获得GET参数
                     */
                    if (requestHeader.startsWith("GET")) {
                        int begin = requestHeader.indexOf("/");
                        int end = requestHeader.indexOf("HTTP/") - 1;
                        String condition = requestHeader.substring(begin, end);
                        condition = URLDecoder.decode(condition, "UTF-8");
                        begin = condition.indexOf('/');
                        end = condition.indexOf('?');
                        end = end == -1 ? condition.length() : end;
                        params.put("path", condition.substring(begin, end));
                        if (end != condition.length()) {
                            condition = condition.substring(end + 1);
                            String[] tmp = condition.split("&");
                            for (String p : tmp) {
                                params.put(p.substring(0, p.indexOf('=')), p.substring(p.indexOf('=') + 1));
                            }

                        }
                        System.out.println(params.toString());
                        System.out.println("GET参数是：" + requestHeader + "-----------");
                    }
                    /**
                     * 获得POST参数
                     * 1.获取请求内容长度
                     */
                    if (requestHeader.startsWith("Content-Length")) {
                        int begin = requestHeader.indexOf("Content-Lengh:") + "Content-Length:".length();
                        String postParamterLength = requestHeader.substring(begin).trim();
                        contentLength = Integer.parseInt(postParamterLength);
                        System.out.println("POST参数长度是：" + Integer.parseInt(postParamterLength));
                    }
                }
                StringBuilder sb = new StringBuilder();
                if (contentLength > 0) {
                    for (int i = 0; i < contentLength; i++) {
                        sb.append((char) bd.read());
                    }
                    System.out.println("POST参数是：" + sb.toString());
                }
                // PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFilename),"GB2312")));
                /*发送回执*/
                PrintWriter pw = new PrintWriter(socket.getOutputStream());

                pw.println("HTTP/1.1 200 OK");
                //为什么要设置gb2312?
                pw.println("Content-Type: text/html;charset=utf-8");
                pw.println();
                // pw.println(PlayerList.players[0].musicInfo("399367888"));
                // System.out.println(PlayerList.players[0].search("爱到永远"));
                if (Objects.equals(params.get("path"), "/api/s")) {
                    response = (response = PlayerList.search(params.get("p"), params.get("name"))) == null ? errorMsg : response;

                } else if (Objects.equals(params.get("path"), "/api/i")) {
                    response = (response = PlayerList.getInfo(params.get("p"), params.get("id"))) == null ? errorMsg : response;
                } else {
                    response = errorMsg;
                }
                System.out.println(response);
                pw.println(response);

                pw.flush();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}