import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * json 字符串工具类 <br/>
 * <p>
 * Created by yk on 5/15/17. <br/>
 */
public class JsonUtil {
    private static Logger logger = Logger.getLogger(JsonUtil.class);

    private Map<String, Object> jsonMap = new HashMap<String, Object>();
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void clear() {
        jsonMap.clear();
    }

    /**
     * 添加元素 <br/>
     *
     * @param key
     * @param value 　支持简单类型（即原生类型的包装器类）、bean对象、List<Object>、Map<String,Object>以及数组
     * @return
     */
    public Map<String, Object> put(String key, Object value) {
        jsonMap.put(key, value);
        return jsonMap;
    }

    /**
     * 判断是否要加引号
     *
     * @param value
     * @return
     */
    private static boolean isNoQuote(Object value) {
        return (value instanceof Integer || value instanceof Boolean
                || value instanceof Double || value instanceof Float
                || value instanceof Short || value instanceof Long || value instanceof Byte);
    }

    private static boolean isQuote(Object value) {
        return (value instanceof String || value instanceof Character);
    }

    /**
     * 返回形如{'apple':'red','lemon':'yellow'}的字符串
     *
     * @return
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        Set<Map.Entry<String, Object>> set = jsonMap.entrySet();
        for (Map.Entry<String, Object> entry : set) {
            Object value = entry.getValue();
            if (value == null) {
                continue;// 对于null值，不进行处理，页面上的js取不到值时也是null
            }
            sb.append("'").append(entry.getKey()).append("':");
            if (value instanceof JsonUtil) {
                sb.append(value.toString());
            } else if (isNoQuote(value)) {
                sb.append(value);
            } else if (value instanceof Date) {
                sb.append("'").append(formatter.format(value)).append("'");
            } else if (isQuote(value)) {
                sb.append("'").append(value).append("'");
            } else if (value.getClass().isArray()) {
                sb.append(ArrayToStr((int[]) value));
            } else if (value instanceof Map) {
                sb.append(fromObject((Map<String, Object>) value).toString());
            } else if (value instanceof List) {
                sb.append(ListToStr((List<Object>) value));
            } else {
                sb.append(fromObject(value).toString());
            }
            sb.append(",");
        }
        int len = sb.length();
        if (len > 1) {
            sb.delete(len - 1, len);
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 数组转字符串
     *
     * @param array
     * @return
     */
    public String ArrayToStr(Object array) {
        if (!array.getClass().isArray())
            return "[]";
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        int len = Array.getLength(array);
        Object v = null;
        for (int i = 0; i < len; i++) {
            v = Array.get(array, i);
            if (v instanceof Date) {
                sb.append("'").append(formatter.format(v)).append("'").append(
                        ",");
            } else if (isQuote(v)) {
                sb.append("'").append(v).append("'").append(",");
            } else if (isNoQuote(v)) {
                sb.append(i).append(",");
            } else {
                sb.append(fromObject(v)).append(",");
            }
        }
        len = sb.length();
        if (len > 1)
            sb.delete(len - 1, len);
        sb.append("]");
        return sb.toString();
    }

    /**
     * 集合转字符串
     *
     * @param list
     * @return
     */
    public String ListToStr(List<Object> list) {
        if (list == null)
            return null;
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        Object value = null;
        for (Iterator<Object> it = list.iterator(); it.hasNext(); ) {
            value = it.next();
            if (value instanceof Map) {
                sb.append(fromObject((Map) value).toString()).append(",");
            } else if (isNoQuote(value)) {
                sb.append(value).append(",");
            } else if (isQuote(value)) {
                sb.append("'").append(value).append("'").append(",");
            } else {
                sb.append(fromObject(value).toString()).append(",");
            }
        }
        int len = sb.length();
        if (len > 1)
            sb.delete(len - 1, len);
        sb.append("]");
        return sb.toString();
    }

    /**
     * 从一个bean装载数据，返回一个JsonUtil对象。 <br/>
     *
     * @param bean
     * @return
     */
    public JsonUtil fromObject(Object bean) {
        JsonUtil json = new JsonUtil();
        if (bean == null)
            return json;
        Class cls = bean.getClass();
        Field[] fs = cls.getDeclaredFields();
        Object value = null;
        String fieldName = null;
        Method method = null;
        int len = fs.length;
        for (int i = 0; i < len; i++) {
            fieldName = fs[i].getName();
            try {
                method = cls.getMethod(getGetter(fieldName), (Class[]) null);
                value = method.invoke(bean, (Object[]) null);
            } catch (Exception e) {
                logger.error(e.getMessage());
                continue;
            }
            json.put(fieldName, value);
        }
        return json;
    }

    /**
     * 从Map中装载数据 <br/>
     *
     * @param map
     * @return
     */
    public JsonUtil fromObject(Map<String, Object> map) {
        JsonUtil json = new JsonUtil();
        if (map == null)
            return json;
        json.getMap().putAll(map);
        return json;
    }

    /**
     * 将给定json字符串转换为指定实体对象
     *
     * @param jsonObject
     * @param entity
     * @return
     */
    public Object jsonObjectToObject(JSONObject jsonObject, Object entity) {
        Field[] field = entity.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
        try {
            for (int j = 0; j < field.length; j++) { // 遍历所有属性
                String name = field[j].getName(); // 获取属性的名字
                if (!jsonObject.containsKey(name)) {
                    continue;
                }
                String nameU = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
                String type = field[j].getGenericType().toString(); // 获取属性的类型
                if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                    Method m = entity.getClass().getMethod("set" + nameU, String.class);
                    m.invoke(entity, jsonObject.getString(name));
                }
                if (type.equals("class java.lang.Integer")) {
                    Method m = entity.getClass().getMethod("set" + nameU, Integer.class);
                    m.invoke(entity, jsonObject.getInteger(name));
                }
                if (type.equals("class java.lang.Long")) {
                    Method m = entity.getClass().getMethod("set" + nameU, Long.class);
                    m.invoke(entity, jsonObject.getLong(name));
                }
                if (type.equals("class java.util.Date")) {
                    Method m = entity.getClass().getMethod("set" + nameU, Date.class);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    m.invoke(entity, sdf.parse(jsonObject.getString(name)));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return entity;
    }


    /**
     * get方法的方法名
     *
     * @param property
     * @return
     */
    private String getGetter(String property) {
        return "get" + property.substring(0, 1).toUpperCase()
                + property.substring(1, property.length());
    }


    public Map<String, Object> getMap() {
        return this.jsonMap;
    }
}
