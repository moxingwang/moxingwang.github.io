* $ 用来指定觉得引用
* & 用来做连接操作 如 =COUNTIF($H:$H,">"&L1)
* 整列选区   =COUNTIF($H:$H,"=2")

## 统计
* count计数多个条件使用countifs
eg: =COUNTIFS($H:$H,"="&L3,$G:$G,"<>未出组")


## 字符串操作
* 拼接
eg: =CONCATENATE("周期=",L1,"人数")


## sheet
* sheet引用
eg: =COUNTIFS(sheet0!$H:$H,"="&sheet0!L1,sheet0!$G:$G,"<>未出组")

## 条件搜索
```
=IF(  OR(IFERROR(FIND("（",A3),0)>0 ,IFERROR(FIND("(",A3),0)>0 ,IFERROR(FIND("国",A3),0)>0    )  ,0,A3)
```