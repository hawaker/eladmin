package me.zhengjie.modules.wkc.client;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class WanKeCloudDecoder implements Decoder {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        String result = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
        logger.info("WanKeCloud response body:[{}]", result);
        return WanKeCloudUtil.readValueType(result, type);
    }
}
