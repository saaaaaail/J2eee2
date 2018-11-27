package sc.ustc.util;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class XmlUtil {

    private XmlUtil(){}

    private static XmlUtil xmlUtil = new XmlUtil();

    public static XmlUtil getInstance(){
        return xmlUtil;
    }

    public String analyzeAction(String fp, String actionName) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //通过文档构建器工厂获取一个文档构建器
            DocumentBuilder db = dbf.newDocumentBuilder();
            //通过文档通过文档构建器构建一个文档实例
            Document doc = db.parse(new File(fp));
            //获取action的节点
            NodeList actions = doc.getElementsByTagName("action");
            int actionsLength = actions.getLength();

            for (int i = 0; i < actionsLength; i++) {
                Node actionNode = actions.item(i);
                //通过Node对象的getAttributes()方法获取全的属性值
                NamedNodeMap actionNodeMap = actionNode.getAttributes();
                String nameString = actionNodeMap.getNamedItem("name").getNodeValue().toString();
                String methodString = actionNodeMap.getNamedItem("method").getNodeValue().toString();
                String classString = actionNodeMap.getNamedItem("class").getNodeValue().toString();
                String resultString;
                if (nameString.equals(actionName)) {
                    //get单个节点中的子节点list
                    NodeList actionChildNodes = actionNode.getChildNodes();

                    //使用反射调用目标方法，获取返回结果
                    Class clazz = Class.forName(classString);
                    Method method = clazz.getMethod(methodString);
                    Object obj = method.invoke(clazz.newInstance());
                    resultString = (String)obj;

                    for (int j = 0; j < actionChildNodes.getLength(); j++) {
                        if (actionChildNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            if (actionChildNodes.item(j).getNodeName().toString().equals("result")) {//result
                                NamedNodeMap resultMap = actionChildNodes.item(j).getAttributes();
                                String resultName = resultMap.getNamedItem("name").getNodeValue().toString();;
                                String resultType = resultMap.getNamedItem("type").getNodeValue().toString();;
                                String resultValue = resultMap.getNamedItem("value").getNodeValue().toString();;

                                if (resultName.equals(resultString)) {
                                    System.out.println(resultType + "," + resultValue);
                                    return resultType + "," + resultValue;
                                }
                            }
                        }
                    }
                    //result不匹配
                    return "no_result";
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //没有对应的action
        return "no_action";
    }
}
