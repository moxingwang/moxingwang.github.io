* [mac下超好用的终端--iterm2用法与技巧](http://blog.csdn.net/thinkdiff/article/details/25075047)
* [iTerm2 快捷键大全](http://blog.csdn.net/zheng0518/article/details/50817329)
* [Mac下终端配置（item2 + oh-my-zsh + solarized配色方案）](http://www.cnblogs.com/weixuqin/p/7029177.html)
* [改变vim配色：安装colorscheme](http://blog.csdn.net/simple_the_best/article/details/51901361)
* [incr.zsh 补全插件 让你在zsh 模式下全自动补全指令或目录](http://yijiebuyi.com/blog/36955b84c57e338dd8255070b80829bf.html)(http://mimosa-pudica.net/src/incr-0.2.zsh)

## error:zsh compinit: insecure directories, run compaudit for list
* [zsh compinit: insecure directories](https://stackoverflow.com/questions/13762280/zsh-compinit-insecure-directories)
````aidl
EDIT2: On OSX 10.11, only this worked:

$ cd /usr/local/share/
$ sudo chmod -R 755 zsh
$ sudo chown -R root:staff zsh
````

## 隐藏 Dock 图标 和恢复
````aidl
隐藏
/usr/libexec/PlistBuddy  -c "Add :LSUIElement bool true" /Applications/iTerm.app/Contents/Info.plist
恢复
/usr/libexec/PlistBuddy  -c "Delete :LSUIElement" /Applications/iTerm.app/Contents/Info.plist
````

## npm 在mac出错的解决
* [npm mac上安装命令报错](https://segmentfault.com/q/1010000007681402/a-1020000007681841)
```aidl
npm ERR! Darwin 15.6.0
npm ERR! argv "/usr/local/bin/node" "/usr/local/bin/npm" "install" "webpack" "--save-dev"
npm ERR! node v6.9.1
npm ERR! npm v3.10.8
npm ERR! code MODULE_NOT_FOUND
```

# Mac terminal名称太长
```
sudo vi /etc/bashrc
把`PS1='\h:\W \u\$ '`换成`PS1='\w>'`
```
