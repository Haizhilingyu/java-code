

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;


/**
*【工程名】:  metaspolit
*【类文件名称】:  Dom4jUtil
*【类文件描述】:  dom4j工具类
*【历史信息】
*  版本     日期      作者/修改者     内容描述
* -----   -----     ----------   --------
* 2.0.0   19-3-4    yangkai      添加注释
*
*/
public class Dom4jUtil {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 将给定对象写入xml
     *
     * @param obj             泛型对象
     * @param entityPropertys 泛型对象的List集合
     * @param xmlFile  xml文件
     */
    public File writeXmlDocument(Object obj, List<Object> entityPropertys, File xmlFile) {
        try {
            XMLWriter writer = null;
            OutputFormat format = OutputFormat.createPrettyPrint();
            // 设置 xml 文件的编码格式c
            format.setEncoding("UTF-8");
            // 新建 xml 文件并新增内容
            Document document = DocumentHelper.createDocument();
            String rootname = obj.getClass().getSimpleName();
            Element root = document.addElement(rootname + "s");
            //获得实体类的所有属性
            Field[] properties = obj.getClass().getDeclaredFields();

            for (Object t : entityPropertys) {
                Element secondRoot = root.addElement(rootname);
                for (int i = 0; i < properties.length; i++) {
                    String name = properties[i].getName();
                    //反射get方法
                    Method meth = t.getClass().getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
                    //为二级节点添加属性，属性值为对应属性的值
                    Object text = meth.invoke(t);
                    String string = "";
                    if (text != null) {
                        string = text.toString();
                    }
                    secondRoot.addElement(name).addText(string);
                }
            }
            writer = new XMLWriter(new FileWriter(xmlFile), format);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            logger.error(e.fillInStackTrace().toString());
        }
        return xmlFile;
    }


    /**
     * 预处理，如果字符串中含有换行符，去掉
     *
     * @param xml
     * @return
     */
    private String getString(String xml) {
        if (xml.matches("\\n")) {
            String[] str = xml.split("\\n");
            String s = "";
            for (int i = 0; i < str.length; i++) {
                s += str[i].trim();
            }
            xml = s;
        }
        return xml;
    }

    /**
     * 修改myconfig.xml 配置文件<br/>只修改<b>constantConfig</b>节点下配置
     *
     * @param nodeName 节点名称
     * @param value    节点修改值
     * @return 1成功，0失败
     */
    public static int writeNode(String nodeName, String value) {
        //配置文件地址
        String xmlPath = System.getProperty("user.dir") + File.separator + "myconfig.xml";
        // 读取并解析XML文档
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(xmlPath);
            // 获取根节点
            Element rootElt = doc.getRootElement();
            //常量配置子节点
            Element constantEle = rootElt.element("constantConfig");
            //获取此节点的指定属性
            Element attrDate = constantEle.element(nodeName);
            attrDate.setText(value);//更改此属性值
        } catch (DocumentException e) {
            return 0;
        }
        try {
            saveDocument(doc, new File(xmlPath));
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    /**
     * 修改 xml 文件指定标签值
     *
     * @param nodeName 标签名称
     * @param value    标签值
     * @param xmlPath    xml文件
     * @return 1成功，0失败
     */
    public static int editXml(String nodeName, String value, String xmlPath) {
        // 读取并解析XML文档
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(xmlPath);
            // 获取根节点
            Element rootElt = doc.getRootElement();
            Element constantEle = rootElt.element("constantConfig");
            //获取此节点的指定属性
            Element attrDate = constantEle.element(nodeName);
            //更改此属性值
            attrDate.setText(value);
        } catch (DocumentException e) {
            e.printStackTrace();
            return 0;
        }
        try {
            saveDocument(doc, new File(xmlPath));
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    /**
     * 保存xml
     *
     * @param document
     * @param xmlFile
     * @throws IOException
     */
    private static void saveDocument(Document document, File xmlFile) throws IOException {
        //创建输出流
        Writer osWrite = new OutputStreamWriter(new FileOutputStream(xmlFile));
        //获取输出的指定格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        //设置编码 ，确保解析的xml为UTF-8格式
        format.setEncoding("UTF-8");
        //XMLWriter 指定输出文件以及格式
        XMLWriter writer = new XMLWriter(osWrite, format);
        //把document写入xmlFile指定的文件(可以为被解析的文件或者新创建的文件)
        writer.write(document);
        writer.flush();
        writer.close();
    }
}
