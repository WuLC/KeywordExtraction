/**
 * ×ÛºÏTextRank¶àÍ¬ÏÖ´°¿ÚµÃµ½µÄ½á¹û
 */
package com.lc.nlp.keyword;

import java.util.*;

import com.lc.parseXML.Dom4JParseXML;
 
public class IntegrateTextRankWithMultiWindow 
{   
	/**
	 * ×ÛºÏ¶à¸ö´°¿ÚÌáÈ¡µÄ¹Ø¼ü´ÊµÄ½á¹û
	 * @param dirPath:ÐèÒªÌáÈ¡¹Ø¼ü´ÊµÄÎÄµµËùÔÚÄ¿Â¼
	 * @param minWindow:×îÐ¡µÄ´°¿Ú
	 * @param maxWindow: ×î´óµÄ´°¿Ú
	 * @param sysKeywordNum: ÌáÈ¡µÄ¹Ø¼ü´ÊµÄ¸öÊý
	 * @return Ã¿ÆªÎÄµµ¼°Æä´æÔÚµÄÈ±µã
	 */
	public static Map<String,List<String>> integrateMultiWindow(String dirPath, int minWindow, int maxWindow, int sysKeywordNum)
	{
		Map<String,List<String>> result = new HashMap<String,List<String>>();
		Map<String,Float> tempKeywordScore = new HashMap<String,Float>();
		
		List<String> fileList = ReadDir.readDirFileNames(dirPath);
		String title=null,content=null,key=null;
		Float value=null;
		Dom4JParseXML dom4j = new Dom4JParseXML();
		for(String filePath:fileList)
		{   
			Map<String,Float> allKeywordScore = new HashMap<String,Float>();
			title = dom4j.parseXML(filePath, "title");
			content = dom4j.parseXML(filePath, "content");
			for(int i=minWindow;i<=maxWindow;i++)
			{
				tempKeywordScore=TextRankExtract.getWordScore(title, content, i);
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
			//¶ÔÒ»ÆªÎÄµµµÄ¶à´°¿Ú½á¹û½øÐÐÅÅÐò
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
			for(int j=0;j<sysKeywordNum;j++)
			{
				fileKeywords.add(entryList.get(j).getKey());
			}
			result.put(filePath,fileKeywords);
		}
		return result;
	}

}
