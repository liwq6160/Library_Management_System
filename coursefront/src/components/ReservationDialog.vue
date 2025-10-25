<template>
  <el-dialog
    v-model="dialogVisible"
    title="预约图书"
    width="600px"
    :before-close="handleClose"
  >
    <div v-if="bookInfo" class="reservation-dialog">
      <!-- 图书信息 -->
      <div class="book-info">
        <el-image
          :src="bookInfo.coverImage || '/default-book-cover.jpg'"
          fit="cover"
          class="book-cover"
        >
          <template #error>
            <div class="image-error">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-image>
        <div class="book-details">
          <h3>{{ bookInfo.bookName }}</h3>
          <p><strong>作者：</strong>{{ bookInfo.author }}</p>
          <p><strong>出版社：</strong>{{ bookInfo.publisher }}</p>
          <p><strong>当前状态：</strong>
            <el-tag :type="bookInfo.availableCount > 0 ? 'success' : 'danger'">
              {{ bookInfo.availableCount > 0 ? '可借' : '已借出' }}
            </el-tag>
          </p>
          <p v-if="bookInfo.availableCount > 0">
            <strong>可借数量：</strong>{{ bookInfo.availableCount }}
          </p>
        </div>
      </div>

      <!-- 预约规则说明 -->
      <el-alert
        title="预约规则"
        type="info"
        :closable="false"
        class="reservation-rules"
      >
        <ul>
          <li>预约仅适用于已借出的图书，当前有库存的图书请直接借阅</li>
          <li>预约申请需管理员审核，请耐心等待</li>
          <li>图书归还后系统将优先通知预约用户</li>
          <li>收到通知后请及时借阅，否则预约将自动失效</li>
        </ul>
      </el-alert>

      <!-- 预约备注 -->
      <el-form :model="reservationForm" label-width="80px" class="reservation-form">
        <el-form-item label="预约备注">
          <el-input
            v-model="reservationForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请填写预约原因或备注（选填）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :loading="submitting">
          确认预约
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { reserveBook } from '@/api/reservation'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  bookInfo: {
    type: Object,
    default: () => null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const dialogVisible = ref(props.modelValue)
const submitting = ref(false)

// 预约表单
const reservationForm = reactive({
  remark: ''
})

// 监听 modelValue 变化
watch(() => props.modelValue, (newVal) => {
  dialogVisible.value = newVal
  if (newVal) {
    // 重置表单
    reservationForm.remark = ''
  }
})

// 监听 dialogVisible 变化
watch(dialogVisible, (newVal) => {
  emit('update:modelValue', newVal)
})

// 关闭弹窗
const handleClose = () => {
  dialogVisible.value = false
}

// 确认预约
const handleConfirm = async () => {
  if (!props.bookInfo || !props.bookInfo.id) {
    ElMessage.error('图书信息不完整')
    return
  }

  submitting.value = true
  try {
    await reserveBook({
      bookId: props.bookInfo.id,
      remark: reservationForm.remark
    })
    ElMessage.success('预约申请提交成功，请等待管理员审核')
    emit('success')
    handleClose()
  } catch (error) {
    // 错误已经在 axios 拦截器中处理，不需要重复显示
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.reservation-dialog {
  padding: 10px 0;
}

.book-info {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.book-cover {
  width: 120px;
  height: 160px;
  border-radius: 8px;
  flex-shrink: 0;
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #c0c4cc;
  font-size: 40px;
}

.book-details {
  flex: 1;
}

.book-details h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #303133;
}

.book-details p {
  margin: 8px 0;
  font-size: 14px;
  color: #606266;
}

.reservation-rules {
  margin-bottom: 20px;
}

.reservation-rules ul {
  margin: 10px 0 0 0;
  padding-left: 20px;
}

.reservation-rules li {
  margin: 5px 0;
  font-size: 13px;
  line-height: 1.6;
}

.reservation-form {
  margin-top: 20px;
}
</style>
