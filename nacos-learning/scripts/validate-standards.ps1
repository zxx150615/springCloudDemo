# Nacos-Learning 项目规范验证脚本
# 用于检查项目是否符合规范要求

param(
    [string]$ProjectRoot = "."
)

$ErrorCount = 0
$WarningCount = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Nacos-Learning 项目规范检查" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查函数
function Check-Dependency {
    param(
        [string]$PomFile,
        [string]$GroupId,
        [string]$ArtifactId,
        [string]$RequiredVersion = "",
        [string]$Description
    )
    
    if (Test-Path $PomFile) {
        $content = Get-Content $PomFile -Raw
        if ($content -match "$GroupId.*$ArtifactId") {
            if ($RequiredVersion -ne "" -and $content -notmatch "version.*$RequiredVersion") {
                Write-Host "⚠️  警告: $Description - 版本可能不正确" -ForegroundColor Yellow
                $script:WarningCount++
            } else {
                Write-Host "✅ $Description" -ForegroundColor Green
            }
        } else {
            Write-Host "❌ 错误: $Description - 未找到依赖" -ForegroundColor Red
            $script:ErrorCount++
        }
    }
}

function Check-Forbidden-Dependency {
    param(
        [string]$PomFile,
        [string]$ForbiddenArtifact,
        [string]$Description
    )
    
    if (Test-Path $PomFile) {
        $content = Get-Content $PomFile -Raw
        if ($content -match $ForbiddenArtifact) {
            Write-Host "❌ 错误: $Description - 禁止使用的依赖" -ForegroundColor Red
            $script:ErrorCount++
        }
    }
}

function Check-Nacos-Config {
    param(
        [string]$ServiceName
    )
    
    $configFile = "nacos-config-examples/nacos-$ServiceName-dev.yaml"
    if (Test-Path $configFile) {
        Write-Host "✅ Nacos 配置文件存在: $configFile" -ForegroundColor Green
    } else {
        Write-Host "⚠️  警告: Nacos 配置文件不存在: $configFile" -ForegroundColor Yellow
        $script:WarningCount++
    }
}

# 1. 检查父 POM 版本
Write-Host "1. 检查父 POM 版本配置..." -ForegroundColor Cyan
$parentPom = "$ProjectRoot/pom.xml"
if (Test-Path $parentPom) {
    $content = Get-Content $parentPom -Raw
    
    if ($content -match "spring-boot-starter-parent.*2\.7\.18") {
        Write-Host "✅ Spring Boot 版本: 2.7.18" -ForegroundColor Green
    } else {
        Write-Host "❌ Spring Boot 版本不正确" -ForegroundColor Red
        $script:ErrorCount++
    }
    
    if ($content -match "spring-cloud.version.*2021\.0\.9") {
        Write-Host "✅ Spring Cloud 版本: 2021.0.9" -ForegroundColor Green
    } else {
        Write-Host "❌ Spring Cloud 版本不正确" -ForegroundColor Red
        $script:ErrorCount++
    }
    
    if ($content -match "spring-cloud-alibaba.version.*2021\.0\.5\.0") {
        Write-Host "✅ Spring Cloud Alibaba 版本: 2021.0.5.0" -ForegroundColor Green
    } else {
        Write-Host "❌ Spring Cloud Alibaba 版本不正确" -ForegroundColor Red
        $script:ErrorCount++
    }
} else {
    Write-Host "❌ 父 POM 文件不存在" -ForegroundColor Red
    $script:ErrorCount++
}

Write-Host ""

# 2. 检查各模块依赖
Write-Host "2. 检查模块依赖..." -ForegroundColor Cyan

$modules = @(
    @{Name="nacos-provider"; Path="$ProjectRoot/nacos-provider/pom.xml"},
    @{Name="nacos-consumer"; Path="$ProjectRoot/nacos-consumer/pom.xml"},
    @{Name="nacos-gateway"; Path="$ProjectRoot/nacos-gateway/pom.xml"},
    @{Name="user-service"; Path="$ProjectRoot/user-service/pom.xml"},
    @{Name="order-service"; Path="$ProjectRoot/order-service/pom.xml"}
)

