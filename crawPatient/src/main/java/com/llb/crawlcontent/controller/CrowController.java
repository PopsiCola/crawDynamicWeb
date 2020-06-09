package com.llb.crawlcontent.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.llb.crawlcontent.utils.ExcelUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author llb
 * Date on 2020/6/4
 */
@Controller
public class CrowController {

    PostMethod postMethod = new PostMethod();
    HttpClient httpClient = new HttpClient();

    @RequestMapping("crowToExcel")
    public ModelAndView crowToExcel(HttpServletRequest request,
                                           @RequestParam(required = false, defaultValue = "http://192.168.2.34:83") String url,
                                           String patient_id, String fileName, String jessionId, String patientName) throws IOException {
//        Map<String, Object> result = new HashMap<>();
        ModelAndView mv = new ModelAndView("index");

        if(patient_id == null || fileName == null || jessionId == null) {
//            result.put("msg", "参数不能为空！");
            mv.addObject("msg", "参数错误!");
            return mv;
        }

        String str = "/sjfx/document/list?pageSize=50&pageNum=1&orderByColumn=create_time&isAsc=desc&doc_type_code=&patient_id_card=&visit_id=&patient_name="+ URLEncoder.encode(patientName, "utf-8") +"&createTimeStart=&createTimeEnd=&timeStampStart=&timeStampEnd=&_=1591317562790&patient_id=" + patient_id;
//        str += "&patient_id=" + patient_id;
        str = url + str ;
        //获取cookie，用户防止登录拦截
//        Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
        //不知道为什么，为null会报空指针，回来再看
//        if(cookies != null) {
//            for(Cookie cookie : cookies){
//                cookieMap.put(cookie.getName(), cookie.getValue());
//                cookieStr += cookie.getName() + "=" + cookie.getValue() +";";
//            }
//        }


        //根据前端传过来的url爬取内容
        GetMethod getMethod = new GetMethod(str);

        //网站需要登录时，设置cookie即可'
        getMethod.setRequestHeader("cookie", "JSESSIONID=" + jessionId);
//        getMethod.setRequestHeader("cookie", "JSESSIONID=b17cdd9e-b707-4e81-950e-45925e7dfcdc");
        //设置请求头
        postMethod.setRequestHeader("user-agent", "postMethod.setRequestHeader(\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        String text = null;
        JSONObject jsonObject = null;
        try {
            //解决中文乱码

            httpClient.getParams().setParameters(new String[]{HttpMethodParams.HTTP_CONTENT_CHARSET}, "UTF-8");
            httpClient.executeMethod(getMethod);
            //获取到接口返回值
            text = getMethod.getResponseBodyAsString();
            jsonObject = JSONObject.parseObject(text);

            //解析JSON
            JSONArray jsonArray = jsonObject.getJSONArray("rows");

            //JsessionId失效
            if (jsonArray.size() == 0) {
               mv.addObject("msg", "查询数据为空！查看患者id是否正确以及JsessionId是否失效！");
               return mv;
            }

            //返回的链接，需要根据链接解析html
            List<String> urls = new ExcelUtil().writeToExcel(jsonArray, jessionId, fileName, url);

            /**逻辑：获得请求的连接，通过Jsoup来解析，保存页面**/

//            new ExcelUtil().jsoupHtml(urls, jsessionId);


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
//            result.put("msg", e);
            mv.addObject("msg", e);
            return mv;
        }
//        result.put("data", jsonObject);
//        result.put("code", 200);
//        result.put("msg", "爬取成功！");
        mv.addObject("msg", "爬取成功！");
        mv.addObject("jsessionId", jessionId);
        return mv;
    }

    @RequestMapping("index")
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("index");
        return mv;
    }
}
