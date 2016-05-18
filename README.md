# Keyword Extraction in Java

Serveral implementations of keyword extraction,including TextRank,TF-IDF,TextRank along with TFTF-IDF

Segment words and filter stop words with [HanLP](https://github.com/hankcs/HanLP)

## 1. Algorithm

### 1.1 TextRank

With title and content of a document as input,return 5 keywords of the documents.For example

```java
String title = "关键词抽取";
String content = "关键词自动提取是一种识别有意义且具有代表性片段或词汇的自动化技术。关键词自动提取在文本挖掘域被称为关键词抽取，在计算语言学领域通常着眼于术语自动识别，在信息检索领域，就是指自动标引。";
System.out.println(TextRank.getKeyword(title, content));

// Output: `[自动, 领域, 关键词, 提取, 抽取]`
```


You can change the number of keywords and the size of co-occur window ,whose default values are 5 and 3,respectively.For example:
```java
TextRank.setKeywordNumber(6);
TextRank.setWindowSize(4);
String titleTest = "关键词抽取";
String contentTest = "关键词自动提取是一种识别有意义且具有代表性片段或词汇的自动化技术。关键词自动提取在文本挖掘域被称为关键词抽取，在计算语言学领域通常着眼于术语自动识别，在信息检索领域，就是指自动标引。";
System.out.println(TextRank.getKeyword(titleTest, contentTest));
// Output:`[自动, 关键词, 领域, 提取, 抽取, 自动识别]`
```

From the output you can see clearly the number of keywords has change due to `TextRank.setKeywordNumber(6);`,and the size of co-occur window is not visible in the result but will affect the resutl if you are aware of the principle of TextRank algorithm.


