package me.zhengjie.modules.wkc.client;

import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class WanKeCloudRequestInterceptor implements RequestInterceptor {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void apply(RequestTemplate template) {
        template.header("cache-control", "no-cache");
        template.header("user-agent", "MineCrafter3/" + WanKeCloudService.APP_VERSION + " (iPhone; iOS 12.4.1; Scale/3.00)");

        if (Request.HttpMethod.GET.name().equalsIgnoreCase(template.method())) {
            String queryLine = template.queryLine();
            if (queryLine == null) {
                return;
            }
            if (queryLine.startsWith("?")) {
                queryLine = queryLine.substring(1);
            }

            if (template.headers().containsKey("Cookie")) {
                Collection<String> collection = template.headers().get("Cookie");
                String cookie = collection.iterator().next();
                if (null != cookie) {
                    String[] cookies = cookie.split(";");
                    if (cookies.length >= 1) {
                        String s = cookies[0];
                        String session = s.replace("sessionid=", "");
                        queryLine = queryLine.concat("&key=").concat(session);
                    }
                }
            }
            logger.debug("WanKeCloud preEncryptUrl:[{}]", queryLine);
            String sign = DigestUtils.md5Hex(queryLine);
            template.query("sign", sign);
        }
        logger.debug("WanKeCloud request url:[{}][{}]", template.method(), template.url());
    }
}
