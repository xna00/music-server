package musicserver.musicsource;

import java.io.IOException;

public class PlayerList {
    static public Player[] players= {new Netease(), new QQ(), new Kuwo(), new Kugou()};
    static Player findPlayer(String name) {
        for (Player p:players) {
            if (p.getName().equals(name))
                return p;
        }
        return null;
    }
    static public String search(String player, String name) {
        Player p = findPlayer(player);
        if (p != null && name != null) {
            try {
                return p.search(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static public String getInfo(String player, String id) {
        Player p = findPlayer(player);
        if(p != null && id != null) {
            try {
                return p.musicInfo(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
