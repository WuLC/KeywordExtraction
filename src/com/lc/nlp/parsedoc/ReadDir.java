/**
* Author: WuLC
* Date:   2016-05-19 22:49:42
* Last modified by:   WuLC
* Last Modified time: 2016-05-19 22:53:09
* Email: liangchaowu5@gmail.com
************************************************************************************
* Function: read the paths of all the files under a directory,including the sub-directories of  it 
* Input(String): path of the directory,the last character of the path cannot be / due to  sub-directories
* Output(List<String>): paths of all files under the directory
*/

package com.lc.nlp.parsedoc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadDir 
{   
	/**
	 * read the paths of all the files under a directory,including the sub-directories of  it
	 * @param dirPath(String): path of the directory, remember the last character can't be /
	 * @return(List<String>): paths of all files under the directory
	 */
	public static List<String> readDirFileNames(String dirPath)
	{
		if (dirPath.equals(""))
		{
			System.out.println("The path of the directory can't be empty");
			System.exit(0);
		}
		
		else if(dirPath != null && (dirPath.substring(dirPath.length()-1)).equals("/"))
		{
			System.out.println("The last character of the path of the directory can't be /");
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
