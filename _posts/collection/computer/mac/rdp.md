## rdesktop

```
rdesktop     -r disk:Hello=/Users/mo/Desktop -r clipboard:CLIPBOARD -u xingwang.mo -p XXXXXXXX fm.group.redstar.corp:3390
rdesktop -f  -r disk:Hello=/Users/mo/Desktop -r clipboard:CLIPBOARD -u xingwang.mo -p XXXXXXXX fm.group.redstar.corp:3390
rdesktop -f  -r disk:Hello=/Users/mo/Desktop -r clipboard:PRIMARYCLIPBOARD -u xingwang.mo -p XXXXXXXX fm.group.redstar.corp:3390

如果无法复杂就来回复制就好。
```


* 错误  http://www.voidcn.com/article/p-bfhncacc-bqo.html
```aidl
报错排除

1）执行rdesktop远程时报错：


ERROR: CredSSP: Initialize failed, do you have correct kerberos tgt initialized ?

Failed to connect, CredSSP required by server.

解决：勾选如下选项即可

b1dec7a70e1a8d2696d4e8567eb7f334.jpg-wh_

若以上选项是灰色，可通过本机组策略修改，如下：

计算机配置――管理模板――Windows组件――远程桌面服务――远程桌面会话主机――安全

4a8214b26db97d018d0237a4a164077c.png-wh_

2）远程时，相互复制粘贴失效

打开windows主机的“任务管理器”，将进程rdpclip.exe杀死后，重新运行即可解决。
```

## Parallels

## Microsoft Remote Desktop for Mac
* [Microsoft Remote Desktop for Mac](https://rink.hockeyapp.net/apps/5e0c144289a51fca2d3bfa39ce7f2b06/)
* [Microsoft Remote Desktop For Mac 哪里下载？](https://www.v2ex.com/t/365984)