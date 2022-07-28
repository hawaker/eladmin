package me.zhengjie.modules.wkc.service.impl;

import me.zhengjie.modules.wkc.domain.WkcJob;
import me.zhengjie.modules.wkc.service.HandleJobType;
import me.zhengjie.modules.wkc.service.JobTypeHandler;
import me.zhengjie.modules.wkc.service.WkcJobService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@HandleJobType("BT")
public class BTJobTypeHandler implements JobTypeHandler {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  WkcJobService wkcJobService;
  @Autowired
  RestTemplate template;

  @Override
  public void handle(WkcJob job) {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("user-agent",
        "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
    httpHeaders.add("referer", job.getUrl());

    HttpEntity<String> httpEntity = new HttpEntity<>("", httpHeaders);
    ResponseEntity<String> response = template
        .exchange(job.getUrl(), HttpMethod.GET, httpEntity, String.class);
    if (response.getStatusCode() != HttpStatus.OK) {
      logger.error("解析任务{}失败", job.getUrl());
      return;
    }
    String html = response.getBody();
    Document doc = Jsoup.parse(html);
    Elements elements = doc.getElementsByTag("a");
    String magnet = null;
    for (Element element : elements) {
      String buf = element.attr("href");
      if (buf.startsWith("magnet:")) {
        magnet = buf;
        break;
      }
    }
    if (null == magnet) {
      logger.error("读取[{}]的磁链失败!", job.getUrl());
    }
    wkcJobService.createJob(job.getWkcUserId(), "magnet", magnet, job.getId());
    job.setStatus(1);
    wkcJobService.update(job);
  }
}