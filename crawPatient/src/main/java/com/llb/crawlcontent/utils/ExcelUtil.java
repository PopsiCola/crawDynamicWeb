package com.llb.crawlcontent.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 读写excel工具类
 * @Author llb
 * Date on 2020/6/4
 */
public class ExcelUtil {


//    @Value("${excelUrl}")
    private String execlUrl = "E://workspace//";
    //创建一个Excel对象
    XSSFWorkbook wb = new XSSFWorkbook();
    /**
     * sheet1。创建汇总页
     */
    public List<String> writeToExcel(JSONArray jsonArray, String jessionId, String fileName, String ip) throws IOException, InterruptedException {


//        doc_id=0e95e02be6604cbd98ac48b654ec413f&
//        &typeNo=C0017

        //存放请求链接
        List<String> urls = new ArrayList<>();
        //存放类别名
        List<String> sheetList = new ArrayList<>();



        //创建表单Sheet对象，汇总
        XSSFSheet countSheet = wb.createSheet("汇总");
        //创建Row行对象
        //第一行为固定值
        XSSFRow row = countSheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("患者姓名");
        cell = row.createCell(1);
        cell.setCellValue("病人档案编号");
        cell = row.createCell(2);
        cell.setCellValue("就诊卡号");
        cell = row.createCell(3);
        cell.setCellValue("身份证号");
        cell = row.createCell(4);
        cell.setCellValue("文档编号");
        cell = row.createCell(5);
        cell.setCellValue("就诊医院");
        cell = row.createCell(6);
        cell.setCellValue("文档版本号");
        cell = row.createCell(7);
        cell.setCellValue("类别名称");
        cell = row.createCell(8);
        cell.setCellValue("就诊时间");
        //写入excel
        for(int i=0; i<jsonArray.size(); i++) {
            row = countSheet.createRow(i+1);
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            //几列是固定的，所以这里写死
            //创建列
            XSSFCell cell1 = row.createCell(0);
            cell1.setCellValue(jsonObject.getString("patient_name"));
            XSSFCell cell2 = row.createCell(1);
            cell2.setCellValue(jsonObject.getString("patient_id"));
            XSSFCell cell3 = row.createCell(2);
            cell3.setCellValue(jsonObject.getString("visit_id"));
            XSSFCell cell4 = row.createCell(3);
            cell4.setCellValue(jsonObject.getString("patient_id_card"));
            XSSFCell cell5 = row.createCell(4);
            cell5.setCellValue(jsonObject.getString("doc_type_code"));
            XSSFCell cell6 = row.createCell(5);
            cell6.setCellValue(jsonObject.getString("org_name"));
            XSSFCell cell7 = row.createCell(6);
            cell7.setCellValue(jsonObject.getString("doc_version"));
            XSSFCell cell8 = row.createCell(7);
            cell8.setCellValue(jsonObject.getString("doc_title"));
            XSSFCell cell9 = row.createCell(8);
            cell9.setCellValue(jsonObject.getString("time_stamp"));

//            String url = "http://192.168.1.122:83/sjfx/document/xmlShow?showFlag=1";
            String url = ip + "/sjfx/document/xmlShow?showFlag=1";
            url += "&doc_id=" + jsonObject.getString("doc_id") + "&typeNo=" + jsonObject.getString("doc_type_code");
            urls.add(url);

            sheetList.add(jsonObject.getString("doc_title"));
        }

        for (int i = 0; i < urls.size(); i++) {
            System.out.println(urls.get(i));
            new JsoupUtil().jsoupHtml(wb, urls.get(i), jessionId, sheetList.get(i));
        }

        //输出文件
        FileOutputStream output = null;
        try {
            File file = new File(execlUrl);
            if(!file.exists())
                file.mkdir();
            output = new FileOutputStream(execlUrl + fileName);
            wb.write(output);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }


    public void jsoupHtml(List<String> urls, String jsessionId, String sheetName) throws IOException, InterruptedException {


    }


}
