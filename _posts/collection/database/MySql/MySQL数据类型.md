# 字符
（N）N表示字符的个数
#### char
* 0-255字符之间

#### varchar
* 0-65536字符之间

# 整型
 (N)N表示最短显示宽度不影响存储
#### tinyint 1字节
#### smallint 2字节
#### mediumint 3字节
#### int 4字节
#### bigint 6字节

# 浮点型
 （N，M）这两个参数表示一共能存M位，其中小数点后占D位。
#### float 4个字节
* floa(N,M)
 N的最大值255，M的最大值10

#### double 8个字节
 * double(N,M)
 N的最大值255，M的最大值30 


# 参考
* [Mysql中数据类型括号中的数字代表的含义](https://www.cnblogs.com/loren-Yang/p/7512258.html)
* [MySQL如何选择float, double, decimal](http://yongxiong.leanote.com/post/mysql_float_double_decimal)
* [浮点数的二进制表示](http://www.ruanyifeng.com/blog/2010/06/ieee_floating-point_representation.html)