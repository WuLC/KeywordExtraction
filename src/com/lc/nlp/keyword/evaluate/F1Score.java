/**
* Author: WuLC
* Date:   2016-05-20 22:56:12
* Last modified by:   WuLC
* Last Modified time: 2016-05-20 23:01:16
* Email: liangchaowu5@gmail.com
***************************************************************************
* Function: calculate precision, recall and f1 score in terms of the keywords extracted by the algorithm and manually
* Input: keywords extracted by the algorithm and manually
* Output: precision, recall, f1 score 
*/

package com.lc.nlp.keyword.evaluate;

import java.util.*;
import java.text.DecimalFormat; 

public class F1Score 
{
	private static DecimalFormat df = new DecimalFormat("0.00");//format the output to reserve two decimal places 
	
	/**
	 * calculate the precision value, recall value and f1 score in terms of the keywords extracted by the algorithm and manually
	 * @param systemKeywords(List<String>): keywords extracted by the algorithm
	 * @param manualKeywords(String[]): keywords extracted manually
	 * return (List<Float>): precision, recall, f1 score
	 */
	public static List<Float> calculate(List<String> systemKeywords,String[] manualKeywords)
	{
	    int sysLen=systemKeywords.size();
		int manLen=manualKeywords.length;
		//Caculate.printKeywords(systemKeywords,manualKeywords);
		int hit=0; 
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
		pValue*=100; //represent in the form of %
         
		//Get Recall Value
	    float rValue=(float)hit/manLen;
	    rValue*=100;

	    //Get F-Measure
	    float fValue;
	    if(rValue==0 || pValue == 0)
	    	fValue=0;
	    else
	    	fValue=2*rValue*pValue/(rValue+pValue);
	    
	   List<Float> result = new ArrayList<Float>();
	   result.add(Float.parseFloat(df.format(pValue)));
	   result.add(Float.parseFloat(df.format(rValue)));
	   result.add(Float.parseFloat(df.format(fValue)));
	   return result;
	}
}
