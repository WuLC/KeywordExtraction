# Keyword Extraction in Java

Several implementations of keyword extraction,including TextRank,TF-IDF,TextRank along with TFTF-IDF.Segment words and filter stop words relies on [HanLP](https://github.com/hankcs/HanLP)

The repository mainly consists of three parts:

**1. Algorithm**: implementation of serveral algorithms of keyword exraction,including TextRank,TF-IDF and methonds of integrate TextRank with TFTF-IDF

**2.Evaluate:**the method to evaluate the result of the algorithm,currently only the F1 Score if available

**3.Parse Documents:**methods provided to read the contens of the corpus used for test
 

## 1. Algorithm

### 1.1 TextRank

Source File: `TexkRank.java`

With title and content of a document as input,return 5 keywords of the documents.For example

```java
String title = "关键词抽取";
String content = "关键词自动提取是一种识别有意义且具有代表性片段或词汇的自动化技术。关键词自动提取在文本挖掘域被称为关键词抽取，在计算语言学领域通常着眼于术语自动识别，在信息检索领域，就是指自动标引。";
System.out.println(TextRank.getKeyword(title, content));

// Output: [自动, 领域, 关键词, 提取, 抽取]
```


You can change the number of keywords and the size of co-occur window ,whose default values are 5 and 3,respectively.For example:
```java
TextRank.setKeywordNumber(6);
TextRank.setWindowSize(4);
String title = "关键词抽取";
String content = "关键词自动提取是一种识别有意义且具有代表性片段或词汇的自动化技术。关键词自动提取在文本挖掘域被称为关键词抽取，在计算语言学领域通常着眼于术语自动识别，在信息检索领域，就是指自动标引。";
System.out.println(TextRank.getKeyword(title, content));
// Output:[自动, 关键词, 领域, 提取, 抽取, 自动识别]
```

From the output you can see clearly the number of keywords has change due to `TextRank.setKeywordNumber(6);`,and the size of co-occur window is not visible in the result but will affect the resutl if you are aware of the principle of TextRank algorithm.

### 1.2 TF-IDF

Source File: `TFIDF.java`

### 1.3 TextRank With Multiple Window

### 1.4 TextRank With TF-IDF

## 2. Evaluate

The Class `F1Score`  uses f1 score to evaluate the keywords extracted by the algorithm.You had got to take the keywords extracted by the algorithm and the keywords extracted manually as input.Sample code is like this:

```java
String title = "关键词抽取";
String content = "关键词自动提取是一种识别有意义且具有代表性片段或词汇的自动化技术。关键词自动提取在文本挖掘域被称为关键词抽取，在计算语言学领域通常着眼于术语自动识别，在信息检索领域，就是指自动标引。";
List<String> sysKeywords = TextRank.getKeyword(title, content);
String[] manualKeywords = {"关键词","自动提取"};
List<Float> result = F1Score.calculate(sysKeywords,manualKeywords);
System.out.println(result);
/*output
[20.0, 50.0, 28.57] represents precision = 20% recall=50% F1 =28.57%
*/
```

## 3. Parse Documents

### 3.1 ReadDir
`ReadDir` Class provides a method to find all the paths of files under a certain directory,including the sub-directories.For example

```java
String dirPath = "G:/corpusMini";
List<String> fileList =  ReadDir.readDirFileNames(dirPath);
for(String file : fileList)
    System.out.println(file);
```

and the output is like this:
```
G:/corpusMini/00001.xml
G:/corpusMini/00002.xml
G:/corpusMini/test/00003.xml
```

as you can see,the method can also read the files of subdirectory `test`,because of this,remember not to take  `/`  as the last character of dirPath

### 3.2 ReadFile

`ReadFile` class is designed to load the content of file of a certain type, **remember you had got to implement the method `loadFile` in trems of the type of your file.** The default method in it is to parse the XML files [here](https://github.com/iamxiatian/data/tree/master/sohu-dataset) ,and the code is like this

```java
/*remember to replace the following code to yours in terms of the type of your files*/
String filePath = "G:/corpusMini/00001.xml";
ParseXML parser = new ParseXML();
String content = parser.parseXML(filePath, "content");
```

