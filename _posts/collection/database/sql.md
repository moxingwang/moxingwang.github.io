## order by 语句对null字段的默认排序
参考：[order by 语句对null字段的默认排序](http://blog.csdn.net/oxcow/article/details/6554168)
````aidl
【Oracle 结论】 
order by colum asc 时，null默认被放在最后
order by colum desc 时，null默认被放在最前
nulls first 时，强制null放在最前，不为null的按声明顺序[asc|desc]进行排序
nulls last 时，强制null放在最后，不为null的按声明顺序[asc|desc]进行排序 
【MySql 结论】
order by colum asc 时，null默认被放在最前
order by colum desc 时，null默认被放在最后
ORDER BY IF(ISNULL(update_date),0,1) null被强制放在最前，不为null的按声明顺序[asc|desc]进行排序
ORDER BY IF(ISNULL(update_date),1,0) null被强制放在最后，不为null的按声明顺序[asc|desc]进行排序
````