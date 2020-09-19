package musicserver.musicsource;



import java.util.ArrayList;

/*
*Created by xna.
 */
public class Jsonparse {
    public static void main(String[] args) {
        String s = "[{\"outputType\":{\"type\":\"APK\"},\"apkData\":{\"type\":\"MAIN\",\"splits\":[],\"versionCode\":1,\"apkName\":\"1.0.1\",\"enabled\":true,\"outputFile\":\"debug-v1.0.1-1.apk\",\"fullName\":\"debug\",\"baseName\":\"debug\"},\"path\":\"debug-v1.0.1-1.apk\",\"properties\":{}}]";

        Jsonparse js = new Jsonparse();
        jsonArray jsay = new jsonArray(s);
        System.out.println(js.parse2("/apkData/versionCode/",jsay.getvalue(0)));
        //System.out.println("fdalkfjls[]".indexOf('q'));
    }
    private int count(char c, String s){
        char[] tmp = s.toCharArray();
        int i = 0;
        int num;
        num = 0;
        for(i=0 ;i < s.length() ;i++){
            if(tmp[i]==c)
                num++;
        }
        return num;
    }
    int match(String s, int a){
        char[] cs = new char[s.length()+1];
        char[] ss = s.toCharArray();
        int i ,j;
        j=s.length();
        if(ss[a]!='{'&&ss[a]!='[') {
            //Log.i("here","fdas");
            return -1;
        }
        for(i=0;i<cs.length;i++)
            cs[i]=0;
        for(i=a;i<ss.length;i++){
            if(ss[i]=='{' || ss[i]=='['|| ss[i]=='}' ||ss[i]==']' ){
                if((cs[j]=='{' && ss[i]=='}')||(cs[j]=='['&&ss[i]==']')){
                    j++;
                    if(j==s.length()&&i!=a)
                        return i;
                }
                else {
                    cs[--j] = ss[i];
                }
            }
        }
        return -1;
    }
    public String parse2(String key, String json){
        if(json == null)
            return null;
        if(json.isEmpty())
            return null;
        if((json.toCharArray()[0] != '{' || json.toCharArray()[json.length() - 1] != '}') && (json.toCharArray()[0] != '[' || json.toCharArray()[json.length() - 1] != ']')) {
            char a = json.toCharArray()[0];
            char b = json.toCharArray()[json.length() - 1];
            System.out.println(a + b);
            return null;
        }

        String currkey;
        String currjs = json;
        String currjsay;
        int i, cun, p, index;
        p=0;
        cun = count('/', key) - 1;
        ///   /arr[0]/saa/
        for(i= 1; i<= cun; i++){
            currkey = key.substring(p+1, key.indexOf('/', p+1));
            p = p + currkey.length() +1;
            //System.out.println(currjs);
            if(currjs == null)
                return null;
            if(currkey.indexOf('[') < 0)
                currjs = (new jsonObject(currjs)).getvalue(currkey);
            else if(currkey.indexOf('[')>0){
                index = Integer.parseInt(currkey.substring(currkey.indexOf('[')+1,currkey.indexOf(']')));
                currjsay = (new jsonObject(currjs)).getvalue(currkey.substring(0, currkey.indexOf('[')));
                if(currjsay == null || currjsay.toCharArray()[0] != '[' || currjsay.toCharArray()[currjsay.length()-1] != ']')
                    return null;
                jsonArray jsar = new jsonArray(currjsay);
                if (index >= jsar.lenth())
                    return null;
                currjs = jsar.getvalue(index);
            }
            else if(currkey.indexOf('[')==0){
                currjsay = currjs;
                if(currjsay.toCharArray()[currjsay.length()-1] != ']')
                    return null;
                String s = currkey;
                index = Integer.parseInt(s.substring(currkey.indexOf('[')+1,currkey.indexOf(']')));
                jsonArray jsar = new jsonArray(currjsay);
                if (index >= jsar.lenth())
                    return null;
                currjs = jsar.getvalue(index);
            }
            else//
                return null;
        }
        return currjs;
    }
}

class jsonObject {
    private ArrayList<key_value> key_values = new ArrayList<>();
    private int i, type;
    private String json;

    jsonObject(String js){
        i = 0;
        json = js;
        key_value kv;
        //System.out.println(js);
        while (i < json.length()){
            if(json.toCharArray()[i] != '"'){
                //System.out.println(json.toCharArray()[i]+"\n");
                i++;
                continue;
            }
            String key = parseString();
            //System.out.println(key);
            if(json.toCharArray()[i] != ':'){
                System.out.println(":error!,not standard");
                return;
            }
            i++;
            while (i < json.length()){
                if(json.toCharArray()[i] == ' ')
                    i++;
                else
                    break;
            }

            String value = parseValue();
            kv = new key_value(key, value, type);
            key_values.add(kv);

        }
    }

