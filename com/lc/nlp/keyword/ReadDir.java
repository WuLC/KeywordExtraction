/**
 * 读取某一目录下的所有文件，返回所有文件的路径
 */

package com.lc.nlp.keyword;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadDir 
{
	public static List<String> readDirFileNames(String dirPath)
	{
		if (dirPath.equals(""))
		{
			System.out.println("文件夹路径不能为空");
			System.exit(0);
		}
		
		else if(dirPath != null && (dirPath.substring(dirPath.length()-1)).equals("/"))
		{
			System.out.println("文件夹路径最后的一个字符不能为/");
			System.exit(0);
		}
		
		File dirFile = new File(dirPath);
		String [] fileNameList=null;
		String tmp=null;
		List<String> fileList=new ArrayList<String>();
		List<String> subFileList=new ArrayList<String>();
		
		fileNameList=dirFile.list();
		  for(int i=0;i<fileNameList.length;i++)
		  {
			 tmp=dirPath+'/'+fileNameList[i];
			 File f1 = new File(tmp);
			 if (f1.isFile())
				 fileList.add(tmp);
			 else
				 subFileList = readDirFileNames(tmp);
			     fileList.addAll(subFileList);
		  }
		return fileList;		
	}

}
