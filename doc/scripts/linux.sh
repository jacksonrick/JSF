################################################
#         一键系统优化脚本，适用于Centos          #
################################################

. /etc/init.d/functions
#date
DATE=`date +"%y-%m-%d %H:%M:%S"`
#ip
IPADDR=`ip addr | grep "inet" | grep -vE  'inet6|127.0.0.1'|awk '{print $2}'`
#hostname
HOSTNAME=`hostname -s`
#user
USER=`whoami`
#disk_check  (grep -w用于字符串精确匹配)
DISK_SDA=`df -h |grep -w "/" |awk '{print $5}'`
#cpu_average_check
cpu_uptime=`cat /proc/loadavg|awk '{print $1,$2,$3}'`
#set LANG
export LANG=zh_CN.UTF-8

#Require root to run this script.
uid=`id | cut -d\( -f1 | cut -d= -f2`
if [ $uid -ne 0 ];then
  action "Please run this script as root." /bin/false
  exit 1
fi

#"stty erase ^H"  设置backspace为删除键
\cp /root/.bash_profile  /root/.bash_profile_$(date +%F)
erase=`grep -wx "stty erase ^H" /root/.bash_profile |wc -l`
if [ $erase -lt 1 ];then
    echo "stty erase ^H" >>/root/.bash_profile
    source /root/.bash_profile
fi

#Config Yum CentOS-Bases.repo and save Yum file
configYum(){
echo "================更新为阿里YUM源=================="
for i in /etc/yum.repos.d/*.repo;do cp $i ${i%.repo}.bak;done
rm -rf /etc/yum.repos.d/*.repo
wget -P /etc/yum.repos.d/ http://mirrors.aliyun.com/repo/Centos-7.repo >/dev/null 2>&1
  #ping -c 1  mirrors.aliyun.com  >/dev/null
  #if [ $? -eq 0 ];then
  #  rm -rf /etc/yum.repos.d/*.repo
  #  wget -P /etc/yum.repos.d/ http://mirrors.aliyun.com/repo/Centos-7.repo >/dev/null 2>&1
  #else
  #  echo "无法连接网络。"
  #  exit $?
  #fi
echo "================保存YUM源文件===================="
sed -i 's#keepcache=0#keepcache=1#g' /etc/yum.conf     
grep keepcache /etc/yum.conf
yum clean all;yum makecache;yum repolist
sleep 5
action "配置国内YUM完成"  /bin/true
echo "================================================="
echo ""
  sleep 2
}

initI18n(){
echo "================更改为中文字符集================="
  \cp /etc/locale.conf  /etc/locale.conf.$(date +%F)
cat >>/etc/locale.conf<<EOF
LANG="zh_CN.UTF-8"
#LANG="en_US.UTF-8"
EOF
source /etc/locale.conf
grep LANG /etc/locale.conf
action "更改字符集zh_CN.UTF-8完成" /bin/true
echo "================================================="
echo ""
  sleep 2
}

initFirewall(){
echo "============禁用SELINUX及关闭防火墙=============="
  \cp /etc/selinux/config /etc/selinux/config.$(date +%F)
  systemctl stop firewalld
  sed -i 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/selinux/config
  setenforce 0
  systemctl status firewalld
  echo '#grep SELINUX=disabled /etc/selinux/config ' 
  grep SELINUX=disabled /etc/selinux/config 
  echo '#getenforce '
  getenforce 
action "禁用selinux及关闭防火墙完成" /bin/true
echo "================================================="
echo ""
  sleep 2
}

initService(){
echo "===============精简开机自启动===================="
  export LANG="en_US.UTF-8"
  for A in `systemctl list-unit-files | grep enable|awk '{print $1}'`;do systemctl $A off;done
  for B in auditd.service crond.service  irqbalance.service  kdump.service microcode.service rsyslog.service sshd.service  sysstat.service  \
  	systemd-readahead-collect.service   multi-user.target remote-fs.target runlevel2.target  NetworkManager.service ;do systemctl enable $B;done
  echo '+--------which services on---------+'
  systemctl list-unit-files | grep enable
  echo '+----------------------------------+'
  export LANG="zh_CN.UTF-8"
action "精简开机自启动完成" /bin/true
echo "================================================="
echo ""
  sleep 10
}

initRemoval(){
echo "======去除系统及内核版本登录前的屏幕显示======="
if    
   [ $UID -ne 0 ];then
   echo This script must use the root user ! ! ! 
   sleep 2
   exit 0
fi
    cp /etc/redhat-release /etc/redhat-release.$(date +%F)
    >/etc/redhat-release
    >/etc/issue
action "去除系统及内核版本登录前的屏幕显示" /bin/true
echo "================================================="
echo ""
  sleep 2
}

initSsh(){
echo "==============SSH配置优化=============="
  \cp /etc/ssh/sshd_config /etc/ssh/sshd_config.$(date +%F)
  sed -i 's/#PermitEmptyPasswords no/PermitEmptyPasswords no/g' /etc/ssh/sshd_config
  sed -i 's/#UseDNS yes/UseDNS no/g' /etc/ssh/sshd_config
  sed -i 's/^GSSAPIAuthentication yes$/GSSAPIAuthentication no/' /etc/ssh/sshd_config
  echo '+-------modify the sshd_config-------+'
  echo 'PermitEmptyPasswords no'
  echo 'UseDNS no'
  echo 'GSSAPIAuthentication no'
  echo '+------------------------------------+'
  service sshd restart
  action "修改SSH参数完成" /bin/true
echo "================================================="
echo ""
  sleep 2
}

syncSysTime(){
echo "================配置时间同步====================="
  \cp /var/spool/cron/root /var/spool/cron/root.$(date +%F) 2>/dev/null
  NTPDATE=`grep ntpdate /var/spool/cron/root 2>/dev/null |wc -l`
  if [ $NTPDATE -eq 0 ];then
    echo "#times sync by lee at $(date +%F)" >>/var/spool/cron/root
    echo "*/5 * * * * /usr/sbin/ntpdate time.windows.com &>/dev/null" >> /var/spool/cron/root
  fi
  echo '#crontab -l'  
  crontab -l
action "配置时间同步完成" /bin/true
echo "================================================="
echo ""
  sleep 2
}

