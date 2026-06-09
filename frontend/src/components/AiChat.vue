<template>
  <div class="ai-chat">
    <!-- 悬浮按钮 -->
    <div class="chat-trigger" :class="{ open: isOpen }" @click="isOpen = !isOpen">
      <el-icon :size="24"><ChatDotRound /></el-icon>
    </div>

    <!-- 聊天面板 -->
    <Transition name="chat-slide">
      <div v-if="isOpen" class="chat-panel">
        <div class="chat-header">
          <span>AI 智能导购</span>
          <div>
            <el-button link title="清空对话" @click="clearChat">
              <el-icon><Delete /></el-icon>
            </el-button>
            <el-button link @click="isOpen = false">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>

        <div class="chat-body" ref="chatBodyRef">
          <div class="message system">
            <div class="bubble">你好！我是书小助，有什么图书想了解的尽管问我~</div>
          </div>
          <div v-for="(msg, i) in messages" :key="i" class="message" :class="msg.role">
            <div class="bubble" v-html="formatText(msg.content)"></div>
          </div>
          <div v-if="loading" class="message assistant">
            <div class="bubble typing">思考中<span class="dots">...</span></div>
          </div>
        </div>

        <div class="chat-footer">
          <el-input
            v-model="input"
            placeholder="输入你的问题..."
            @keydown.enter.exact.prevent="send"
            :disabled="loading"
            size="large"
          >
            <template #append>
              <el-button @click="send" :disabled="loading || !input.trim()">
                <el-icon><Promotion /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import request from '@/api'

const isOpen = ref(false)
const input = ref('')
const loading = ref(false)
const messages = ref([])
const chatBodyRef = ref(null)

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
    }
  })
}

const formatText = (text) => {
  return text.replace(/\n/g, '<br>')
}

const clearChat = async () => {
  messages.value = []
  try {
    await request.post('/api/user/ai/clear')
  } catch { /* ignore */ }
}

const send = async () => {
  const msg = input.value.trim()
  if (!msg || loading.value) return

  messages.value.push({ role: 'user', content: msg })
  input.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const res = await request.post('/api/user/ai/chat', { message: msg })
    if (res.data.code === 200) {
      messages.value.push({ role: 'assistant', content: res.data.data.reply })
    } else {
      messages.value.push({ role: 'assistant', content: '抱歉，暂时无法回答，请稍后再试。' })
    }
  } catch {
    messages.value.push({ role: 'assistant', content: '网络异常，请稍后再试。' })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}
</script>

<style scoped>
.ai-chat {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 9999;
}

.chat-trigger {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: var(--app-text);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(0,0,0,0.2);
  transition: transform 0.2s;
}
.chat-trigger:hover {
  transform: scale(1.08);
}
.chat-trigger.open {
  background: #666;
}

.chat-panel {
  position: absolute;
  bottom: 64px;
  right: 0;
  width: 380px;
  height: 500px;
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 14px 16px;
  font-weight: 700;
  font-size: 15px;
  border-bottom: 1px solid var(--app-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--app-bg);
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.message {
  display: flex;
}
.message.user {
  justify-content: flex-end;
}
.message.user .bubble {
  background: var(--app-text);
  color: white;
  border-radius: 14px 14px 2px 14px;
}
.message.assistant .bubble,
.message.system .bubble {
  background: var(--app-bg);
  color: var(--app-text);
  border: 1px solid var(--app-border);
  border-radius: 14px 14px 14px 2px;
}
.bubble {
  max-width: 80%;
  padding: 10px 14px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}
.typing .dots {
  animation: blink 1s infinite;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.chat-footer {
  padding: 10px 12px;
  border-top: 1px solid var(--app-border);
  background: var(--app-bg);
}

.chat-slide-enter-active,
.chat-slide-leave-active {
  transition: all 0.25s ease;
}
.chat-slide-enter-from,
.chat-slide-leave-to {
  opacity: 0;
  transform: translateY(12px);
}
</style>
