<template>
  <el-dialog
    v-model="dialogVisible"
    title="预约详情"
    width="700px"
    :before-close="handleClose"
  >
    <div v-if="reservationInfo" class="audit-dialog">
      <!-- 用户信息 -->
      <el-descriptions title="用户信息" :column="2" border>
        <el-descriptions-item label="用户名">{{ reservationInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ reservationInfo.realName }}</el-descriptions-item>
      </el-descriptions>

      <!-- 图书信息 -->
      <el-descriptions title="图书信息" :column="1" border class="mt-20">
        <el-descriptions-item label="图书封面">
          <el-image
            :src="reservationInfo.coverImage || '/default-book-cover.jpg'"
            fit="cover"
            style="width: 80px; height: 100px; border-radius: 4px;"
            :preview-src-list="[reservationInfo.coverImage]"
          >
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </el-descriptions-item>
        <el-descriptions-item label="图书名称">{{ reservationInfo.bookName }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ reservationInfo.author }}</el-descriptions-item>
      </el-descriptions>

      <!-- 预约信息 -->
      <el-descriptions title="预约信息" :column="2" border class="mt-20">
        <el-descriptions-item label="预约时间">{{ reservationInfo.reservationDate }}</el-descriptions-item>
        <el-descriptions-item label="预约状态">
          <el-tag :type="getStatusType(reservationInfo.status)">
            {{ getStatusText(reservationInfo.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用户备注" :span="2">
          {{ reservationInfo.remark || '无' }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 审核操作 （仅待审核状态显示） -->
      <div v-if="reservationInfo.status === 'pending'" class="audit-actions mt-20">
        <el-divider content-position="left">审核操作</el-divider>
        <el-form :model="auditForm" label-width="100px">
          <el-form-item label="审核意见">
            <el-input
              v-model="auditForm.remark"
              type="textarea"
              :rows="3"
              placeholder="请填写审核意见或拒绝原因（拒绝时必填）"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <template v-if="reservationInfo && reservationInfo.status === 'pending'">
          <el-button
            type="danger"
            @click="handleReject"
            :loading="rejecting"
          >
            拒绝
          </el-button>
          <el-button
            type="success"
            @click="handleApprove"
            :loading="approving"
          >
            通过
          </el-button>
        </template>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { approveReservation, rejectReservation } from '@/api/reservation'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  reservationInfo: {
    type: Object,
    default: () => null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const dialogVisible = ref(props.modelValue)
const approving = ref(false)
const rejecting = ref(false)

// 审核表单
const auditForm = reactive({
  remark: ''
})

// 监听 modelValue 变化
watch(() => props.modelValue, (newVal) => {
  dialogVisible.value = newVal
  if (newVal) {
    // 重置表单
    auditForm.remark = ''
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

// 通过审核
const handleApprove = async () => {
  try {
    await ElMessageBox.confirm('确认通过该预约申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    approving.value = true
    await approveReservation(props.reservationInfo.id)
    ElMessage.success('审核通过')
    emit('success')
    handleClose()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    approving.value = false
  }
}

// 拒绝审核
const handleReject = async () => {
  if (!auditForm.remark || auditForm.remark.trim() === '') {
    ElMessage.warning('请填写拒绝原因')
    return
  }

  try {
    await ElMessageBox.confirm('确认拒绝该预约申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    rejecting.value = true
    await rejectReservation(props.reservationInfo.id, auditForm.remark)
    ElMessage.success('已拒绝预约')
    emit('success')
    handleClose()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    rejecting.value = false
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    pending: 'warning',
    approved: 'success',
    cancelled: 'info',
    completed: 'primary'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '待审核',
    approved: '已通过',
    cancelled: '已取消',
    completed: '已完成'
  }
  return statusMap[status] || status
}
</script>

<style scoped>
.audit-dialog {
  padding: 10px 0;
}

.mt-20 {
  margin-top: 20px;
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #c0c4cc;
  font-size: 30px;
}

.audit-actions {
  padding: 10px 0;
}
</style>
