/*
 * 将TextRank和TF-IDF进行综合，有下面两种方案：
 * 1.先通过对每篇文章提取20个关键字，再对这些关键字进行TFIDF
 * 2.同时通过tf-idf和TextRank进行关键字提取，选取相同的关键字
 */
package com.lc.nlp.keyword;

import java.util.*;

import com.lc.parseXML.Dom4JParseXML;


public class TextRankWithTFIDF 
{
	/**
	 * 对TextRank得出的关键字再乘上每个词的IDF值，然后再排序返回前n个
	 * @param dirPath:需要提取关键字的文档所在目录
	 * @param sysKeywords: 需要提取的关键字的个数
	 * @return: dirPath目录下每个文档的关键字
	 */
	public static Map<String,List<String>> textRankThenTFIDF(String dirPath, int sysKeywordNum)
	{
		Map<String,List<String>> result = new HashMap<String,List<String>>();
		TextRankExtract tr = new TextRankExtract();
		Dom4JParseXML dom4j = new Dom4JParseXML();
		Map<String,Float> idfForDir = TFIDFExtract.idfForDir(dirPath);
		List<String> fileList = ReadDir.readDirFileNames(dirPath);
		String title=null,content= null;
		
		for(String file:fileList)
		{
			title = dom4j.parseXML(file, "title");
			content = dom4j.parseXML(file, "content");
			Map<String,Float> trKeywords = tr.getWordScore(title, content, 5);
			Iterator<Map.Entry<String, Float>> it = trKeywords.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry<String,Float> temp =it.next();
				String key = temp.getKey();
				trKeywords.put(key, temp.getValue()*idfForDir.get(key));
			}
			//根据得分从小到大排序，并提取关键字
			List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String,Float>>(trKeywords.entrySet());
			Collections.sort(entryList,
					new Comparator<Map.Entry<String, Float>>()
				{
					public int compare(Map.Entry<String, Float> c1, Map.Entry<String, Float> c2)
					{
						return c2.getValue().compareTo(c1.getValue());
					}
					
				}
			);
			
			List<String> temp = new ArrayList<String>();
			for (int i=0;i<sysKeywordNum;i++)
			{
				temp.add(entryList.get(i).getKey());
			}
		result.put(file, temp);
		}
		return result;
	}
   
	/**
	 * 对TextRank和TFIDF提取的关键字进行投票，选出同时出现的，其余的从TFIDF中选
	 * @param dirPath: 需要提取关键字的文档的目录
	 * @param sysKeywords: 需要提取的关键字的个数
	 * @return: dirPath目录下每个文档的关键字
	 */
	public static Map<String,List<String>> textRankWithTFIDF(String dirPath, int sysKeywords)
	{
		Map<String, List<String>> result = new HashMap<String,List<String>>();
		List<String> fileList = ReadDir.readDirFileNames(dirPath);
		Dom4JParseXML dom4j = new Dom4JParseXML();
		int keywordCandidateNum = 10;
		Map<String,List<String>> tfidfKeywordsForDir = TFIDFExtract.getKeywords(dirPath, keywordCandidateNum);
		List<String> trKeyword = new ArrayList<String>();
		List<String> tfidfKeyword = new ArrayList<String>();
		String title= null,content = null;
		for(String file:fileList)
		{
			title = dom4j.parseXML(file, "title");
			content = dom4j.parseXML(file, "content");
			trKeyword = TextRankExtract.getKeyword(title, content, keywordCandidateNum, 4);
			tfidfKeyword = tfidfKeywordsForDir.get(file);
			
			List<String> temp = new ArrayList<String>();
			for(String keyword:tfidfKeyword)
			{
				if (trKeyword.contains(keyword))
					temp.add(keyword);
				if (temp.size()==sysKeywords)
					break;
			}
			if (temp.size()==sysKeywords)
				result.put(file,temp);
			else
				for(String keyword:tfidfKeyword)
				{
					if (!temp.contains(keyword))
						temp.add(keyword);
				    if (temp.size()==sysKeywords)
				    	result.put(file, temp);
				}
		}
		return result;
	}

}
