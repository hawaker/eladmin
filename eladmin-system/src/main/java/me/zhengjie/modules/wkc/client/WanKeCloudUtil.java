package me.zhengjie.modules.wkc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WanKeCloudUtil {
    private static ObjectMapper mapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(WanKeCloudUtil.class);

    static {
        // 指定策略不存在 json 字段不处理反射实体
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String getDeviceId(String phone) {
        return DigestUtils.md5Hex(phone).substring(0, 16).toUpperCase();
    }

    public static String getImeiId(String phone) {
        BigDecimal decimal = new BigDecimal(phone);
        return String.valueOf(decimal.multiply(decimal)).substring(0, 14).toLowerCase();
    }

    public static String getSignPassword(String pass) {
        StringBuilder sb = new StringBuilder();
        sb.append(DigestUtils.md5Hex(pass));
        char c1 = sb.charAt(2);
        char c2 = sb.charAt(8);
        char c3 = sb.charAt(17);
        char c4 = sb.charAt(27);
        sb.deleteCharAt(27);
        sb.deleteCharAt(17);
        sb.deleteCharAt(8);
        sb.deleteCharAt(2);
        sb.insert(2, c2);
        sb.insert(8, c1);
        sb.insert(17, c4);
        sb.insert(27, c3);
        return DigestUtils.md5Hex(sb.toString());
    }


    /**
     * <p>
     * 对象转换为字符串
     * </p>
     *
     * @param value 转换对象
     * @return
     */
    public static String toJson(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            logger.error("[ {} ] toJson error {}", value, e.getMessage());
        }
        return null;
    }

    /**
     * <p>
     * JSON 字符串转换为对象
     * </p>
     *
     * @param content   JSON 字符串
     * @param valueType 转换对象类型
     * @return
     */
    public static <T> T readValue(String content, Class<T> valueType) {
        try {
            if (StringUtils.isNotEmpty(content)) {
                return mapper.readValue(content, valueType);
            }
        } catch (Exception e) {
            logger.error("[ {} ] readValue error {}", valueType, e.getMessage());
        }
        return null;
    }

    public static <T> T readValueType(String content, Type type) {
        try {
            if (StringUtils.isNotEmpty(content)) {
                return mapper.readValue(content, mapper.constructType(type));
            }
        } catch (Exception e) {
            logger.error("[ {} ] readValue error {}", type.getTypeName(), e.getMessage(), e);
        }
        return null;
    }
}