foreach ($module in $modules) {
    if (Test-Path $module.Path) {
        Write-Host "`n检查模块: $($module.Name)" -ForegroundColor Yellow
        
        # 检查必需依赖
        Check-Dependency -PomFile $module.Path -GroupId "com.alibaba.cloud" -ArtifactId "spring-cloud-starter-alibaba-nacos-discovery" -Description "Nacos Discovery"
        Check-Dependency -PomFile $module.Path -GroupId "com.alibaba.cloud" -ArtifactId "spring-cloud-starter-alibaba-nacos-config" -Description "Nacos Config"
        
        # 检查禁止的依赖
        Check-Forbidden-Dependency -PomFile $module.Path -ForbiddenArtifact "spring-cloud-starter-netflix-eureka" -Description "禁止使用 Eureka"
        Check-Forbidden-Dependency -PomFile $module.Path -ForbiddenArtifact "spring-cloud-starter-zuul" -Description "禁止使用 Zuul"
        Check-Forbidden-Dependency -PomFile $module.Path -ForbiddenArtifact "postgresql" -Description "禁止使用 PostgreSQL"
        Check-Forbidden-Dependency -PomFile $module.Path -ForbiddenArtifact "rabbitmq" -Description "禁止使用 RabbitMQ"
        Check-Forbidden-Dependency -PomFile $module.Path -ForbiddenArtifact "kafka" -Description "禁止使用 Kafka"
        
        # Gateway 特殊检查
        if ($module.Name -eq "nacos-gateway") {
            Check-Dependency -PomFile $module.Path -GroupId "org.springframework.cloud" -ArtifactId "spring-cloud-starter-gateway" -Description "Spring Cloud Gateway"
        }
        
        # Order Service 检查 RocketMQ 和 OpenFeign
        if ($module.Name -eq "order-service") {
            Check-Dependency -PomFile $module.Path -GroupId "org.springframework.cloud" -ArtifactId "spring-cloud-starter-openfeign" -Description "OpenFeign"
            Check-Dependency -PomFile $module.Path -GroupId "com.alibaba.cloud" -ArtifactId "spring-cloud-starter-alibaba-rocketmq" -Description "RocketMQ"
        }
        
        # Provider 检查 MySQL
        if ($module.Name -eq "nacos-provider") {
            Check-Dependency -PomFile $module.Path -GroupId "mysql" -ArtifactId "mysql-connector-java" -Description "MySQL Connector"
        }
    }
}

Write-Host ""

# 3. 检查 Nacos 配置文件
Write-Host "3. 检查 Nacos 配置文件..." -ForegroundColor Cyan
Check-Nacos-Config -ServiceName "provider"
Check-Nacos-Config -ServiceName "consumer"
Check-Nacos-Config -ServiceName "gateway"

Write-Host ""

# 4. 检查包结构
Write-Host "4. 检查包结构..." -ForegroundColor Cyan
$services = @("nacos-provider", "nacos-consumer", "user-service", "order-service")
foreach ($service in $services) {
    $controllerPath = "$ProjectRoot/$service/src/main/java/com/zxx/learning"
    if (Test-Path $controllerPath) {
        Write-Host "✅ $service 包结构正确" -ForegroundColor Green
    } else {
        Write-Host "⚠️  警告: $service 包结构可能不正确" -ForegroundColor Yellow
        $script:WarningCount++
    }
}

Write-Host ""

# 总结
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "检查完成" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "错误数: $ErrorCount" -ForegroundColor $(if ($ErrorCount -eq 0) { "Green" } else { "Red" })
Write-Host "警告数: $WarningCount" -ForegroundColor $(if ($WarningCount -eq 0) { "Green" } else { "Yellow" })
Write-Host ""

if ($ErrorCount -eq 0 -and $WarningCount -eq 0) {
    Write-Host "✅ 项目完全符合规范！" -ForegroundColor Green
    exit 0
} elseif ($ErrorCount -eq 0) {
    Write-Host "⚠️  项目基本符合规范，但有警告需要关注" -ForegroundColor Yellow
    exit 0
} else {
    Write-Host "❌ 项目存在规范问题，请修复后重新检查" -ForegroundColor Red
    exit 1
}
