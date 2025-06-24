# 云盘服务
## 软件包安装
### java
```markdown
1. tar -xzf /home/软件包/openjdk-17.0.2_linux-x64_bin.tar.gz -C /opt/java/
2. vim /etc/profile
3. 在文件末尾追加：
   export JAVA_HOME=/opt/java/jdk-17.0.2
   export PATH=$JAVA_HOME/bin:$PATH
4. 刷新配置：source /etc/profile
```
### rocketmq
```markdown
  docker run -d \
  --name rmqnamesrv \
  -p 9876:9876 \
  -v /data/docker/rocketmq/namesrv/logs:/root/logs \
  -v /data/docker/rocketmq/namesrv/store:/root/store \
  -e "JAVA_OPTS=-Xms64m -Xmx64m" \
  apache/rocketmq:5.3.3 \
  sh mqnamesrv \

  docker run -d \
  --name rmqbroker \
  --link rmqnamesrv:namesrv \
  -p 10909:10909 -p 10911:10911 \
  -v /data/docker/rocketmq/broker/logs:/root/logs \
  -v /data/docker/rocketmq/broker/store:/root/store \
  -v /data/docker/rocketmq/conf/broker.conf:/opt/rocketmq/conf/broker.conf \
  -e "NAMESRV_ADDR=namesrv:9876" \
  -e "JAVA_OPTS=-Xms256m -Xmx256m -XX:MaxDirectMemorySize=128m" \
  apache/rocketmq:5.3.3 \
  sh mqbroker -c /opt/rocketmq/conf/broker.conf \
```
