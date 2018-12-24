* 删除远程分支
````$xslt
git push origin :分支名称
````
* 删除本地分支 
````
git branch -D 分支名称
````

git代码库回滚: 指的是将代码库某分支退回到以前的某个commit id

【本地代码库回滚】：

git reset --hard commit-id :回滚到commit-id，讲commit-id之后提交的commit都去除

git reset --hard HEAD~3：将最近3次的提交回滚




* [5 Git 分支 - 远程分支](https://git-scm.com/book/zh/v1/Git-%E5%88%86%E6%94%AF-%E8%BF%9C%E7%A8%8B%E5%88%86%E6%94%AF)
* [git 删除本地分支和远程分支、本地代码回滚和远程代码库回滚](https://www.cnblogs.com/hqbhonker/p/5092300.html)