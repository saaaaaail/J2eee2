package sc.ustc.controller;

import sc.ustc.util.XmlUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class SimpleController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        resp.setCharacterEncoding("utf-8");

        PrintWriter out = resp.getWriter();
        String actionName = getActionName(req);
        out.println(actionName);

        //xml解析获得匹配结果
        String fp = this.getClass().getClassLoader().getResource("controller.xml").getPath();
        XmlUtil xmlUtil = XmlUtil.getInstance();
        String result = xmlUtil.analyzeAction(fp,actionName);
        out.println(result);

        //根据结果执行跳转
        dispatch(req,resp,result,out);

        /*
        out.println("<!DOCTYPE html>\r\n" +
                "<html>\r\n" +
                "<head>\r\n" +
                "<meta charset=\"UTF-8\">\r\n" +
                "<title>SimpleController</title>\r\n" +
                "</head>\r\n" +
                "<body>\r\n" +
                "欢迎使用SimpleController！！\r\n" +
                "</body>\r\n" +
                "</html>");*/

    }

    private String getActionName(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.substring(uri.lastIndexOf("/") + 1, uri.indexOf(".sc"));
    }

    private void dispatch(HttpServletRequest req, HttpServletResponse resp,String result,PrintWriter out){
        try {
            switch (result){
                case "no_action": out.println("不可识别的action请求");break;
                case "no_result": out.println("没有请求的资源");break;
                default: {
                    String[] tmp = result.split(",");
                    switch (tmp[0]){
                        case "forward": out.println(tmp[1]);req.getRequestDispatcher(tmp[1]).forward(req,resp);break;
                        case "redirect": out.println(tmp[1]);resp.sendRedirect(tmp[1]);break;
                    }
                }
            }
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
