# 用户操作
* 修改当前用户密码 passwd
* 修改用户密码 passwd userName
* 新增用户到组 useradd -G root test


### 命令行操作
````
     常用
    ctrl+左右键:在单词之间跳转
    ctrl+a:跳到本行的行首
    ctrl+e:跳到页尾
    Ctrl+u：删除当前光标前面的文字 （还有剪切功能）
    ctrl+k：删除当前光标后面的文字(还有剪切功能)
    Ctrl+L：进行清屏操作
    Ctrl+y:粘贴Ctrl+u或ctrl+k剪切的内容
    Ctrl+w:删除光标前面的单词的字符
    Alt – d ：由光标位置开始，往右删除单词。往行尾删
    说明
    
    Ctrl – k: 先按住 Ctrl 键，然后再按 k 键；
    Alt – k: 先按住 Alt 键，然后再按 k 键；
    M – k：先单击 Esc 键，然后再按 k 键。
    移动光标
    
    Ctrl – a ：移到行首
    Ctrl – e ：移到行尾
    Ctrl – b ：往回(左)移动一个字符
    Ctrl – f ：往后(右)移动一个字符
    Alt – b ：往回(左)移动一个单词
    Alt – f ：往后(右)移动一个单词
    Ctrl – xx ：在命令行尾和光标之间移动
    M-b ：往回(左)移动一个单词
    M-f ：往后(右)移动一个单词
    编辑命令
    
    Ctrl – h ：删除光标左方位置的字符
    Ctrl – d ：删除光标右方位置的字符（注意：当前命令行没有任何字符时，会注销系统或结束终端）
    Ctrl – w ：由光标位置开始，往左删除单词。往行首删
    Alt – d ：由光标位置开始，往右删除单词。往行尾删
    M – d ：由光标位置开始，删除单词，直到该单词结束。
    Ctrl – k ：由光标所在位置开始，删除右方所有的字符，直到该行结束。
    Ctrl – u ：由光标所在位置开始，删除左方所有的字符，直到该行开始。
    Ctrl – y ：粘贴之前删除的内容到光标后。
    ctrl – t ：交换光标处和之前两个字符的位置。
    Alt + . ：使用上一条命令的最后一个参数。
    Ctrl – _ ：回复之前的状态。撤销操作。
    Ctrl -a + Ctrl -k 或 Ctrl -e + Ctrl -u 或 Ctrl -k + Ctrl -u 组合可删除整行。
    
    Bang(!)命令
    
    !! ：执行上一条命令。
    ^foo^bar ：把上一条命令里的foo替换为bar，并执行。
    !wget ：执行最近的以wget开头的命令。
    !wget:p ：仅打印最近的以wget开头的命令，不执行。
    !$ ：上一条命令的最后一个参数， 与 Alt - . 和 $_ 相同。
    !* ：上一条命令的所有参数
    !*:p ：打印上一条命令是所有参数，也即 !*的内容。
    ^abc ：删除上一条命令中的abc。
    ^foo^bar ：将上一条命令中的 foo 替换为 bar
    ^foo^bar^ ：将上一条命令中的 foo 替换为 bar
    !-n ：执行前n条命令，执行上一条命令： !-1， 执行前5条命令的格式是： !-5
    查找历史命令
    
    Ctrl – p ：显示当前命令的上一条历史命令
    Ctrl – n ：显示当前命令的下一条历史命令
    Ctrl – r ：搜索历史命令，随着输入会显示历史命令中的一条匹配命令，Enter键执行匹配命令；ESC键在命令行显示而不执行匹配命令。
    Ctrl – g ：从历史搜索模式（Ctrl – r）退出。
    控制命令
    
    Ctrl – l ：清除屏幕，然后，在最上面重新显示目前光标所在的这一行的内容。
    Ctrl – o ：执行当前命令，并选择上一条命令。
    Ctrl – s ：阻止屏幕输出
    Ctrl – q ：允许屏幕输出
    Ctrl – c ：终止命令
    Ctrl – z ：挂起命令
    重复执行操作动作
    
    M – 操作次数 操作动作 ： 指定操作次数，重复执行指定的操作。
````

### 内存查看
#### memory
    top命令能显示系统内存。
    目前常用的Linux下查看内容的专用工具是free命令。
    下面是对内存查看free命令输出内容的解释：
    total：总计物理内存的大小。
    
#### 物理内存
    df

### 文件搜索
#### grep
    eg: grep -n aa file.txt
#### sed 
    eg: sed -n '1,5p' aaa.sh
#### ack 
    eg: ack -n ffff aaa.sh 

### 日志查看
#### head
#### tail
#### cat 一次性显示全部文件内容
#### less http://www.cnblogs.com/waitig/archive/2016/09/28/5916338.html

### MySql连接
#### 连接 mysql -P 3306 -h 192.168.226.96 -u root -p

### 根据端口找到应用：
````
lsof -i:端口
ll /proc/PID
netstat -ntlp
````

### Nmap 网络端口扫描（nmap -ST 172.16.10.9）

###  ls以M查看文件大小：ls -lh

### ps -ef|grep java

###  killall java

````
pwd    显示当前在哪个目录下
2. su - username 切换用户  
3. useradd username 添加用户
	useradd -g 组名 用户名
4. passwd username 给用户设置密码
5. userdel username 删除用户
6. userdel -r username 删除用户以及用户主目录
7. 设置Linux的启动级别： /etc/inittab  编辑 “id:5:initdefault:” 这行即可
	在启动引导界面的时候就按“e”, 就可以就修改启动级别 
8. mkdir 建立目录     rmdir 删除空目录
9. 查看文件但是不能修改内容.
````

### 防火墙
* [linux运维-firewall](http://blog.csdn.net/ma_jia_min/article/details/73155542)
* [Linux CentOS 7 防火墙/端口设置](https://www.cnblogs.com/taiyonghai/p/5825578.html)
* [CentOS 7安装Fail2ban防御暴力破解密码（配合FirewallD)](http://blog.163.com/l1_jun/blog/static/14386388201642443431107/)
* systemctl start firewalld 开启防火墙
* firewall-cmd --zone=public --list-ports  查看已开放的端口
* firewall-cmd --add-port=443/tcp   开放端口

##### 关闭firewall
````aidl
systemctl stop firewalld.service #停止firewall
systemctl disable firewalld.service #禁止firewall开机启动
````