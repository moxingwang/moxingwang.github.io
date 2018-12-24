单个代码库的用户信息：
    #查看
        git config --get user.name
        git config --get user.email
    #设置
        git config user.name {xxxxx}  #中文名称
        git config user.email {xxxx@chinaredstar.com} # 公司邮箱
全局的用户信息：
    #查看
        git config --global --get user.name
        git config --global --get user.email
    #设置
        git config --global user.name {xxxxx}
        git config --global user.email {xxxx@chinaredstar.com}