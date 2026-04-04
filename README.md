# 大众点评 Demo Monorepo

这是一个前后端分离的简化版大众点评 demo。
后端现在已经升级为 MySQL 8 + Redis 架构：

- MySQL 8 负责持久化店铺和点评数据
- Redis 负责缓存店铺列表、店铺详情、点评和推荐结果

## 目录结构

- `java/`: Spring Boot 后端，负责商户和点评 API
- `web/`: Vue 3 前端，负责首页、店铺详情和评价提交界面

## 技术栈

- 后端: Java 17、Spring Boot 3、Spring Data JPA、MySQL 8、Redis、Maven
- 前端: Vue 3、Vue Router、Vite

## 本机服务信息

- MySQL 端口: `3306`
- Redis 端口: `6379`
- MySQL 数据库: `dianping_demo`
- MySQL 应用账号: `dianping`
- MySQL 应用密码: `dianping123`

你本机的数据库和缓存安装在独立目录，不跟项目绑定：

- MySQL: `~/dev-tools/mysql/current`
- Redis: `~/dev-tools/redis/current`
- MySQL 数据目录: `~/dev-data/mysql`
- Redis 数据目录: `~/dev-data/redis`

常用命令：

```bash
# 启动 MySQL
$HOME/dev-tools/mysql/current/bin/mysqld --defaults-file=$HOME/dev-data/mysql/my.cnf --daemonize

# 停止 MySQL
$HOME/dev-tools/mysql/current/bin/mysqladmin --socket=$HOME/dev-data/mysql/mysql.sock -uroot -proot123 shutdown

# 启动 Redis
$HOME/dev-tools/redis/current/bin/redis-server $HOME/dev-data/redis/redis.conf

# 停止 Redis
$HOME/dev-tools/redis/current/bin/redis-cli -h 127.0.0.1 -p 6379 shutdown
```

## 启动方式

### 1. 启动后端

用 IDEA 打开 [java](/Users/qiu/Documents/New%20project/java) 并直接运行 [DianpingDemoApplication.java](/Users/qiu/Documents/New%20project/java/src/main/java/com/example/dianping/DianpingDemoApplication.java)。

如果你想用命令行，也可以在 [java](/Users/qiu/Documents/New%20project/java) 目录执行：

```bash
mvn spring-boot:run
```

默认地址：

- API: `http://localhost:8080/api/shops`

### 2. 启动前端

进入 [web](/Users/qiu/Documents/New%20project/web) 目录执行：

```bash
npm install
npm run dev
```

默认地址：

- Web: `http://localhost:5173`

## 你现在的手动启动顺序

1. 先启动 MySQL
2. 再启动 Redis
3. 然后在 IDEA 里启动后端
4. 最后在 [web](/Users/qiu/Documents/New%20project/web) 里执行 `npm run dev`

## API 示例

- `GET /api/shops`
- `GET /api/shops?keyword=火锅`
- `GET /api/shops/{id}`
- `GET /api/shops/{id}/reviews`
- `GET /api/shops/{id}/recommendations`
- `POST /api/shops/{id}/reviews`

请求体示例：

```json
{
  "nickname": "小王",
  "score": 5,
  "content": "环境很好，菜品也很稳定。"
}
```