    private String parseValue(){
        if(json.toCharArray()[i] == '{' || json.toCharArray()[i] == '['){
            if(json.toCharArray()[i] == '{')
                type = 0;
            else
                type = 1;
            return parseObjectOrArray();
        }
        else if(json.toCharArray()[i] == '"'){
            type = 2;
            return parseString();
        }
        else if("0123456789".indexOf(json.toCharArray()[i]) != (-1)){
            type = 3;
            return parseNumber();
        }
        else if(json.toCharArray()[i] == 't' ||json.toCharArray()[i] == 'f'){
            type = 4;
            return parseBoolean();
        }
        else if(json.toCharArray()[i] == 'n'){
            type = 5;
            return parseNull();
        }
        return null;
    }
    private String parseString(){
        String ret;
        ret = json.substring(i+1, json.indexOf('"', i+1));
        i = i+ret.length()+2;//"daf":"abc","ad":"dfs"
        return ret;
    }
    private String parseObjectOrArray(){
        String ret;
        Jsonparse js = new Jsonparse();
        ret = json.substring(i, js.match(json, i)+1);
        i = i+ ret.length();//{afsd},
        return ret;
    }
    private String parseNumber(){
        String ret;
        int j;
        for(j=i; "0123456789".indexOf(json.toCharArray()[j]) != (-1); j++)
            ;
        ret = json.substring(i, j);
        i = i+ ret.length();//"sad":1234,
        return ret;
    }
    private String parseBoolean(){
        //true false
        String ret;
        if(json.toCharArray()[i] == 't'){
            if(json.substring(i, i+ 4).equals("true"))
                ret = "true";
            else
                ret = "error true != true";
        }
        else {
            if(json.substring(i, i+5).equals("false"))
                ret = "false";
            else
                ret = "error false != false";
        }
        i = i+ ret.length();//"da":true,
        return ret;
    }
    private String parseNull(){
        String ret;
        if(json.substring(i, i+4).equals("null"))
            ret = "null";
        else
            ret = "error null != null";
        i = i+ ret.length();//null,
        return ret;
    }
    String getvalue(String key){
        int i;
        for(i=0; i<key_values.size();i++){
            if(key.equals(key_values.get(i).getkey()))
                return key_values.get(i).getvalue();
        }
        return null;
    }
    int gettype(String key){
        int i;
        for(i=0; i<key_values.size();i++){
            if(key.equals(key_values.get(i).getkey()))
                return key_values.get(i).gettype();
        }
        return -1;
    }
    void scan_key_values_array(){
        int i;
        for(i=0; i<key_values.size();i++){
            System.out.println(key_values.get(i).getkey());
            System.out.println(getvalue(key_values.get(i).getkey()));
            System.out.println(gettype(key_values.get(i).getkey()));
        }
    }
}

class jsonArray {
    private ArrayList<String> jsarray = new ArrayList<>();

    jsonArray(String jsar){
        if(jsar == null || jsar.toCharArray()[0] != '[' || jsar.toCharArray()[jsar.length()-1] != ']'){
            System.out.println("not an array!");
            return;
        }
        int i;
        i = 1;
        String tmp;
        Jsonparse js = new Jsonparse();
        while (i < jsar.length()- 1){
            if(jsar.toCharArray()[i] == ',' || jsar.toCharArray()[i] == ' '){
                i++;
                continue;
            }
            if(jsar.toCharArray()[i] == '{' || jsar.toCharArray()[i] == '['){
                tmp = jsar.substring(i, js.match(jsar, i)+ 1);
                jsarray.add(tmp);
                i = i+ tmp.length();//{sf},
            }
            else if(jsar.indexOf(',', i) > 0){
                tmp = jsar.substring(i, jsar.indexOf(',', i));
                if(tmp.toCharArray()[0] == '"' && tmp.toCharArray()[tmp.length()-1] == '"')
                    jsarray.add(tmp.substring(1,tmp.length()-1));
                i = i+ tmp.length();
            }
            else
                break;
        }
        if(i < jsar.length()-1)
            jsarray.add(jsar.substring(jsar.lastIndexOf(',')+ 1, jsar.length()- 1));
    }
    String getvalue(int i){
        if(i < jsarray.size())
            return jsarray.get(i);
        return null;
    }
    void scan_jsarray(){
        int i;
        for(i= 0; i< jsarray.size(); i++)
            System.out.println(getvalue(i));
    }
    int lenth(){
        return jsarray.size();
    }
}

class key_value{
    private String key, value;
    private int type;
    key_value(String ky, String va, int ty){
        key = ky;
        value = va;
        type = ty;
    }
    String getkey(){
        return key;
    }
    String getvalue(){
        return value;
    }
    int gettype(){
        return type;
    }
    /*
    0 object
    1 array
    2 string
    3 number
    4 boolean
    5 null
     */
}