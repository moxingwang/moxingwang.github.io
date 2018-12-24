# 常见命令改造
> 添加bat文件到 C:\Windows 下
* ls
> 保存ls.bat到C:\Windows
````aidl
@echo off
dir
````

* open
> 保存open.bat到C:\Windows
````aidl
@echo off
explorer
````

## gvim
* 乱码设置
C:\Program Files (x86)\Vim
修改_vimrc添加
````aidl
set encoding=utf-8
set fileencodings=utf-8,gbk,gb18030,gk2312
source $VIMRUNTIME/delmenu.vim
source $VIMRUNTIME/menu.vim
language messages zh_CN.utf-8

````



# 子系统
[Win10配置Linux Ubuntu子系统：使用教程及技巧](http://www.qingpingshan.com/pc/fwq/383974.html)

[Windows10内置Linux子系统初体验](https://www.jianshu.com/p/bc38ed12da1d)