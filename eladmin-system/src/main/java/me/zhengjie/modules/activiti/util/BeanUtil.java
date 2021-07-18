/**
 * Created by Jellyleo on 2019年12月16日 Copyright © 2019 jellyleo.com All rights reserved.
 */
package me.zhengjie.modules.activiti.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Slf4j
public class BeanUtil {

  private static ObjectMapper objectMapper = new ObjectMapper();

  static {
    // 指定策略不存在 json 字段不处理反射实体
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   *
   * 功能描述:
   *
   * @param map
   * @param beanType
   * @return
   * @throws Exception
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  public static <T> T mapToBean(Map<Object, Object> map, Class<T> beanType) throws Exception {
    T t = beanType.newInstance();
    PropertyDescriptor[] pds = Introspector.getBeanInfo(beanType, Object.class)
        .getPropertyDescriptors();
    for (PropertyDescriptor pd : pds) {
      for (Entry<?, ?> entry : map.entrySet()) {
        if (entry.getKey().equals(pd.getName())) {
          pd.getWriteMethod().invoke(t, entry.getValue());
        }
      }
    }
    return t;
  }

//  public static <T> T readValue(String content, TypeReference<?> typeReference) {
//    try {
//      if (typeReference == null) {
//        throw new Exception("typeReference不能为null");
//      }
//      if (org.apache.commons.lang3.StringUtils.isNotEmpty(content)) {
//        return objectMapper.readValue(content, typeReference);
//      }
//    } catch (Exception e) {
//      log.error("[ {} ] readValue error {}", typeReference, e.getMessage(), e);
//    }
//    return null;
//  }

  /**
   *
   * 功能描述:
   *
   * @param bean
   * @return
   * @throws Exception
   * @see [相关类/方法](可选)
   * @since [产品/模块版本](可选)
   */
  public static Map<String, Object> beanToMap(Object bean) throws Exception {

    Map<String, Object> map = new HashMap<>();
    BeanInfo info = Introspector.getBeanInfo(bean.getClass(), Object.class);
    PropertyDescriptor[] pds = info.getPropertyDescriptors();
    for (PropertyDescriptor pd : pds) {
      String key = pd.getName();
      Object value = pd.getReadMethod().invoke(bean);
      map.put(key, value);
    }
    return map;
  }


  public static Map<String, Object> beanToMapByName(Object object) {
    Map<String, Object> map = new HashMap<>();
    Method[] methods = object.getClass().getMethods();
    if (methods == null || methods.length <= 0) {
      return map;
    }
    List<String> notPut=Arrays.asList("processInstance","identityLinks","executions","createWithEmptyRelationshipCollections");
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      String methodName = method.getName();
      if (!methodName.startsWith("get") && !methodName.startsWith("is")&&method.getParameterCount()>0) {
        continue;
      }
      if (methodName.startsWith("get")){
        methodName=methodName.substring(3);
      }else if (methodName.startsWith("is")){
        methodName=methodName.substring(2);
      }
      methodName=methodName.substring(0,1).toLowerCase()+methodName.substring(1);
      if (notPut.contains(methodName)){
        continue;
      }
      try {
        map.put(methodName, method.invoke(object, new Object[0]));
      } catch (Exception e) {
        //log.error("fail on methodName:[{}]", methodName, e);
        continue;
      }
    }
    return map;
  }

  public static List<Map> beanListToMapList(List<?> objects) {
    if (CollectionUtils.isEmpty(objects)) {
      return null;
    }
    List<Map> list = new ArrayList<>();
    for (Object object : objects) {
      list.add(beanToMapByName(object));
    }
    return list;
  }
}