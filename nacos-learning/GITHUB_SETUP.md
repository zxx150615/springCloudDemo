# GitHub 推送配置说明

## 当前配置状态

✅ 已配置项目级 Git 用户信息（不影响公司 GitLab）：
- 用户名：zxx150615
- 邮箱：zxx150615@163.com
- Credential Helper：manager-core

## 重要提示

⚠️ **GitHub 不再支持密码认证**，需要使用 **Personal Access Token (PAT)**

## 步骤 1：生成 Personal Access Token

1. 登录 GitHub：https://github.com
2. 点击右上角头像 → **Settings**
3. 左侧菜单最下方 → **Developer settings**
4. 点击 **Personal access tokens** → **Tokens (classic)**
5. 点击 **Generate new token (classic)**
6. 填写：
   - **Note**：`springCloudDemo`（任意名称）
   - **Expiration**：选择过期时间（建议 90 天或自定义）
   - **Select scopes**：勾选 `repo`（完整仓库权限）
7. 点击 **Generate token**
8. **重要**：复制生成的 token（只显示一次！格式类似：`ghp_xxxxxxxxxxxxxxxxxxxx`）

## 步骤 2：保存凭据（两种方式）

### 方式 1：首次推送时自动保存（推荐）

直接执行推送命令，当提示输入密码时，输入你的 **Personal Access Token**（不是密码）：

```powershell
cd nacos-learning
git push origin main
```

输入：
- **Username**: `zxx150615`
- **Password**: `粘贴你的 Personal Access Token`

Git Credential Manager 会自动保存，之后就不需要再输入了。

### 方式 2：使用脚本手动保存

运行提供的 PowerShell 脚本：

```powershell
cd nacos-learning
.\save-github-credential.ps1
```

然后输入你的 Personal Access Token。

## 验证配置

```powershell
cd nacos-learning
git config --list --local
```

应该看到：
- `user.email=zxx150615@163.com`
- `user.name=zxx150615`
- `credential.https://github.com.helper=manager-core`

## 推送代码

```powershell
cd nacos-learning
git add .
git commit -m "你的提交信息"
git push origin main
```

## 注意事项

1. ✅ 此配置**仅影响 nacos-learning 项目**，不会影响公司 GitLab 的使用
2. ✅ Personal Access Token 需要妥善保管，不要泄露
3. ✅ Token 过期后需要重新生成并更新凭据
4. ✅ 如果更换了 Token，需要删除旧凭据：
   ```powershell
   git credential reject
   protocol=https
   host=github.com
   ```
   然后重新推送时会提示输入新 Token
