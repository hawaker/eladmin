package me.zhengjie.modules.wkc.client;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedHashMap;

public class WanKeCloudEncoder implements Encoder {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final String ENCODING = "utf-8";

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        LinkedHashMap<String, Object> params = (LinkedHashMap<String, Object>) object;
        if (template.headers().containsKey("Content-Type")) {
            Collection<String> collection = template.headers().get("Content-Type");
            String contentType = collection.iterator().next();
            if (contentType.contains("json")) {
                String body = WanKeCloudUtil.toJson(params);
                logger.info("WanKeCloud request method:[{}],url:[{}],body:[{}]",template.method(),template.url(), body);
                template.body(body);
                return;
            }
        }
        String body = getBody(params, true);
        logger.info("WanKeCloud request method:[{}],url:[{}],body:[{}]",template.method(),template.url(), body);
        template.body(body);

    }

    public String getBody(LinkedHashMap<String, Object> map, Boolean addSign) {
        if (null == map || map.size() <= 0) {
            return null;
        }
        StringBuilder param = new StringBuilder();
        map.forEach((k, v) -> {
            try {
                param.append(k + "=" + URLEncoder.encode(v.toString(), ENCODING) + "&");
            } catch (UnsupportedEncodingException e) {
                logger.error("编码错误", e);
            }
        });
        String bodyParam = param.toString();
        bodyParam = bodyParam.substring(0, bodyParam.length() - 1);

        if (addSign) {
            logger.debug("WanKeCloud preEncryptBody:[{}]", bodyParam);
            String sign = DigestUtils.md5Hex(bodyParam);
            String body = bodyParam + "&sign=" + sign;
            return body;
        }
        return bodyParam;
    }
}