package me.zhengjie.utils;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @date:2021/9/15 18:02
 * @author: caoqingyuan
 */
@Slf4j
public class TemplateUtils {
  private static Configuration cfg = new Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);


  public static String process(Map input, String templateStr) {
    String template = "content";
    StringTemplateLoader stringLoader=new StringTemplateLoader();
    stringLoader.removeTemplate(template);
    stringLoader.putTemplate(template, templateStr);
    cfg.setTemplateLoader(stringLoader);
    try {
      Template templateCon = cfg.getTemplate(template);
      StringWriter writer = new StringWriter();
      templateCon.process(input, writer);
      return writer.toString();
    } catch (IOException e) {
      log.error("模板格式化IO错误",e);
    } catch (TemplateException e) {
      log.error("模板格式化错误",e);
    }
    return null;
  }
}
