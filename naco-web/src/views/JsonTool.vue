<template>
  <div class="json-tool">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>JSON转换</span>
          <div class="card-actions">
            <el-button type="primary" @click="handleFormat">格式化</el-button>
            <el-button @click="handleClear">清空</el-button>
            <el-button :disabled="!outputText" @click="handleCopyOutput">复制结果</el-button>
            <el-button @click="handleTreeView">树状查看</el-button>
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

      <el-row :gutter="12">
        <el-col :xs="24" :md="12">
          <div class="panel-title">输入</div>
          <el-input
            v-model="inputText"
            type="textarea"
            :rows="18"
            placeholder="请输入任意文本；若为合法 JSON 可格式化输出"
            resize="vertical"
          />
        </el-col>

        <el-col :xs="24" :md="12" class="right-col">
          <div class="panel-title">输出（2 空格缩进）</div>
          <el-input
            v-model="outputText"
            type="textarea"
            :rows="18"
            placeholder="格式化结果会显示在这里"
            resize="vertical"
            readonly
          />
        </el-col>
      </el-row>
    </el-card>

    <el-drawer
      v-model="treeVisible"
      title="JSON 树状结构"
      direction="rtl"
      size="35%"
    >
      <el-scrollbar style="height: 100%">
        <el-tree
          :data="treeData"
          node-key="id"
          :props="treeProps"
          default-expand-all
          highlight-current
        />
      </el-scrollbar>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const inputText = ref('')
const outputText = ref('')
const errorMsg = ref('')

const treeVisible = ref(false)
const treeData = ref([])

const treeProps = {
  children: 'children',
  label: 'label'
}

let idCounter = 0
const nextId = () => {
  idCounter += 1
  return `node-${idCounter}`
}

const buildJsonTreeNode = (value, key) => {
  const node = {
    id: nextId(),
    label: '',
    children: []
  }

  const isArray = Array.isArray(value)
  const isObject = value !== null && typeof value === 'object' && !isArray

  if (isArray || isObject) {
    const typeText = isArray ? '数组' : '对象'
    node.label = key != null && key !== '' ? `${key} (${typeText})` : `(${typeText})`

    if (isArray) {
      value.forEach((item, index) => {
        node.children.push(buildJsonTreeNode(item, `[${index}]`))
      })
    } else {
      Object.keys(value).forEach((k) => {
        node.children.push(buildJsonTreeNode(value[k], k))
      })
    }
  } else {
    const displayKey = key != null && key !== '' ? `${key}: ` : ''
    let displayValue
    if (typeof value === 'string') {
      displayValue = `"${value}"`
    } else if (value === null) {
      displayValue = 'null'
    } else {
      displayValue = String(value)
    }
    node.label = `${displayKey}${displayValue}`
    node.children = []
  }

  return node
}

const buildJsonTree = (value) => {
  idCounter = 0
  return [buildJsonTreeNode(value, 'root')]
}

const handleFormat = () => {
  errorMsg.value = ''

  const raw = (inputText.value ?? '').trim()
  if (!raw) {
    ElMessage.warning('请输入需要转换的内容')
    return
  }

  try {
    const obj = JSON.parse(raw)
    outputText.value = JSON.stringify(obj, null, 2)
    ElMessage.success('格式化成功')
  } catch (e) {
    outputText.value = ''
    const msg = e instanceof Error ? e.message : String(e)
    errorMsg.value = `非合法 JSON：${msg}`
    ElMessage.error('非合法 JSON，无法格式化')
  }
}

const handleTreeView = () => {
  errorMsg.value = ''

  const raw = (inputText.value ?? '').trim()
  if (!raw) {
    ElMessage.warning('请输入 JSON 内容')
    return
  }

  try {
    const obj = JSON.parse(raw)
    treeData.value = buildJsonTree(obj)
    treeVisible.value = true
  } catch (e) {
    treeVisible.value = false
    treeData.value = []
    const msg = e instanceof Error ? e.message : String(e)
    errorMsg.value = `非合法 JSON：${msg}`
    ElMessage.error('非合法 JSON，无法解析为树状结构')
  }
}

const handleClear = () => {
  inputText.value = ''
  outputText.value = ''
  errorMsg.value = ''
}

const fallbackCopy = async (text) => {
  const textarea = document.createElement('textarea')
  textarea.value = text
  textarea.setAttribute('readonly', '')
  textarea.style.position = 'fixed'
  textarea.style.opacity = '0'
  textarea.style.left = '-9999px'
  textarea.style.top = '0'
  document.body.appendChild(textarea)
  textarea.select()
  const ok = document.execCommand('copy')
  document.body.removeChild(textarea)
  return ok
}

const handleCopyOutput = async () => {
  errorMsg.value = ''

  if (!outputText.value) {
    ElMessage.warning('没有可复制的内容')
    return
  }

  try {
    if (navigator.clipboard?.writeText) {
      await navigator.clipboard.writeText(outputText.value)
      ElMessage.success('已复制到剪贴板')
      return
    }
    const ok = await fallbackCopy(outputText.value)
    if (ok) {
      ElMessage.success('已复制到剪贴板')
    } else {
      ElMessage.error('复制失败')
    }
  } catch (e) {
    const msg = e instanceof Error ? e.message : String(e)
    errorMsg.value = `复制失败：${msg}`
    ElMessage.error('复制失败')
  }
}
</script>

<style scoped>
.json-tool {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.card-actions {
  display: flex;
  gap: 10px;
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

@media (min-width: 992px) {
  .right-col {
    margin-top: 0;
  }
}
</style>

