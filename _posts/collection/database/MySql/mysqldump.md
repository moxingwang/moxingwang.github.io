#### 1. 导出数据的所有表的数据和结构
    mysqldump -h 192.168.11.11 -u root -p his_chat > /home/mo/Desktop/his_chat.sql
#### 2. 导出选中表的结构和数据
    mysqldump -u root -p mo aa bb > /home/mo/Desktop/aa.sql
#### 3.导出选中表的结构
    mysqldump -u root -p -d  mo aa bb > /home/mo/Desktop/111.sql
#### 4.导出设置编码
    mysqldump --default-character-set=utf8 -h 192.168.10.2 -u ih_nurse_php -p his_chat ih_record_table > aaaaaa.sql