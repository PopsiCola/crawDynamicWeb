package com.llb.crawlcontent.utils;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析页面
 * @Author llb
 * Date on 2020/6/5
 */
public class JsoupUtil {

    private String execlUrl = "E://workspace//1.xlsx";

    //@TODO:根据传过来的链接，解析页面依次保存到excel中
    public void jsoupHtml(XSSFWorkbook wb, String url, String jsessionId, String sheetName) throws IOException, InterruptedException {

        List<String> dataList = new ArrayList<>();

        //创建一个Excel对象

        //创建表单Sheet对象，汇总
        //默认2行2列开始
        int rowInt = 2;
        int columnInt = 2;
        XSSFSheet countSheet = wb.createSheet(sheetName);

        System.setProperty("webdriver.chrome.driver", "E:\\chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("headless");
        ChromeDriver driver = new ChromeDriver(chromeOptions);
//        driver.manage().addCookie(new Cookie("JSESSIONID", "b17cdd9e-b707-4e81-950e-45925e7dfcdc"));
//        System.out.println(url);
//        System.out.println(url+ "&JSESSIONID=" + jsessionId);
        driver.get(url+ "&JSESSIONID=" + jsessionId);

        // 休眠1s,为了让js执行完
        Thread.sleep(1000l);
        // 网页源码
        String source = driver.getPageSource();
        driver.close();

        Document doc = null;
        try {

            //使用xml的方式解析获取到jsoup的document对象
            doc = Jsoup.parse(source);
//            System.out.println(doc);
//            //先获得的是整个页面的html标签页面
//            doc= Jsoup.connect(url).maxBodySize(0).timeout(100000).cookie("JSESSIONID", jsessionId).userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15").ignoreContentType(true).get();
//            System.out.println(doc);
            //可以通过元素的标签获取html中的特定元素
            Elements allData = doc.select("div[class=\"layui-tab-item layui-show\"]");
            /**************************解析文本*************************/
            //解析患者基本信息内容
            Elements patientElement = allData.select("table[style=\"border: 0px\"]");
//            Elements patientElement = doc.select("\"table[style=\\\"border: 0px\\\"]\"");
            for (Element element : patientElement) {
                Elements remove = element.getElementsByTag("tbody").remove();
                //                System.out.println("90行=========================" + element.text().trim());
                String[] split = element.text().split(" ");
//                Elements brElement = element.select("br");
                for (int i = 0; i < split.length; i++) {
                    //避免空行
                    if("".equals(split[i])) {
                        //                        System.out.println(split[i]);
                        continue;
                    }
                    //每次进来都需要重置
                    columnInt = 2;
                    //创建Row行对象
                    //第一行为固定值
                    XSSFRow row = countSheet.createRow(rowInt++);
                    XSSFCell cell = row.createCell(columnInt);

                    cell.setCellValue(split[i]);
                    //                    dataList.add(split[i]);
                    System.out.println(split[i]);
                    columnInt++;
                }
                Elements trElement = remove.select("tr");
                for (Element element1 : trElement) {
                    //每次进来都需要重置
                    columnInt = 2;
                    XSSFRow row = countSheet.createRow(rowInt);
                    XSSFCell cell = row.createCell(columnInt);
                    //解析表格td
                    Elements tdElement = element1.select("td");
                    if(tdElement.size() != 0) {
                        columnInt = 2;
                        for (Element td : tdElement) {
                            //TODO:表格分割
                            //创建Row行对象
                            //第一行为固定值
                            //                            row = countSheet.createRow(rowInt);
                            cell = row.createCell(columnInt);
                            if("".equals(td.text())) {
                                columnInt += 2;
                                continue;
                            }
                            System.out.print(td.text() + " ");
                            cell.setCellValue(td.text());
                            columnInt += 2;
                        }
                        System.out.println();
                        rowInt++;
                    }
                    //                    System.out.println(element1.text());
                }


                //表格再次需要解析
                //                Elements tbody = element.select("tbody");
                //                if(tbody != null) {
                //                    System.out.println(tbody);
                //                }

            }

            //V1.0版本，表格没有分割
//            Elements patientElement = allData.select("table[style=\"border: 0px\"]");
//            String patientInfo = patientElement.text();
//            String[] datas = patientInfo.split(" ");
//            for (int i = 0; i < datas.length; i++) {
//                //每次进来都需要重置
//                columnInt = 2;
//                //创建Row行对象
//                //第一行为固定值
//                XSSFRow row = countSheet.createRow(rowInt++);
//                XSSFCell cell = row.createCell(columnInt);
//
//                cell.setCellValue(datas[i]);
//                columnInt++ ;
//                System.out.println(datas[i]);
////                dataList.add(datas[i]);
//            }

            //章节内容有表格，需要删除表格，表格格式需要重新定义
            //解析章节
                Elements titleElement = doc.select("div[class=\"list\"]");
            for (Element element : titleElement) {
                Elements remove = element.getElementsByTag("tbody").remove();
//                System.out.println("90行=========================" + element.text().trim());
                String[] split = element.text().split(" ");
                for (int i = 0; i < split.length; i++) {
                    //避免空行
                    if("".equals(split[i])) {
//                        System.out.println(split[i]);
                        continue;
                    }
                    //每次进来都需要重置
                    columnInt = 2;
                    //创建Row行对象
                    //第一行为固定值
                    XSSFRow row = countSheet.createRow(rowInt++);
                    XSSFCell cell = row.createCell(columnInt);

                    cell.setCellValue(split[i]);
//                    dataList.add(split[i]);
                    System.out.println(split[i]);
                    columnInt++;
                }
                Elements trElement = remove.select("tr");
                for (Element element1 : trElement) {
                    //每次进来都需要重置
                    columnInt = 2;
                    XSSFRow row = countSheet.createRow(rowInt);
                    XSSFCell cell = row.createCell(columnInt);
                    //解析表格th
                    Elements thElement = element1.select("th");
                    if(thElement.size() != 0) {
                        columnInt = 2;
                        for (Element th : thElement) {
                            //TODO:表格分割

                            //创建Row行对象
                            //第一行为固定值
//                            row = countSheet.createRow(rowInt);
                            cell = row.createCell(columnInt);
                            if("".equals(th.text())) {
                                columnInt++;
                                continue;
                            }
                            cell.setCellValue(th.text());
                            System.out.print(th.text() + " ");
                            columnInt++;
                        }
                        System.out.println();
                        rowInt++;
                    }
                    //解析表格td
                    Elements tdElement = element1.select("td");
                    if(tdElement.size() != 0) {
                        columnInt = 2;
                        for (Element td : tdElement) {
                            //TODO:表格分割
                            //创建Row行对象
                            //第一行为固定值
//                            row = countSheet.createRow(rowInt);
                            cell = row.createCell(columnInt);
                            if("".equals(td.text())) {
                                columnInt++;
                                continue;
                            }
                            System.out.print(td.text() + " ");
                            cell.setCellValue(td.text());
                            columnInt++;
                        }
                        System.out.println();
                        rowInt++;
                    }
                    //                    System.out.println(element1.text());
                }


                //表格再次需要解析
//                Elements tbody = element.select("tbody");
//                if(tbody != null) {
//                    System.out.println(tbody);
//                }

            }

            //结尾  医师以及日期
            Elements doctorElement = allData.select("span[id=\"auName\"]");
            //创建Row行对象
            //第一行为固定值
            XSSFRow row = countSheet.createRow(rowInt++);
            XSSFCell cell = row.createCell(4);
            cell.setCellValue("医师：");
//            System.out.println("医师：");
//            System.out.println(doctorElement.text());
            row = countSheet.createRow(rowInt++);
            cell = row.createCell(4);
            cell.setCellValue(doctorElement.text());
            Elements dateElement = allData.select("span[id=\"auTime\"]");
            row = countSheet.createRow(rowInt++);
            cell = row.createCell(4);
            cell.setCellValue("时间：");
            row = countSheet.createRow(rowInt++);
            cell = row.createCell(4);
            cell.setCellValue(dateElement.text());

//            //输出文件
//            FileOutputStream output = null;
//            try {
//                output = new FileOutputStream(execlUrl);
//                wb.write(output);
//                output.flush();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.out.println("时间：");
//            System.out.println(dateElement.text());

            //            System.out.println(title);
//            String t = allData.text();
//            System.out.println(t);
            //可以通过元素的id获取html中的特定元素

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        XSSFWorkbook wb = new XSSFWorkbook();
        new JsoupUtil().jsoupHtml(wb,"http://192.168.1.122:83/sjfx/document/xmlShow?doc_id=57eae89dd9d240a78f5dcfe2cffb7923&showFlag=1&typeNo=C0001",
                "788d7ac1-02d4-40ad-a915-eeb1dd20750a", "33");
    }
}
