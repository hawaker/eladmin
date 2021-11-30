package me.zhengjie.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Description:
 *
 * @date:2021/11/26 18:17
 * @author: caoqingyuan
 */
public class VsMateUtils {

  /**
   * 写入vsMeta数据
   *
   * @param title     标题
   * @param title1    标题1
   * @param shotTitle 短标题
   * @param date      日期
   * @param plot      简介
   * @param cla       类型
   * @param act       演员
   * @param direc     导演
   * @param level     级别
   * @param rate      评分
   * @param year      年份
   * @param factorys  制片厂
   * @throws IOException
   */
  public static void wirteVs(OutputStream output,String title, String title1, String shotTitle, String date,
      String plot, String[] cla, String[] act, String[] direc, String level, String rate,
      Integer year, String[] factorys)
      throws IOException {
    //头部
    output.write(0x08);
    //文件类型
    output.write(0x01);
    //标题1
    writeTagAndString(output, 0x12, title);
    //标题2
    writeTagAndString(output, 0x1A, title1);
    //副标题
    writeTagAndString(output, 0x22, shotTitle);
    //年份
    output.write(0x28);
    output.write(year);
    output.write(0x0F);
    //日期
    writeTagAndString(output, 0x32, date);

    //锁定
    output.write(0x38);
    output.write(0x01);

    //简介
    writeTagAndString(output, 0x42, plot);

    // 写入来源json
    writeTagAndString(output, 0x4A, "null");

    // 写入组信息
    writeGroup(output, act, direc, cla, factorys);

    // 写入级别
    writeTagAndString(output, 0x5A, level);

    // 写入评分
    output.write(0x60);
    //评级
    output.write((int) (Double.parseDouble(rate) * 10));

    output.flush(); // 把缓存区内容压入文件
    output.close();
  }

  public static void writeString(OutputStream output, String msg)
      throws IOException {
    byte[] bytestr = msg.getBytes(StandardCharsets.UTF_8);
    writeLength(output, bytestr.length);
    output.write(bytestr);
  }

  public static void writeLength(OutputStream output, int sizes) throws IOException {
    if (sizes > 128) {
      output.write(sizes % 128 + 128);
      sizes = sizes / 128;
    }
    output.write(sizes);
  }

  /**
   * 写入组数据
   *
   * @param output   流
   * @param acts     演员
   * @param direcs   导演
   * @param types    类型
   * @param factorys 厂商
   * @throws IOException
   */
  public static void writeGroup(OutputStream output, String[] acts, String[] direcs, String[] types,
      String[] factorys) throws IOException {
    int cals = 0;
    cals += calcLength(types);
    cals += calcLength(acts);
    cals += calcLength(direcs);
    cals += calcLength(factorys);
    if (cals > 0) {
      //写入组数据
      output.write(0x52);
      writeLength(output, cals);
    } else {
      return;
    }
    writeTagAndStrings(output, 0x0a, acts);
    writeTagAndStrings(output, 0x12, direcs);
    writeTagAndStrings(output, 0x1A, types);
    writeTagAndStrings(output, 0x22, factorys);
  }

  public static void writeTagAndString(OutputStream output, int tag, String c) throws IOException {
    if (c == null) {
      return;
    }
    output.write(tag);
    writeString(output, c);
  }

  public static void writeTagAndStrings(OutputStream output, int tag, String[] c)
      throws IOException {
    if (c == null || c.length == 0) {
      return;
    }
    for (String s : c) {
      writeTagAndString(output, tag, s);
    }
  }

  public static int calcLength(String[] s) {
    if (null == s) {
      return 0;
    }
    if (s.length == 0) {
      return 0;
    }
    int cals = 0;
    for (String c : s) {
      cals += 2 + sumStrAscii(c);
    }
    return cals;
  }

  public static void printByte(byte[] b) {
    for (int i = 0; i < b.length; i++) {
      System.out.println(String.format("%2X", b[i]));
    }

  }

  public static int sumStrAscii(String str) {
    byte[] bytestr = str.getBytes(StandardCharsets.UTF_8);
    return bytestr.length;
  }

  public static void main(String[] args) {
    //int length = sumStrAscii("胜多负少的法师打发打发的发送到发送到发送到发的发生阿斯顿发送到发生大大声的发生佛我;对比阿斯蒂芬煎熬地发哦度");
    //System.out.println(length);
    //System.out.println(byteAsciiToChar(length));
    //printByte("Z".getBytes(StandardCharsets.UTF_8));
    File file=new File("./job-021.avi.vsmeta");
    try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
      VsMateUtils.wirteVs(fileOutputStream,"abc","def","ghi","2021-11-22","哈哈",new String[]{"a","b","c"},new String[]{"d","e","f"},new String[]{"f","g","h"},null,"20",220,new String[]{"i","j","k"});
    }catch (FileNotFoundException e){
      e.printStackTrace();
    }catch (IOException e1){
      e1.printStackTrace();
    }
  }
}
