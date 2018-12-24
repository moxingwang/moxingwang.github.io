* 修改bash;[How to uninstall zsh](http://apple.stackexchange.com/questions/100468/how-to-uninstall-zsh)
> If the default login shell was changed, you can change it back to /bin/bash by running chsh -s /bin/bash.

* [zsh](https://wiki.archlinux.org/index.php/Zsh_(%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87))

* [如何在mac中用命令行时用sublime打开文件](https://segmentfault.com/q/1010000002397241)
````aidl
如果是在默认shell下,
sudo ln -s "/Applications/Sublime\ Text.app/Contents/SharedSupport/bin/subl" /usr/bin/subl

使用zsh的可以使用以下命令
alias subl="'/Applications/Sublime Text.app/Contents/SharedSupport/bin/subl'"
alias nano="subl"
export EDITOR="subl"
````