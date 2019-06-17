package cn.sunnymaple.rest.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * 工具类
 * @author wangzb
 * @date 2019/6/5 13:49
 */
public class Utils {

    /**
     * 判断字符串是否为空
     * 可判断 null,"","  "三种类型
     * @param str 目标字符串
     * @return
     */
    public static boolean isEmpty(String str){
        return str == null || Objects.equals("",str.trim());
    }

    /**
     * 判断对象是否为null
     * @param object 目标对象
     * @return
     */
    public static boolean isEmpty(Object object){
        return object instanceof String ? isEmpty(object.toString()) : object == null;
    }

    /**
     * 判断Collection集合是否为空
     * 同CollectionUtils.isEmpty(collection)
     * @param collection Collection集合
     * @return
     */
    public static boolean isEmpty(Collection<?> collection){
        return CollectionUtils.isEmpty(collection);
    }

    /**
     * 判断数组是否为空
     * @param objects Object对象数组
     * @return
     */
    public static boolean isEmpty(Object[] objects){
        return objects == null || objects.length == 0;
    }

    /**
     * map转java对象
     * @param map map
     * @param tClass 目标java对象的Class
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T map2Object(Map map, Class<T> tClass) throws IOException {
        String jsonStr = JSON.toJSON(map).toString();
        return parseObject(jsonStr,tClass);
    }

    /**
     * 将json字符串转Object对象
     * 当然也支持json数组字符串转List，但注意返回的List的泛型为JSONObject对象
     * @param jsonStr 目标字符串
     * @param tClass 目标对象Class
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T parseObject(String jsonStr,Class<T> tClass) throws IOException {
        return JSON.parseObject(jsonStr,tClass);
    }

    /**
     * 通配符匹配URI
     * @param uri 目标uri
     * @param sources 通配符
     * @return
     */
    public static boolean uriMatching(String uri, List<String> sources){
        if (!Utils.isEmpty(sources)){
            for (String source : sources){
                AntPathMatcher matcher = new AntPathMatcher();
                boolean match = matcher.match(source, uri);
                if (match){
                    return true;
                }
            }
        }
        return false;
    }
}
