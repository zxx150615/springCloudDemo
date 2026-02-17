# Naco Web - Spring Cloud 管理平台前端

基于 Vue3 + Vite + Element Plus 的前端管理平台。

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 下一代前端构建工具
- **Vue Router 4** - 官方路由管理器
- **Element Plus** - 基于 Vue 3 的组件库
- **Axios** - HTTP 客户端

## 项目结构

```
naco-web/
├── src/
│   ├── api/              # API 接口封装
│   │   ├── modules/      # API 模块（auth, user, merchant, order）
│   │   └── request.js    # Axios 封装和拦截器
│   ├── components/       # 组件
│   │   └── Layout.vue    # 布局组件
│   ├── router/           # 路由配置
│   │   └── index.js      # 路由定义和守卫
│   ├── styles/           # 样式文件
│   │   └── index.css     # 全局样式
│   ├── utils/            # 工具函数
│   │   └── auth.js       # Token 管理工具
│   ├── views/            # 页面组件
│   │   ├── Login.vue     # 登录页
│   │   ├── Register.vue  # 注册页
│   │   ├── Dashboard.vue # 首页
│   │   ├── UserList.vue  # 用户列表
│   │   ├── MerchantList.vue # 商家列表
│   │   └── OrderList.vue # 订单列表
│   ├── App.vue           # 根组件
│   └── main.js           # 应用入口
├── index.html            # HTML 模板
├── vite.config.js        # Vite 配置
├── package.json          # 项目配置
└── .env.development      # 开发环境变量
```

## 安装依赖

```bash
npm install
```

## 开发

```bash
npm run dev
```

项目将在 `http://localhost:3000` 启动。

## 构建

```bash
npm run build
```

构建产物将输出到 `dist/` 目录。

## 预览构建结果

```bash
npm run preview
```

## 环境变量

在 `.env.development` 中配置开发环境的 API 基础 URL：

```
VITE_API_BASE_URL=http://localhost:8080
```

## 功能特性

- ✅ 用户登录/注册
- ✅ 路由守卫（认证检查）
- ✅ Token 自动管理
- ✅ 用户管理
- ✅ 商家管理（CRUD + 分页）
- ✅ 订单管理
- ✅ 响应式布局
- ✅ Element Plus UI 组件

## API 对接

前端通过 `/api` 代理到后端网关 `http://localhost:8080`。

### 认证接口

- `POST /api/auth/login` - 登录
- `POST /api/auth/register` - 注册

### 业务接口（需携带 Authorization 头）

- `GET /api/user/list` - 用户列表
- `GET /api/merchant/list` - 商家列表
- `GET /api/merchant/page` - 商家分页
- `GET /api/order/list` - 订单列表

## 注意事项

⚠️ **Node.js 版本要求**：建议使用 Node.js 18+ 或 20+。当前项目配置支持 Node.js 17，但可能会有警告。

## 开发说明

1. 确保后端服务（Gateway）已启动在 `http://localhost:8080`
2. 启动前端开发服务器：`npm run dev`
3. 访问 `http://localhost:3000` 开始使用
