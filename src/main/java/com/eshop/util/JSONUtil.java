package com.eshop.util;

import com.eshop.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by windvalley on 2018/8/25.
 */
@Slf4j
public class JSONUtil {
    public static ObjectMapper objectMapper = new ObjectMapper();

    static{
        //对象全部字段全部列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);

        //取消默认的日期转化为timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);

        //忽略空Bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);

        //全部的日期格式全部统一为"yyyy-MM-dd HH:mm:ss"
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDFORMATE));

        //忽略在json字符串中存在，但是在对象中不存在对应属性的情况，防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> String object2String(T object){
        if (object != null) {
            try {
                return object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
            } catch (IOException e) {
                log.warn("Parse object to string error", e);
                return null;
            }
        }
        return null;
    }

    public static <T> String object2StringPretty(T object){
        if (object != null) {
            try {
                return object instanceof String ? (String) object : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } catch (IOException e) {
                log.warn("Parse object to string error", e);
                return null;
            }
        }
        return null;
    }

    public static <T> T string2Object(String value, Class<T> clazz){
        if (StringUtils.isEmpty(value) == false  && clazz != null){
            try {
                return clazz.equals(String.class) ? (T)value : objectMapper.readValue(value, clazz);
            } catch (IOException e){
                log.warn("Parse string to object error", e);
                return null;
            }
        }
        return null;
    }

    public static <T> T string2Object(String value, TypeReference<T> typeReference){
        if (StringUtils.isEmpty(value) == false  && typeReference != null){
            try {
                return (T)(typeReference.getType().equals(String.class) ? (T)value : objectMapper.readValue(value, typeReference));
            } catch (IOException e){
                log.warn("Parse string to object error", e);
                return null;
            }
        }
        return null;
    }

    public static <T> T string2Object(String value, Class<?> collectionClass, Class<?>... elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return objectMapper.readValue(value, javaType);
        } catch (IOException e){
            log.warn("Parse string to object error", e);
            return null;
        }
    }

    public static void main(String[] args){
        User u1 = new User();
        u1.setId(1);
        u1.setUsername("wang jian");
        u1.setEmail("mec.wangjian@outlook.com");

        User u2 = new User();
        u2.setId(2);
        u2.setUsername("wang yinuo");
        u2.setEmail("mec.wangjyinuo@outlook.com");

        List<User> userList = new ArrayList<User>();
        userList.add(u1);
        userList.add(u2);

        String user1JSON = object2String(u1);

        String user1JSONPretty = object2StringPretty(u1);

        log.info("user1 json:{}", user1JSON);

        log.info("user1 json pretty:{}", user1JSONPretty);

        String userListString = object2StringPretty(userList);
        log.info("user list json pretty:{}", userListString);

        User user = string2Object(user1JSON, User.class);

        List<User> users1 = string2Object(userListString, ArrayList.class);
        List<User> users2 = string2Object(userListString, new TypeReference<List<User>>() {
        });
        List<User> users3 = string2Object(userListString, List.class, User.class);
    }
}
