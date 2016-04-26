package com.lc.nlp.keyword;

import java.io.*;
import java.util.*;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.lc.parseXML.Dom4JParseXML;

public class TFIDFExtract 
{
	
	/**
     * 传入文件路径，得到文件中每个词的TF值
     * @param filePath: 文件的路径
     * @return  表示每个词的TF值的一个HashMap<String, Float>
     */
    public static HashMap<String, Float> getTF(String filePath)
    {    
    	// 对文件进行分词操作
    	List<Term> terms=new ArrayList<Term>();
        ArrayList<String> words = new ArrayList<String>();
        Dom4JParseXML dom4j = new Dom4JParseXML();
        String text=null,title=null;
        try
        {   
        	title = dom4j.parseXML(filePath,"title");
            text = dom4j.parseXML(filePath,"content");
        }
        catch(Exception e)
        {
        	System.out.println(filePath);
        }
        terms=HanLP.segment(title+text);
        for(Term t:terms)
        {
        	if(TFIDFExtract.shouldInclude(t))
        	{
        		words.add(t.word);
        	}      		
        }
        
        //统计分词后的list，得到每个词的TF值
    	 HashMap<String, Integer> wordCount = new HashMap<String, Integer>();
    	 HashMap<String, Float> TFValues = new HashMap<String, Float>();
    	 for(String word : words)
         {
             if(wordCount.get(word) == null)
             {
            	 wordCount.put(word, 1);
             }
             else
             {
            	 wordCount.put(word, wordCount.get(word) + 1);
             }
         }
    	 
         int wordLen = words.size();
         //遍历HashMap一种常用方法
         Iterator<Map.Entry<String, Integer>> iter = wordCount.entrySet().iterator(); 
         while(iter.hasNext())
         {
             Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)iter.next();
             TFValues.put(entry.getKey().toString(), Float.parseFloat(entry.getValue().toString()) / wordLen);
           //System.out.println(entry.getKey().toString() + " = "+  Float.parseFloat(entry.getValue().toString()) / wordLen);
         }
         return TFValues;
     } 
  
    
    /**
     * 判断一个词是否应该作为候选词
     * @param term: 待判断的词
     * @return  boolean
     */
    public static boolean shouldInclude(Term term)
    {
        return CoreStopWordDictionary.shouldInclude(term);
    }
      
    
    /**
     * 传入目录的路径,得到该目录下每个文件中每个词的TF值
     * @param dirc: 文件所在目录
     * @return: 表示每个文件中每个词的TF值的一个HashMap<String,HashMap<String, Float>>
     * @throws IOException
     */
    public static HashMap<String,HashMap<String, Float>> tfForDir(String dirPath) 
    {
        HashMap<String, HashMap<String, Float>> allTF = new HashMap<String, HashMap<String, Float>>();
        List<String> filelist = ReadDir.readDirFileNames(dirPath);
        
        for(String file : filelist)
        {
            HashMap<String, Float> dict = new HashMap<String, Float>();
            dict = TFIDFExtract.getTF(file);
            allTF.put(file, dict);
        }
        return allTF;
    }

    
    /**
     * 传入目录路径，得到这个路径下所有文件所有词的idf值
     * @param dirPath: 文件所在目录
     * @return 包含各个词的IDF值的一个HashMap<String, Float>
     */
    public static HashMap<String, Float> idfForDir(String dirPath)
    {
    	List<String> fileList = new ArrayList<String>();
    	fileList = ReadDir.readDirFileNames(dirPath);
    	int docNum = fileList.size(); //得到文章总数
    	
    	Dom4JParseXML dom4j = new Dom4JParseXML();
        Map<String, Set<String>> passageWords = new HashMap<String, Set<String>>(); //存储每篇文章出现的不重复的单词       
        // 得到每篇文章的所有不重复单词
        for(String filePath:fileList)
        {   
        	List<Term> terms=new ArrayList<Term>();
            Set<String> words = new HashSet<String>();
        	String text=null,title=null;
            try
            {
                title = dom4j.parseXML(filePath,"title");
            	text = dom4j.parseXML(filePath,"content");
            }
            catch(Exception e)
            {
            	System.out.println(filePath);
            }
            terms=HanLP.segment(title+text);
            for(Term t:terms)
            {
            	if(TFIDFExtract.shouldInclude(t))
            	{
            		words.add(t.word);
            	}      		
            }
            passageWords.put(filePath, words);
        }
        
        // 计算每个词的idf值
        HashMap<String, Integer> wordPassageNum = new HashMap<String, Integer>();//存储单词及其出现的文章数量
        for(String filePath : fileList)
        {
            Set<String> wordSet = new HashSet<String>();
            wordSet = passageWords.get(filePath);
            for(String word:wordSet)
            {           	
                if(wordPassageNum.get(word) == null)
                	wordPassageNum.put(word,1);
                else             
                	wordPassageNum.put(word, wordPassageNum.get(word) + 1);           
            }
        }
        
        HashMap<String, Float> wordIDF = new HashMap<String, Float>(); // 存储每个词的IDF值
        Iterator<Map.Entry<String, Integer>> iter_dict = wordPassageNum.entrySet().iterator();
        while(iter_dict.hasNext())
        {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)iter_dict.next();
            float value = (float)Math.log( docNum / (Float.parseFloat(entry.getValue().toString())) );
            wordIDF.put(entry.getKey().toString(), value);
            //System.out.println(entry.getKey().toString() + "=" +value);
        }
        return wordIDF;
    }

    
    /**
     * 传入目录的路径，得到该文件下每个文件中每个词的TF-IDF值
     * @param dirPath:文件所在的目录
     */
    public static Map<String, HashMap<String, Float>> getDirTFIDF(String dirPath)
    {
        HashMap<String, HashMap<String, Float>> dirFilesTF = new HashMap<String, HashMap<String, Float>>(); //目录下各个文件的各个词的TF值
        HashMap<String, Float> dirFilesIDF = new HashMap<String, Float>();//目录下所有词的IDF值
        
        dirFilesTF = TFIDFExtract.tfForDir(dirPath);
        dirFilesIDF = TFIDFExtract.idfForDir(dirPath);
        
        Map<String, HashMap<String, Float>> dirFilesTFIDF = new HashMap<String, HashMap<String, Float>>();//目录下所有词的TFIDF值
        Map<String,Float> singlePassageWord= new HashMap<String,Float>();
        List<String> fileList = new ArrayList<String>();
        fileList = ReadDir.readDirFileNames(dirPath);
        for (String filePath: fileList)
        {
        	HashMap<String,Float> temp= new HashMap<String,Float>();
        	singlePassageWord = dirFilesTF.get(filePath);
        	Iterator<Map.Entry<String, Float>> it = singlePassageWord.entrySet().iterator();
        	while(it.hasNext())
        	{
        		Map.Entry<String, Float> entry = it.next();
        		String word = entry.getKey();
        		Float TFIDF = entry.getValue()*dirFilesIDF.get(word);
        		temp.put(word, TFIDF);
        	}
        	dirFilesTFIDF.put(filePath, temp);
        }
        return dirFilesTFIDF;
    }
 
    
    /**
     * 传入文件夹路径和提取的关键字的个数，返回文件夹中每个文件提取的关键字 
     * @param dirPath: 文件夹路径
     * @param keywordNum: 需要提取的关键字的个数
     * @return 包含每篇文章的关键字的一个Map<String,List<String>>
     */
    public static Map<String,List<String>> getKeywords(String dirPath, int keywordNum)
    {
    	List<String> fileList = new ArrayList<String>();
    	fileList = ReadDir.readDirFileNames(dirPath);
    	
    	Map<String, HashMap<String, Float>> dirTFIDF = new HashMap<String, HashMap<String, Float>>(); //得到目录总每个文件中每个词的TFIDF值 
    	dirTFIDF = TFIDFExtract.getDirTFIDF(dirPath);
    	
    	Map<String,List<String>> keywordsForDir = new HashMap<String,List<String>>(); //存储目录中每个文件提取的关键字
    	for (String file:fileList)
    	{
    		Map<String,Float> singlePassageTFIDF= new HashMap<String,Float>();
    		singlePassageTFIDF = dirTFIDF.get(file);
    		
    		//对得到的TFIDF值从大到小排序
	        List<Map.Entry<String,Float>> entryList=new ArrayList<Map.Entry<String,Float>>(singlePassageTFIDF.entrySet());
	        
	
	        Collections.sort(entryList,new Comparator<Map.Entry<String,Float>>()
	        {
	        	@Override
	        	public int compare(Map.Entry<String,Float> c1,Map.Entry<String,Float> c2)
	        	{
	        		return c2.getValue().compareTo(c1.getValue()); //降序排序。如果要升序排序改成c1.getValue().compareTo(c2.getValue())	        		
	        	}
	        }
	        );
	        	        
	        //获取前n个关键字List
            List<String> systemKeywordList=new ArrayList<String>();
            for(int k=0;k<keywordNum;k++)
            {
            	try
            	{
            	systemKeywordList.add(entryList.get(k).getKey());
            	}
            	catch(IndexOutOfBoundsException e)
            	{
            		continue;
            	}
            }
            
            keywordsForDir.put(file, systemKeywordList);
        }
        return keywordsForDir;
    }
           
}
	


