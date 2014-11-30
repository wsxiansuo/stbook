package com.sxs.styd.stbook.util;

import java.util.Map;
/**.
 * 工具类
 * @author xiansuo
 *
 */
public class MapUtil {
    /**.
     * 祖父处理
     * @param map map
     * @param key guanjianzi 
     * @return String
     */
    public static String getString(Map<String, String> map, String key){
        if (map != null && map.containsKey(key)){
            return map.get(key).toString();
        }
        return "";
    }
}
