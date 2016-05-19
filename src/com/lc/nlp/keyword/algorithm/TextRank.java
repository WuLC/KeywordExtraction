/**
* Author: WuLC
* Date:   2016-05-18 23:10:12
* Last modified by:   WuLC
* Last Modified time: 2016-05-19 22:20:19
* Email: liangchaowu5@gmail.com
*/

package com.lc.nlp.keyword.algorithm;

/**
 * Function: extract keywords of document through TextRank algorithm
 * Input(String): target text that keywords will be extracted from 
 * Output(List<String>): keywords of the text
 */

import java.util.*;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;


public class TextRank
{
    static final float d = 0.85f;           //damping factor, default 0.85
    static final int max_iter = 200;        //max iteration times
    static final float min_diff = 0.0001f;  //condition to judge whether recurse or not
    private static  int nKeyword=5;         //number of keywords to extract,default 5
    private static  int coCurrenceWindow=3; //size of the co-occur window, default 3
    
    // change default parameters
    public static void setKeywordNumber(int sysKeywordNum)
    {
    	nKeyword = sysKeywordNum;
    }
 
    
    public static void setWindowSize(int window)
    {
    	coCurrenceWindow = window;
    }

    
    /**
     * extract keywords in terms of title and content of document
     * @param title(String): title of document
     * @param content(String): content of document
     * @param sysKeywordCount(int): number of keywords to extract,default 5
     * @param window(int): size of the co-occur window, default 3
     * @return (List<String>): list of keywords 
     */
    public static List<String> getKeyword(String title, String content)
    {
    	
    	Map<String, Float> score = TextRank.getWordScore(title, content); 
    	
        //rank keywords in terms of their score
        List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String, Float>>(score.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>()
        {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2)
            {
                return (o1.getValue() - o2.getValue() > 0 ? -1 : 1);
            }
        }
        );
        
        //System.out.println("After sorting: "+entryList);
        
        List<String> sysKeywordList=new ArrayList<String>();
        
        //List<String>  unmergedList=new ArrayList<String>();
        for (int i = 0; i < nKeyword; ++i){
            try{
        	//unmergedList.add(entryList.get(i).getKey());
        	sysKeywordList.add(entryList.get(i).getKey());
            }catch(IndexOutOfBoundsException e){
            	continue;
            }
        }

        System.out.print("window:"+coCurrenceWindow+"\nkeywordNum: "+nKeyword);
        return sysKeywordList;
    }
    
    
    /**
     * judge whether a word belongs to stop words
     * @param term(Term): word needed to be judged
     * @return(boolean):  if the word is a stop word,return false;otherwise return true    
     */
    public static boolean shouldInclude(Term term)
	    {
	        return CoreStopWordDictionary.shouldInclude(term);
	    }
 
    

    /**
     * return score of each word after TextRank algorithm
     * @param title(String): title of document
     * @param content(String): content of document
     * @param window(int): size of the co-occur window, default 3
     * @return (Map<String,Float>):  score of each word
     */
    public static Map<String,Float> getWordScore(String title, String content)
     {
    	 	
     	 //segment text into words
         List<Term> termList = HanLP.segment(title + content);
        
         int count=1;  //position of each word
         Map<String,Integer> wordPosition = new HashMap<String,Integer>();
         
         List<String> wordList=new ArrayList<String>();
         
         //filter stop words
         for (Term t : termList)
         {
             if (shouldInclude(t))
             {
                 wordList.add(t.word);
                 if(!wordPosition.containsKey(t.word))
                 {
                   wordPosition.put(t.word,count);
                   count++;
                 }
             }
         }
         //System.out.println("Keyword candidates:"+wordList);
         
         //generate word-graph in terms of size of co-occur window
         Map<String, Set<String>> words = new HashMap<String, Set<String>>();
         Queue<String> que = new LinkedList<String>();
         for (String w : wordList)
         {
             if (!words.containsKey(w))
             {
                 words.put(w, new HashSet<String>());
             }
             que.offer(w);    // insert into the end of the queue
             if (que.size() > coCurrenceWindow)
             {
                 que.poll();  // pop from the queue
             }

             for (String w1 : que)
             {
                 for (String w2 : que)
                 {
                     if (w1.equals(w2))
                     {
                         continue;
                     }

                     words.get(w1).add(w2);
                     words.get(w2).add(w1);
                 }
             }
         }       
         //System.out.println("word-graph:"+words); //each k,v represents all the words in v point to k 
         
         // iterate till recurse
         Map<String, Float> score = new HashMap<String, Float>();
         for (int i = 0; i < max_iter; ++i)
         {
             Map<String, Float> m = new HashMap<String, Float>();
             float max_diff = 0;
             for (Map.Entry<String, Set<String>> entry : words.entrySet())
             {
                 String key = entry.getKey();
                 Set<String> value = entry.getValue();
                 m.put(key, 1 - d);
                 for (String other : value)
                 {
                     int size = words.get(other).size();
                     if (key.equals(other) || size == 0) continue;
                     m.put(key, m.get(key) + d / size * (score.get(other) == null ? 0 : score.get(other))); 
                 }
                 
                 max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 1 : score.get(key))));
             }
             score = m;
             
             //exit once recurse
             if (max_diff <= min_diff) 
             	break;
         }
         return score;
     }
}

