/**
* Author: WuLC
* Date:   2016-05-23 16:04:42
* Last modified by:   WuLC
* Last Modified time: 2016-05-23 16:28:19
* Email: liangchaowu5@gmail.com
***********************************************************************************
* Function: integrate results of TextRank algorithm with different size of co-occurance window
* Input: path of directory of the corpus
* Output: keywords of each document in the directory
*/

package com.lc.nlp.keyword.algorithm;

import java.util.*;

import com.lc.nlp.parsedoc.ReadDir;
import com.lc.nlp.parsedoc.ReadFile;

public class TextRankWithMultiWin 
{
	private static int keywordNum = 5;
	
	/**
	 * set the number of keywords to extract
	 * @param sysKeywordNum(int): number of keywords to extractt
	 */
	public static void setKeywordNumber(int sysKeywordNum)
	{
		keywordNum = sysKeywordNum;
	}
	
	
	/**
	 * integrate the results of TextRank algorithm  with different co-occurance  window
	 * @param dirPath(String): path of directory of the corpus
	 * @param minWindow(int): the minimum size of co-occurance window
	 * @param maxWindow(int): the maximum size of co-occurance window
	 * @param sysKeywordNum(int): number of keywords to extract
	 * @return(Map<String,List<String>>): keywords of each document of the directory
	 */
	public static Map<String,List<String>> integrateMultiWindow(String dirPath, int minWindow, int maxWindow)
	{
		Map<String,List<String>> result = new HashMap<String,List<String>>();
		Map<String,Float> tempKeywordScore = new HashMap<String,Float>();
		
		List<String> fileList = ReadDir.readDirFileNames(dirPath);
		String key=null;
		Float value=null;

		for(String filePath:fileList)
		{   
			Map<String,Float> allKeywordScore = new HashMap<String,Float>();
			// remember to modify the loadFile method in class ReadFile according the format of your file
			String content = ReadFile.loadFile(filePath); 
			for(int i=minWindow;i<=maxWindow;i++)
			{
				TextRank.setWindowSize(i); // set the size of co-occurance window
				tempKeywordScore=TextRank.getWordScore("",content);
				Iterator<Map.Entry<String, Float>> it = tempKeywordScore.entrySet().iterator();
				while (it.hasNext())
				{
					Map.Entry<String, Float> entry = it.next();
					key = entry.getKey();
					value = entry.getValue();
					if(allKeywordScore.containsKey(key))
						allKeywordScore.put(key, allKeywordScore.get(key)+value);
					else
						allKeywordScore.put(key, value);			
				}
			}
			// sort the result in terms of their score
			List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String,Float>>(allKeywordScore.entrySet());
			Collections.sort(entryList,new Comparator<Map.Entry<String, Float>>()
			{
				@Override
				public int compare(Map.Entry<String, Float> c1 , Map.Entry<String, Float> c2)
				{
					return c2.getValue().compareTo(c1.getValue());
				}
			});
		    
			List<String> fileKeywords = new ArrayList<String>();
			for(int j=0;j<keywordNum;j++)
			{
				fileKeywords.add(entryList.get(j).getKey());
			}
			result.put(filePath,fileKeywords);
		}
		return result;
	}

}
