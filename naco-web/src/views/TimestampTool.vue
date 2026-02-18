<template>
  <div class="timestamp-tool">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>时间戳转换</span>
          <div class="card-actions">
            <el-radio-group v-model="unit" size="small">
              <el-radio-button label="ms">毫秒(ms)</el-radio-button>
              <el-radio-button label="s">秒(s)</el-radio-button>
            </el-radio-group>
            <el-button size="small" @click="fillNowTimestamp">当前时间戳</el-button>
            <el-button size="small" @click="fillNowDatetime">当前时间</el-button>
            <el-button size="small" @click="handleClear">清空</el-button>
          </div>
        </div>
      </template>

      <el-alert
        v-if="errorMsg"
        :title="errorMsg"
        type="error"
        show-icon
        :closable="true"
        style="margin-bottom: 12px"
        @close="errorMsg = ''"
      />

      <div class="now-line">
        <div class="now-item">
          <span class="now-label">当前本地时间：</span>
          <span class="now-value">{{ nowDatetime }}</span>
        </div>
        <div class="now-item">
          <span class="now-label">当前时间戳（{{ unit }}）：</span>
          <span class="now-value">{{ nowTimestamp }}</span>
        </div>
      </div>

      <el-divider content-position="left">模块 A：时间戳 → 本地时间字符串</el-divider>

      <el-row :gutter="12">
        <el-col :xs="24" :md="12">
          <div class="panel-title">输入时间戳（{{ unit }}）</div>
          <el-input
            v-model="timestampInput"
            placeholder="请输入数字，如 1700000000000 或 1700000000"
            clearable
          >
            <template #append>
              <el-button type="primary" @click="convertTsToDatetime">转换</el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :md="12" class="right-col">
          <div class="panel-title">输出本地时间（YYYY-MM-DD HH:mm:ss）</div>
          <el-input v-model="datetimeOutput" placeholder="转换结果会显示在这里" readonly />
        </el-col>
      </el-row>

      <el-divider content-position="left">模块 B：本地时间字符串 → 时间戳</el-divider>

      <el-row :gutter="12">
        <el-col :xs="24" :md="12">
          <div class="panel-title">输入本地时间（YYYY-MM-DD HH:mm:ss）</div>
          <el-input
            v-model="datetimeInput"
            placeholder="例如 2026-02-17 08:30:00"
            clearable
          >
            <template #append>
              <el-button type="primary" @click="convertDatetimeToTs">转换</el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :md="12" class="right-col">
          <div class="panel-title">输出时间戳（{{ unit }}）</div>
          <el-input v-model="timestampOutput" placeholder="转换结果会显示在这里" readonly />
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

const unit = ref('ms') // 'ms' | 's'
const errorMsg = ref('')

const timestampInput = ref('')
const datetimeOutput = ref('')

const datetimeInput = ref('')
const timestampOutput = ref('')

const pad2 = (n) => String(n).padStart(2, '0')
const formatDateTime = (date) => {
  const y = date.getFullYear()
  const m = pad2(date.getMonth() + 1)
  const d = pad2(date.getDate())
  const hh = pad2(date.getHours())
  const mm = pad2(date.getMinutes())
  const ss = pad2(date.getSeconds())
  return `${y}-${m}-${d} ${hh}:${mm}:${ss}`
}

const DATE_TIME_RE = /^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/

const parseDateTime = (raw) => {
  const m = DATE_TIME_RE.exec(raw)
  if (!m) {
    return { ok: false, message: '时间格式不正确，请使用 YYYY-MM-DD HH:mm:ss' }
  }
  const y = Number(m[1])
  const mo = Number(m[2])
  const d = Number(m[3])
  const hh = Number(m[4])
  const mm = Number(m[5])
  const ss = Number(m[6])

  const date = new Date(y, mo - 1, d, hh, mm, ss)
  if (
    date.getFullYear() !== y ||
    date.getMonth() !== mo - 1 ||
    date.getDate() !== d ||
    date.getHours() !== hh ||
    date.getMinutes() !== mm ||
    date.getSeconds() !== ss
  ) {
    return { ok: false, message: '日期或时间不合法（可能存在越界，如 2026-02-30）' }
  }
  return { ok: true, date }
}

const MAX_DATE_MS = 8.64e15 // Date 可表示的最大毫秒范围量级

const toMillis = (num) => (unit.value === 's' ? num * 1000 : num)
const fromMillis = (ms) => (unit.value === 's' ? Math.floor(ms / 1000) : ms)

const convertTsToDatetime = () => {
  errorMsg.value = ''
  const raw = (timestampInput.value ?? '').trim()
  if (!raw) {
    ElMessage.warning('请输入时间戳')
    return
  }

  const n = Number(raw)
  if (!Number.isFinite(n)) {
    errorMsg.value = '时间戳必须是合法数字'
    ElMessage.error('时间戳输入不合法')
    datetimeOutput.value = ''
    return
  }

  const ms = toMillis(n)
  if (!Number.isFinite(ms) || Math.abs(ms) > MAX_DATE_MS) {
    errorMsg.value = '时间戳超出可转换范围'
    ElMessage.error('时间戳超出范围')
    datetimeOutput.value = ''
    return
  }

  const date = new Date(ms)
  if (Number.isNaN(date.getTime())) {
    errorMsg.value = '时间戳无法转换为有效日期'
    ElMessage.error('转换失败')
    datetimeOutput.value = ''
    return
  }

  datetimeOutput.value = formatDateTime(date)
  ElMessage.success('转换成功')
}

const convertDatetimeToTs = () => {
  errorMsg.value = ''
  const raw = (datetimeInput.value ?? '').trim()
  if (!raw) {
    ElMessage.warning('请输入本地时间字符串')
    return
  }

  const parsed = parseDateTime(raw)
  if (!parsed.ok) {
    errorMsg.value = parsed.message
    ElMessage.error('时间字符串输入不合法')
    timestampOutput.value = ''
    return
  }

  const ms = parsed.date.getTime()
  if (!Number.isFinite(ms) || Math.abs(ms) > MAX_DATE_MS) {
    errorMsg.value = '时间超出可转换范围'
    ElMessage.error('转换失败')
    timestampOutput.value = ''
    return
  }

  timestampOutput.value = String(fromMillis(ms))
  ElMessage.success('转换成功')
}

const handleClear = () => {
  errorMsg.value = ''
  timestampInput.value = ''
  datetimeOutput.value = ''
  datetimeInput.value = ''
  timestampOutput.value = ''
}

const now = ref(Date.now())
let timer = null
onMounted(() => {
  timer = window.setInterval(() => {
    now.value = Date.now()
  }, 1000)
})
onBeforeUnmount(() => {
  if (timer) window.clearInterval(timer)
  timer = null
})

const nowDatetime = computed(() => formatDateTime(new Date(now.value)))
const nowTimestamp = computed(() => String(fromMillis(now.value)))

const fillNowTimestamp = () => {
  errorMsg.value = ''
  timestampInput.value = nowTimestamp.value
  convertTsToDatetime()
}

const fillNowDatetime = () => {
  errorMsg.value = ''
  datetimeInput.value = nowDatetime.value
  convertDatetimeToTs()
}
</script>

<style scoped>
.timestamp-tool {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  gap: 12px;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.right-col {
  margin-top: 12px;
}

.now-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}

.now-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.now-label {
  color: #606266;
}

.now-value {
  font-weight: 600;
  color: #303133;
}

@media (min-width: 992px) {
  .right-col {
    margin-top: 0;
  }
}
</style>

