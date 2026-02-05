# GitHub 凭据保存脚本
# 使用方法：运行此脚本，输入你的 GitHub Personal Access Token

Write-Host "请输入你的 GitHub Personal Access Token (不是密码):" -ForegroundColor Yellow
$token = Read-Host -AsSecureString
$BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($token)
$plainToken = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)

# 使用 Git Credential Manager 保存凭据
$credential = "protocol=https`nhost=github.com`nusername=zxx150615`npassword=$plainToken"
$credential | git credential approve

Write-Host "`n凭据已保存！现在可以免登录推送代码了。" -ForegroundColor Green
Write-Host "注意：请妥善保管你的 Personal Access Token" -ForegroundColor Yellow
