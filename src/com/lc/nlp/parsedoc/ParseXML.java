/**
* Author: WuLC
* Date:   2016-05-19 22:49:42
* Last modified by:   WuLC
* Last Modified time: 2016-05-19 22:53:31
* Email: liangchaowu5@gmail.com
**************************************************************
* Function: parse XML file in terms of file and certain tag
* Input: file path of XML file and a tag 
* Output: content of the tag
*/

package com.lc.nlp.parsedoc;

import java.io.File;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ParseXML 
{   
	/**
	 * return the content of a XML file of a certain tag
     * the tag should be second level,or you can modify the code to change to any level
	 * @param fileName(String): file path of the XML file
	 * @param tag(String): a second-level tag of the XML file
	 * @return(String): content of tag of the file
	 */
    public String parseXML(String fileName,String tag) 
    {
        File inputXml = new File(fileName);
        SAXReader saxReader = new SAXReader();
        Document document=null;
        Element rootTag=null,subTag=null;
        boolean hasTag=false;
        try 
        {
            document = saxReader.read(inputXml);
            rootTag = document.getRootElement();
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

