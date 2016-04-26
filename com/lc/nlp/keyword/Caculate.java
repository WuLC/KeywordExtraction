/**
 * 功能：根据给定的系统的关键字的数组和人工提取出来的关键字，计算P值、R值和F值，以及这三个值的平均值
 */
package com.lc.nlp.keyword;

import java.util.*;
import java.io.*;
import java.text.DecimalFormat; //格式化输出，保留两位小数

public class Caculate {
	//存储所有文章的三个值
	private static List<Float> pValueList=new ArrayList<Float> ();
	private static List<Float> rValueList=new ArrayList<Float> ();
	private static List<Float> fValueList=new ArrayList<Float> ();
	private static int sysLen;
	private static DecimalFormat df=new DecimalFormat("0.00");//用来格式化输出，保留2位小数
	
	/**
	 * 传入由系统得到的关键字和人工得到的关键字，计算P值、R值、F值
	 * @param systemKeywords: 系统算法提取的关键字
	 * @param manualKeywords: 人工提取的关键字
	 */
	public static void  caculate(List<String> systemKeywords,String[] manualKeywords)
	{
	    sysLen=systemKeywords.size();
		int manLen=manualKeywords.length;
		//Caculate.printKeywords(systemKeywords,manualKeywords);
		int hit=0; //由系统得到的关键字有几个是在人工提取中出现的
		for(int i=0;i<sysLen;i++)
		{
			for(int j=0;j<manLen;j++)
			{
				if(systemKeywords.get(i).equals(manualKeywords[j]))
				{
					hit++;
					break;
				}	
			}
		}
		
		
		//Get Precision Value
		float pValue=(float)hit/sysLen;
		pValue*=100;//用百分号表示
        pValueList.add(pValue);
        
        
		//Get Recall Value
	    float rValue=(float)hit/manLen;
	    rValue*=100;
	    rValueList.add(rValue);

	    //Get F-Measure
	    float fValue;
	    if(rValue==0 || pValue == 0)
	    	fValue=0;
	    else
	    	fValue=2*rValue*pValue/(rValue+pValue);
	    
	    fValueList.add(fValue);
	    
	}

	/**
	 * 打印系统提取和人工提取的关键字
	 * @param systemKeywords: 系统算法提取的关键字
	 * @param manualKeywords: 人工提取的关键字
	 */
	public static void printKeywords(List<String> systemKeywords,String[] manualKeywords)
	{	
		//人工的
		System.out.print("人工提取的关键字:");
		int manLen=manualKeywords.length;
		for(int i=0;i<manLen;i++)
			System.out.print(manualKeywords[i]+' ');
		System.out.println(' ');
				
		//系统的
		System.out.print("系统提取的关键字:");
		int sysLen=systemKeywords.size();
		for(int i=0;i<sysLen;i++)
			System.out.print(systemKeywords.get(i)+" ");
		System.out.println(" ");	
	}

	/**
	 * 将这三个测评值的平均值写入文件
	 * @param fileName: 结果输出文件
	 */
	public static void writeAverageResult(String fileName)
	{
		float sum=0;
		int pLen=pValueList.size();
		for(int i=0;i<pLen;i++)
			sum+=pValueList.get(i);
		String pResult="P值的平均值为("+pLen+')'+df.format(sum/pLen)+"%\n";
		
		sum=0;
		int rLen=rValueList.size();
		for(int i=0;i<rLen;i++)
			sum+=rValueList.get(i);
		String rResult="R值的平均值为("+rLen+')'+df.format(sum/rLen)+"%\n";
		
		sum=0;
		int fLen=fValueList.size();
		for(int i=0;i<fLen;i++)
			sum+=fValueList.get(i);
		String fResult="F值的平均值为("+fLen+')'+df.format(sum/fLen)+"%\n";
		
		//将内容写入到文件中
		BufferedWriter  bw=null;
		try
		{
			bw=new BufferedWriter(new FileWriter(new File(fileName),true));//true参数为追加文件
			bw.write("\n系统提取关键字个数为"+sysLen+"\n"+pResult+rResult+fResult);		
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				bw.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
		//清空各个list
		pValueList.clear();
		rValueList.clear();
		fValueList.clear();
	}

}

