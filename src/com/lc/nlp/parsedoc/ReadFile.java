/**
* Author: WuLC
* Date:   2016-05-19 22:49:42
* Last modified by:   WuLC
* Last Modified time: 2016-05-19 22:53:55
* Email: liangchaowu5@gmail.com
**************************************************************
*Function: given a text file of a certain type, read and load its' content as String
*Input(String): path of the file
*Output(String): text content of the file
*/
package com.lc.nlp.parsedoc;

import java.io.File;

public class ReadFile 
{
	public static String loadFile(String filePath)
	{
		File f = new File(filePath);
		if(!f.isFile())
		{
			System.out.println("The input "+filePath+" is not a file or the file doesn't exist");
			System.exit(0);
		}
		String content = new String();
		
/*define your own way of loading the your file's content, the following commented two lines 
is an example of loading the content of a XML file with the Class ParseXML in the same package*/
		//ParseXML parser = new ParseXML();
		//content = parser.parseXML(filePath, "content");
		
		return content;
	}

}
