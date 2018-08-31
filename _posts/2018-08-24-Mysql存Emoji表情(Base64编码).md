### Mysql的utf8编码为何存储不了Emoji表情？
* Emoji
> 首先的知道Emoji的编码方式,Emoji即绘文字，绘意指图形，文字则是图形的隐喻，可用来代表多种表情，如笑脸表示笑、蛋糕表示食物等。Unicode编码为E63E到E757。

* Mysql utf8和utf8mb4的区别 
> The character set named utf8 uses a maximum of three bytes per character and contains only BMP characters. As of MySQL 5.5.3, the utf8mb4 character set uses a maximum of four bytes per character supports supplementary characters:
  For a BMP character, utf8 and utf8mb4 have identical storage characteristics: same code values, same encoding, same length.
  For a supplementary character, utf8 cannot store the character at all, whereas utf8mb4 requires four bytes to store it. Because utf8 cannot store the character at all, you have no supplementary characters in utf8 columns and need not worry about converting characters or losing data when upgrading utf8 data from older versions of MySQL.
  上面是Mysql官网对utf8mb4的描述，总结来说为utf8的字符集每个字符最多使用三个字节，并且只包含BMP(Unicode基本多文种平面。关于Unicode的知识，推荐大家去看维基百科，理解17个平面是什么意思)字符。 从MySQL 5.5.3开始，utf8mb4字符集每个字符最多使用4个字节，支持补充字符。
  
  总结来说Emoji对应的Unicode编码不在utf8对应的Unicode分组平面内，无法直接用Mysql的utf8存储Emoji编码格式字符。

### 让Mysql支持Emoji表情的多种方式

1. 修改Mysql的表面为utf8_mb4
    ````
    这种做法，网上有很多教程这里不做细致说明。这种做法我是觉得很不方便，还需要修改数据库配置、重启；如果说是针对线上生产环境，重启数据库的代价自然是很高的，也是有风险的。
    ````


2. 服务端使用Base64转换Emoji编码
    * 服务端对Emoji表情进行Base64压缩
    ````
    String mysqlColumn = MimeUtility.encodeWord(emojiStr);
    ````

    * 对数据存储的Base64编码后的字符串逆向解码

    ````
    String emojiStr = MimeUtility.decodeWord(mysqlColumn);
    ````
    
    相对于这种使用Base64转换的方式，我们只需要在服务端做编码和解码即可。
    
### 总结

本人也是遇到数据库存储Emoji报错，为了解决这个问题，网上搜索资料总得了这几点，给出以下两篇文章，推荐大家去阅读，理解MySql在utf8下为什么不能存储Emoji表情，理解Unicode基本知识。

### 参考文章
* 1.[The utf8mb4 Character Set (4-Byte UTF-8 Unicode Encoding)](https://dev.mysql.com/doc/refman/5.5/en/charset-unicode-utf8mb4.html)
* 2.[Unicode](https://zh.wikipedia.org/wiki/Unicode)
* 3.[Emoji](https://zh.wikipedia.org/wiki/%E7%B9%AA%E6%96%87%E5%AD%97)