initTools(){
    echo "==============安装升级系统工具=============="
    echo "请先确保已切换到国内源！"
    yum install psmisc sysstat iotop pidstat iftop telnet net-tools lsof zip unzip -y
    sleep 2
action "安装升级系统工具" /bin/true
echo "================================================="
echo ""
  sleep 2
}

addUser(){
echo "===================新建用户======================"
#add user
while true
do  
    read -p "请输入新用户名:" name
    NAME=`awk -F':' '{print $1}' /etc/passwd|grep -wx $name 2>/dev/null|wc -l`
    if [ ${#name} -eq 0 ];then
       echo "用户名不能为空，请重新输入。"
       continue
    elif [ $NAME -eq 1 ];then
       echo "用户名已存在，请重新输入。"
       continue
    fi
useradd $name
break
done
#create password
while true
do
    read -p "为 $name 创建一个密码:" pass1
    if [ ${#pass1} -eq 0 ];then
       echo "密码不能为空，请重新输入。"
       continue
    fi
    read -p "请再次输入密码:" pass2
    if [ "$pass1" != "$pass2" ];then
       echo "两次密码输入不相同，请重新输入。"
       continue
    fi
echo "$pass2" |passwd --stdin $name
break
done
sleep 1

#add visudo
echo "==============add visudo=============="
\cp /etc/sudoers /etc/sudoers.$(date +%F)
SUDO=`grep -w "$name" /etc/sudoers |wc -l`
if [ $SUDO -eq 0 ];then
    echo "$name  ALL=(ALL)       NOPASSWD: ALL" >>/etc/sudoers
    echo '#tail -1 /etc/sudoers'
    grep -w "$name" /etc/sudoers
    sleep 1
fi
action "创建用户$name并将其加入visudo完成"  /bin/true
echo "================================================="
echo ""
sleep 2
}

initLimits(){
echo "===============加大文件描述符===================="
  LIMIT=`grep nofile /etc/security/limits.conf |grep -v "^#"|wc -l`
  if [ $LIMIT -eq 0 ];then
  \cp /etc/security/limits.conf /etc/security/limits.conf.$(date +%F)
cat >>/etc/security/limits.conf<<EOF
*  soft nofile 2048
*  hard nofile 65535
*  soft nproc  16384
*  hard nproc  16384
EOF
  fi
  tail -1 /etc/security/limits.conf
  ulimit -HSn 65535
  echo '#ulimit -n'
  ulimit -n
action "配置文件描述符为65535" /bin/true
echo "================================================="
echo ""
sleep 5
}

initRestart(){
rm -rf /usr/lib/systemd/system/ctrl-alt-del.target
init q
action "将ctrl+alt+delete键屏蔽，防止误操作的时候服务器重启" /bin/true
echo "================================================="
echo ""
sleep 2
}

initSysctl(){
echo "================优化内核参数====================="
SYSCTL=`grep "net.ipv4.tcp" /etc/sysctl.conf |wc -l`
if [ $SYSCTL -lt 10 ];then
   \cp /etc/sysctl.conf /etc/sysctl.conf.$(date +%F)
cat >>/etc/sysctl.conf<<EOF
net.ipv4.tcp_fin_timeout = 2
net.ipv4.tcp_tw_reuse = 1
net.ipv4.tcp_tw_recycle = 1
net.ipv4.tcp_syncookies = 1
net.ipv4.tcp_max_syn_backlog = 16384
net.ipv4.tcp_max_tw_buckets = 36000
net.ipv4.tcp_syn_retries = 1
net.ipv4.tcp_synack_retries = 1
net.ipv4.tcp_max_orphans = 16384
net.ipv4.tcp_rmem=4096 87380 4194304
net.ipv4.tcp_wmem=4096 16384 4194304
net.ipv4.tcp_keepalive_time = 600 
net.ipv4.tcp_keepalive_probes = 5 
net.ipv4.tcp_keepalive_intvl = 15 
net.ipv4.route.gc_timeout = 100
net.ipv4.ip_local_port_range = 1024 65000 
net.ipv4.icmp_echo_ignore_broadcasts=1
net.core.somaxconn = 16384 
net.core.netdev_max_backlog = 16384
EOF
fi
sysctl -p
action "内核调优完成" /bin/true
echo "================================================="
echo ""
  sleep 5
}

initHistory(){
echo "======设置默认历史记录数2000和连接超时时间600======"
echo "TMOUT=600" >>/etc/profile
echo "HISTSIZE=2000" >>/etc/profile
echo "HISTTIMEFORMAT='%Y-%m-%d %H:%M:%S => '" >>/etc/profile
source /etc/profile
action "设置默认历史记录数和连接超时时间" /bin/true
echo "================================================="
echo ""
sleep 2
}

initChattr(){
echo "======锁定关键文件系统======"
chattr +i /etc/passwd
chattr +i /etc/inittab
chattr +i /etc/group
chattr +i /etc/shadow
chattr +i /etc/gshadow
/bin/mv /usr/bin/chattr /usr/bin/lock
action "锁定关键文件系统" /bin/true
echo "================================================="
echo ""
sleep 2
}

grub_md5(){
echo "======grub_md5加密======"
echo "======命令行输入：/sbin/grub-md5-crypt 进行交互式加密======"
echo "把密码写入/etc/grub.conf 格式：password --MD5 密码"
echo ""
sleep 10
}

ban_ping(){
echo '#内网可以ping，其他不能ping'
echo 'iptables -t filter -I INPUT -p icmp --icmp-type 8 -i eth0 -s  0.0.0.0/24 -j ACCEPT'
sleep 10
}

menu2(){
while true;
do
 clear
cat <<EOF
-----------------------------------------
|   ****Please Enter Your Choice:****   |
-----------------------------------------
(1)  新建一个用户并将其加入sudo
(2)  配置为国内YUM源镜像和保存YUM源文件
(3)  配置中文字符集
(4)  禁用SELINUX及关闭防火墙
(5)  精简开机自启动
(6)  去除系统及内核版本登录前的屏幕显示
(7)  ssh配置优化
(8)  设置时间同步
(9)  安装升级系统工具
(10) 加大文件描述符
(11) 将ctrl+alt+delete键屏蔽，防止误操作的时候服务器重启
(12) 系统内核调优
(13) 设置默认历史记录数和连接超时时间
(14) 锁定关键文件系统
(15) grub_md5加密方式
(16) 禁ping方式
(0) 返回上一级菜单

EOF
 read -p "Please enter your Choice[0-15]: " input2
 case "$input2" in
   0)
   clear
   break 
   ;;
   1)
   addUser
   ;;
   2)
   configYum
   ;;
   3)
   initI18n
   ;;
   4)
   initFirewall
   ;;
   5)
   initService
   ;;
   6)
   initRemoval
   ;;
   7)
   initSsh
   ;;
   8)
   syncSysTime
   ;;
   9)
   initTools
   ;;
   10)
   initLimits
   ;;
   11)
   initRestart
   ;;
   12)
   initSysctl
   ;;
   13)
   initHistory
   ;;
   14)
   initChattr
   ;;
   15)
   grub_md5
   ;;
   16)
   ban_ping
   ;;
   *) echo "----------------------------------"
      echo "|          Warning!!!            |"
      echo "|   Please Enter Right Choice!   |"
      echo "----------------------------------"
      for i in `seq -w 3 -1 1`
        do 
          echo -ne "\b\b$i";
          sleep 1;
        done
      clear
 esac
done
}

while true;
do
 clear
 echo "========================================"
 echo '          Linux Optimization            '   
 echo "========================================"
cat << EOF
|-----------System Infomation-----------
| DATE       : $DATE
| HOSTNAME   : $HOSTNAME
| USER       : $USER
| IP         : $IPADDR
| DISK_USED  : $DISK_SDA
| CPU_AVERAGE: $cpu_uptime
----------------------------------------
|****Please Enter Your Choice:[1-3]****|
----------------------------------------
(1) 一键优化
(2) 自定义优化
(3) 退出
EOF
 #choice
 read -p "Please enter your choice[0-3]: " input1
 case "$input1" in
 1) 
   configYum
   initI18n
   initFirewall
   initService
   initRemoval
   initSsh
   syncSysTime
   initTools
   initLimits
   initRestart
   initSysctl
   initHistory
   initChattr
   ;;
 2)
   menu2
   ;;
 3) 
   clear 
   break
   ;;
 *)   
   echo "----------------------------------"
   echo "|          Warning!!!            |"
   echo "|   Please Enter Right Choice!   |"
   echo "----------------------------------"
   for i in `seq -w 3 -1 1`
       do
         echo -ne "\b\b$i";
         sleep 1;
       done
   clear
 esac  
done