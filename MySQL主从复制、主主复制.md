# MySQL性能优化
## CentOS 基础环境
### CentOS 固定IP设置
### CentOS 防火墙设置
### CentOS 互信设置
## MySQL优化
### MySQL 安装

1. 检查系统是否自带MySQL
```shell
rpm -qa | grep mysql
```
2. 自带有就卸载
```shell
//普通删除模式
rpm -e mysql
//强力删除模式，如果使用上面命令删除时，提示有依赖的其它文件，则用该命令可以对其进行强力删除
rpm -e --nodeps mysql　　
```
3. 安装mysql服务及客户端
```shell
yum install mysql
yum install mysql-server
```
4. 启动mysql服务
```shell
/etc/init.d/mysqld start
```
5. 设置用户名何密码
```shell
mysqladmin -uroot password root
```
6. 测试登陆是否成功
```shell
mysql -uroot -proot
```
### MySQL 主从复制
#### 简介
```text
MySQL主从又叫做Replication、AB复制。简单讲就是A和B两台机器做主从后，在A上写数据，另外一台B也会跟着写数据，两者数据实时同步的

MySQL主从是基于binlog的，主上须开启binlog才能进行主从。 主从过程大致有3个步骤 
1）主将更改操作记录到binlog里 
2）从将主的binlog事件(sql语句)同步到从本机上并记录在relaylog里 
3）从根据relaylog里面的sql语句按顺序执行

主上有一个log dump线程，用来和从的I/O线程传递binlog
从上有两个线程，其中I/O线程用来同步主的binlog并生成relaylog，另外一个SQL线程用来把relaylog里面的sql语句执行一遍
两种情况：一种是做备份用，一种是作为读用
```
#### 配置主从复制-主

1. 关闭防火墙
```shell
service iptables stop
```
2. 修改my.cnf 在[mysqld]下添加如下代码
```cnf
[mysqld]
log-bin=mysql-bin
server-id=1
binlog-ignore-db=information_schema
binlog-ignore-db=cluster
binlog-ignore-db=mysql

binlog-do-db=test
```
* log-bin 日志文件
* server-id 服务器编码mysql集群下唯一
* binlog-ignore-db 日志记录忽略的数据库
* binlog-do-db 日志记录的数据库

3. 启动数据库服务
```shell
/etc/init.d/mysqld start
```

4. 登陆数据库
```shell
mysql -uroot -proot
```

5. 授权用户
```mysql
GRANT FILE ON *.* TO 'root'@'192.168.56.%' IDENTIFIED BY 'root';
GRANT REPLICATION SLAVE ON *.* TO 'root'@'192.168.56.%' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
```
* 192.168.56.% 指  192.168.56.0 --- 192.168.56.255 都可以访问

6. 重启数据库服务
```shell
/etc/init.d/mysqld restart
```

7. 登陆数据库
```shell
mysql -uroot -proot
```

8. 检查master配置
```mysql
show master status;
```
File | Position | Binlog_Do_DB | Binlog_Ignore_DB
---|---|---|---
mysql-bin.000004 | 733 | test | nformation_schema,cluster,mysql

#### 配置主从复制-从

1. 测试连接master
```mysql
mysql -uroot -proot -h192.168.56.133 -P3306
```
* 能连接表示账号/密码/网络/端口访问正常
* 否则检查账号/密码/网络/端口
* 注意防火墙slave需要获得master的日志信息来实现同步，需要开放master的访问权限，最简单解决办法关闭master防火墙：service iptables stop

2. 修改my.cnf 在[mysqld]下添加如下代码
```cnf
[mysqld]
log-bin=mysql-bin
server-id=2
binlog-ignore-db=information_schema
binlog-ignore-db=cluster
binlog-ignore-db=mysql

replicate-do-db=test
replicate-ignore-db=mysql
log-slave-updates
slave-skip-errors=all
slave-net-timeout=60
```
* log-bin 日志文件
* server-id 服务器编码mysql集群下唯一
* binlog-ignore-db 日志记录忽略的数据库
* replicate-do-db 复制的数据库
* replicate-ignore-db 复制的忽略数据库
* log-slave-updates 表示可以复制master日志中的数据到slave
* slave-skip-errors 复制时自动跳过错误
* slave-net-timeout 网络连接超时

3. 关闭slave
```mysql
slave stop
```
4. 重置slave
```mysql
reset slave
```
5. 配置slave连接master
```mysql
change master to master_host='192.168.56.135',master_user='root',master_password='root',master_log_file='mysql-bin.000005', master_log_pos=106;
```
* change master to master_host=MasterIP,master_user=账号,master_password=密码,master_log_file=日志文件, master_log_pos=日志分区;
* 日志文件  master:show master status 中的 File
* 日志分区  master:show master status 中的 Position
6. 启动slave
```mysql
slave start
```
7. 检查slave连接master状态
```mysql
show slave status /G;
```
8. 完成，测试
* 在master 的test库建表,CURD
* 查看slaver 中是否同步了

### MySQL 主主复制
#### 配置主从复制-主A/主B
1. 关闭防火墙
```shell
service iptables stop
```
2. 修改my.cnf 在[mysqld]下添加如下代码
```cnf
[mysqld]
log-bin=mysql-bin
server-id=1
binlog-ignore-db=information_schema
binlog-ignore-db=cluster
binlog-ignore-db=mysql

binlog-do-db=test

replicate-do-db=test
replicate-ignore-db=mysql
log-slave-updates
slave-skip-errors=all
slave-net-timeout=60
```
* log-bin 日志文件
* server-id 服务器编码mysql集群下唯一
* binlog-ignore-db 日志记录忽略的数据库
* binlog-do-db 日志记录的数据库
* replicate-do-db 复制的数据库
* replicate-ignore-db 复制的忽略数据库
* log-slave-updates 表示可以复制master日志中的数据到slave
* slave-skip-errors 复制时自动跳过错误
* slave-net-timeout 网络连接超时

3. 启动数据库服务
```shell
/etc/init.d/mysqld start
```

4. 登陆数据库
```shell
mysql -uroot -proot
```

5. 授权用户
```mysql
GRANT FILE ON *.* TO 'root'@'192.168.56.%' IDENTIFIED BY 'root';
GRANT REPLICATION SLAVE ON *.* TO 'root'@'192.168.56.%' IDENTIFIED BY 'root';
FLUSH PRIVILEGES;
```
* 192.168.56.% 指  192.168.56.0 --- 192.168.56.255 都可以访问

6. 重启数据库服务
```shell
/etc/init.d/mysqld restart
```

7. 登陆数据库
```shell
mysql -uroot -proot
```

8. 检查master配置
```mysql
show master status;
```
File | Position | Binlog_Do_DB | Binlog_Ignore_DB
---|---|---|---
mysql-bin.000004 | 733 | test | nformation_schema,cluster,mysql

9. 关闭slave
```mysql
slave stop
```
10. 重置slave
```mysql
reset slave
```

11. 配置slave连接master
```mysql
change master to master_host='192.168.56.135',master_user='root',master_password='root',master_log_file='mysql-bin.000005', master_log_pos=106;
```
* change master to master_host=MasterIP,master_user=账号,master_password=密码,master_log_file=日志文件, master_log_pos=日志分区;
* 日志文件  master:show master status 中的 File
* 日志分区  master:show master status 中的 Position

12. 启动slave
```mysql
slave start
```

13. 检查slave连接master状态
```mysql
show slave status /G;
```

14. 完成，测试
* 两个服务器相互在test下建表CURD检查是否同步

### MySQL 读写分离