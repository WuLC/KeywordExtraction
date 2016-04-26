/*
 * Dom4J 解释xml文件,传入文件名和tag，返回tag的内容
 */

package com.lc.parseXML;

import java.io.File;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Dom4JParseXML implements parseXML 
{

    public  String parseXML(String fileName,String tag) 
    {
        File inputXml = new File(fileName);
        SAXReader saxReader = new SAXReader();
        Document document=null;
        Element rootTag=null,subTag=null;
        boolean hasTag=false;
        try 
        {
        	//Document 接口表示整个 HTML 或 XML 文档。从概念上讲，它是文档树的根，并提供对文档数据的基本访问。 
            document = saxReader.read(inputXml);
            //Element 接口表示 HTML 或 XML 文档中的一个元素
            rootTag = document.getRootElement();
            //elementIterator()获取所有的子标签
            //Iterator是对 collection 进行迭代的迭代器。
            for (Iterator i = rootTag.elementIterator(); i.hasNext();) 
            {
                subTag = (Element) i.next();    
                if(subTag.getName().equals(tag))
                {
                	hasTag=true;
                	break;
                }
            }
        } 
        catch (DocumentException e) 
        {
            System.out.println(e.getMessage());
        }
        
        
        if(hasTag)
        	return subTag.getText();
        else 
        	return "not such a tag in the xml file";
    }
}
