package me.zhengjie.modules.wkc.service.impl;

import me.zhengjie.modules.system.service.JobService;
import me.zhengjie.modules.wkc.domain.WkcJob;
import me.zhengjie.modules.wkc.service.HandleJobType;
import me.zhengjie.modules.wkc.service.JobTypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@HandleJobType("BT")
public class BTJobTypeHandler implements JobTypeHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    JobService jobService;

    @Override
    public void handle(WkcJob job) {
//        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//        requestFactory.setConnectTimeout(5000);
//        requestFactory.setReadTimeout(8000);

//        RestTemplate template = new RestTemplate(requestFactory);
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
//        httpHeaders.add("referer", job.getUrl());
//
//        HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
//        ResponseEntity<String> response = template.exchange(job.getUrl(), HttpMethod.GET, httpEntity, String.class);
//        if (response.getStatusCode() != HttpStatus.OK) {
//            logger.error("解析任务{}失败", job.getUrl());
//            return;
//        }
//        String html = response.getBody();
//        Document doc = Jsoup.parse(html);
//        Elements elements = doc.getElementsByClass("uk-button ");
//        String magnet = null;
//        for (Element element : elements) {
//            String buf = element.attr("href");
//            if (buf.startsWith("magnet:")) {
//                magnet = buf;
//                break;
//            }
//        }
//        if (null == magnet) {
//            logger.error("读取[{}]的磁链失败!", job.getUrl());
//        }
//        JobEntity jobEntity = new JobEntity();
//        jobEntity.setUrl(magnet);
//        jobEntity.setParentId(job.getId());
//        jobEntity.setType("magnet");
//        jobEntity.setPhone(job.getPhone());
//        jobEntity.setCreated(false);
//        jobService.save(jobEntity);
//        job.setCreated(true);
//        jobService.save(job);
    }
}