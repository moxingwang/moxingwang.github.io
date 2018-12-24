* Git标签，git打标签

````
Git跟其他版本控制系统一样，可以打标签（tag）标记一个版本号。
一、列出标签
1. 列出当前仓库的所有标签：git tag
2. 搜素符合条件的标签：git tag -l "3.7.*"
二、创建标签tag，并推送到远程
1. 打标签：git tag -a "3.7.5" -m "日本邀请制"
2. 给指定的commit打标签：git tag -a "3.7.4" 9fbc3d0
3. 提交指定标签到git服务器：git push origin "3.7.5" 
4. 提交本地所有标签到git服务器：git push origin --tags
三、切换、并查看标签信息
1. 切换到指定标签：git checkout "3.7.5"
2. 查看标签的版本信息：git show "3.7.5"
四、删除标签
1. 删除标签tag：git tag -d 3.7.5
2. 删除远程服务器的标签：git push origin :refs/tags/3.7.5
命令总结：
命令git tag <name>用于新建一个标签，默认为HEAD，也可以指定一个commit id；
命令git tag -a <tagname> -m "blablabla..."可以指定标签信息；
命令git tag -s <tagname> -m "blablabla..."可以用PGP签名标签；
命令git tag可以查看所有标签。
命令git push origin <tagname>可以推送一个本地标签；
命令git push origin --tags可以推送全部未推送过的本地标签；
命令git tag -d <tagname>可以删除一个本地标签；
命令git push origin :refs/tags/<tagname>可以删除一个远程标签。
拉取tag : git checkout -b branch_name tag_name
````