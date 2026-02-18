<template>
  <div class="regex-tool">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>正则匹配测试</span>
          <div class="card-actions">
            <el-input
              v-model="pattern"
              class="pattern-input"
              placeholder="请输入正则表达式，例如：^\\w+@\\w+\\.com$"
              clearable
            >
              <template #prepend>/</template>
            </el-input>

            <el-button type="primary" @click="handleTest">测试匹配</el-button>
            <el-button @click="handleClear">清空</el-button>
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

      <div class="panel">
        <div class="panel-title">测试文本</div>
        <el-input
          v-model="testText"
          type="textarea"
          :rows="10"
          placeholder="在这里输入要进行匹配测试的文本"
          resize="vertical"
        />
      </div>

      <el-row :gutter="12" style="margin-top: 16px">
        <el-col :xs="24" :md="12">
          <div class="panel-title">匹配结果</div>

          <el-table
            v-if="matches.length"
            :data="matches"
            border
            size="small"
            style="width: 100%"
          >
            <el-table-column
              prop="order"
              label="#"
              width="60"
            />
            <el-table-column
              prop="match"
              label="匹配内容"
              min-width="160"
              show-overflow-tooltip
            />
            <el-table-column
              prop="rangeText"
              label="位置（起-止）"
              width="140"
            />
            <el-table-column label="分组内容" min-width="160">
              <template #default="{ row }">
                <span v-if="!row.groups || !row.groups.length">—</span>
                <div v-else class="group-tags">
                  <el-tag
                    v-for="(g, idx) in row.groups"
                    :key="idx"
                    size="small"
                  >
                    ${{ idx + 1 }}: {{ g === undefined ? 'undefined' : g }}
                  </el-tag>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <el-empty
            v-else
            description="暂无匹配结果"
            :image-size="80"
          />
        </el-col>

        <el-col :xs="24" :md="12">
          <div class="panel-title">高亮预览</div>
          <div class="preview-box">
            <span
              v-for="(seg, index) in highlightedSegments"
              :key="index"
              :class="['preview-segment', { 'preview-segment--match': seg.isMatch }]"
            >
              {{ seg.text }}
            </span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'

const pattern = ref('')
const testText = ref('')
const errorMsg = ref('')

const matches = ref([])

const highlightedSegments = computed(() => {
  const text = testText.value || ''
  if (!text) {
    return []
  }

  if (!matches.value.length) {
    return [
      {
        text,
        isMatch: false
      }
    ]
  }

  const segments = []
  let cursor = 0

  const ordered = [...matches.value].sort((a, b) => a.index - b.index)

  ordered.forEach((m) => {
    if (m.index > cursor) {
      segments.push({
        text: text.slice(cursor, m.index),
        isMatch: false
      })
    }

    if (m.endIndex > m.index) {
      segments.push({
        text: text.slice(m.index, m.endIndex),
        isMatch: true
      })
    }

    cursor = Math.max(cursor, m.endIndex)
  })

  if (cursor < text.length) {
    segments.push({
      text: text.slice(cursor),
      isMatch: false
    })
  }

  return segments
})

const buildMatches = (regexp, text) => {
  const result = []
  const flags = regexp.flags || ''
  let index = 1

  if (flags.includes('g')) {
    let match
    let lastIndex = -1

    while ((match = regexp.exec(text)) !== null) {
      // 避免零宽匹配导致死循环
      if (match[0] === '' && regexp.lastIndex === lastIndex) {
        regexp.lastIndex++
        if (regexp.lastIndex > text.length) {
          break
        }
        continue
      }
      lastIndex = regexp.lastIndex

      if (!match[0]) {
        continue
      }

      const start = match.index
      const end = start + match[0].length

      result.push({
        order: index++,
        match: match[0],
        index: start,
        endIndex: end,
        rangeText: `${start} - ${end}`,
        groups: match.length > 1 ? match.slice(1) : []
      })
    }
  } else {
    const match = regexp.exec(text)
    if (match && match[0]) {
      const start = match.index
      const end = start + match[0].length
      result.push({
        order: 1,
        match: match[0],
        index: start,
        endIndex: end,
        rangeText: `${start} - ${end}`,
        groups: match.length > 1 ? match.slice(1) : []
      })
    }
  }

  return result
}

const handleTest = () => {
  errorMsg.value = ''
  matches.value = []

  const rawPattern = (pattern.value || '').trim()
  const text = testText.value || ''

  if (!rawPattern) {
    ElMessage.warning('请输入正则表达式')
    return
  }

  try {
    // 默认使用全局匹配模式，用户暂不自定义 flags
    const regexp = new RegExp(rawPattern, 'g')

    const result = buildMatches(regexp, text)
    matches.value = result

    if (!result.length) {
      ElMessage.info('未匹配到任何结果')
    } else {
      ElMessage.success(`共匹配到 ${result.length} 个结果`)
    }
  } catch (e) {
    const msg = e instanceof Error ? e.message : String(e)
    errorMsg.value = `正则语法错误：${msg}`
    ElMessage.error('正则语法错误，请检查表达式与标志位')
  }
}

const handleClear = () => {
  pattern.value = ''
  testText.value = ''
  matches.value = []
  errorMsg.value = ''
}
</script>

<style scoped>
.regex-tool {
  padding: 20px;
}

.card-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 18px;
  font-weight: bold;
}

.card-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.pattern-input {
  min-width: 260px;
  max-width: 420px;
}

.flags-group {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px 8px;
}

.panel {
  margin-top: 8px;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.preview-box {
  min-height: 120px;
  padding: 8px 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #f9fafc;
  white-space: pre-wrap;
  word-break: break-all;
}

.preview-segment--match {
  background-color: #ffe58f;
}

.group-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

@media (max-width: 768px) {
  .pattern-input {
    flex: 1 1 100%;
    max-width: 100%;
  }
}
</style>

