/**
 * 通过TextRank提取文本关键词，输入String格式的文本 ，输出List<String>格式的关键字
 */
package com.lc.nlp.keyword;

import java.util.*;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;

public class TextRankExtract
{
    static final float d = 0.85f;    // 阻尼系数，一般取值为0.85
    static final int max_iter = 200; //最大迭代次数
    static final float min_diff = 0.0001f;
    private static  int nKeyword=5;     //系统提取的关键字的个数
    private static  int coCurrenceWindow=5; //同现窗口的大小
    public TextRankExtract()
    {
      //jdk bug : Exception in thread "main" java.lang.IllegalArgumentException: Comparison method violates its general contract!
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
    }
    
    /**
     * 根据文章内容，返回排序好的list格式的关键字
     * @param title           文章标题
     * @param content         文章内容
     * @param sysKeywordCount 需要提取的关键字个数
     * @param window          同现窗口的大小
     * @return                根据分数排好序的关键字列表
     */
    public static List<String> getKeyword(String title, String content,int sysKeywordCount,int window)
    {
    	nKeyword = sysKeywordCount;
    	Map<String, Float> score = TextRankExtract.getWordScore(title, content, window);      
        //对提取的关键字按照其得分来排序
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
        
        //System.out.println("对每个词得分排序结果"+entryList);
        
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

/**实验结果显示合并后效果更差
        //根据词的位置对从文中抽取出来的关键词进行合并        
        for(int i=0;i<unmergedList.size();i++){
        	String word1=unmergedList.get(i);
        	String  mergedKeyword=unmergedList.get(i);
        	for(int j=i;j<unmergedList.size();j++){
        		String word2=unmergedList.get(j);
        		if( (wordPosition.get(word1)-wordPosition.get(word2))== 1){
        			 mergedKeyword=word2+word1;
        			 unmergedList.remove(j);//删除已经合并的词的权重低的一个
        		}
        		else if( (wordPosition.get(word1)-wordPosition.get(word2))== -1){
        			mergedKeyword=word1+word2;
        			unmergedList.remove(j);
        		}
        		else 
        			continue;
        	}
        	sysKeywordList.add(mergedKeyword);
        }
        
 */
        
        return sysKeywordList;
    }
    
    
    /**
     * 判断某个词是否一个停止词
     * @param term  需要判断的词语
     * @return      词语是停止词返回false，否则返回true
     */
    public static boolean shouldInclude(Term term)
	    {
	        return CoreStopWordDictionary.shouldInclude(term);
	    }
 
    
    /**
     * 根据文章标题和内容，返回分词后通过TextRank算法得到的每个词的得分
     * @param title   文章标题
     * @param content 文章内容
     * @param window  同现窗口大小
     * @return  文章中所有词及其得分
     */
    public static Map<String,Float> getWordScore(String title, String content,int window)
     {
    	 coCurrenceWindow=window;
   	
     	//分词并判断词性
         List<Term> termList = HanLP.segment(title + content);            
         int count=1; //记录关键字的位置
         Map<String,Integer> wordPosition = new HashMap<String,Integer>();
         List<String> wordList=new ArrayList<String>();
         //过滤掉停止词
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
        //System.out.println("候选关键词列表:"+wordList);
         
         // 根据coCurrenceWindow大小连接有关系的词语
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
         
         //System.out.println("词图模型为:"+words); //每个词和与其有联系的词组成的hashmap，key被value中的值指向
         
         // 迭代直到收敛
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
             //收敛后就退出
             if (max_diff <= min_diff) 
             	break;
         }
         return score;
     }
}

	
