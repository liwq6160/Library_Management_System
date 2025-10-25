<template>
  <div v-if="notifications.length > 0" class="reservation-notifications">
    <el-alert
      v-for="item in notifications"
      :key="item.id"
      :title="`您预约的《${item.bookName}》已归还，可以借阅了！`"
      type="success"
      :closable="true"
      @close="handleClose(item.id)"
      class="notification-item"
    >
      <template #default>
        <div class="notification-content">
          <div class="book-info">
            <el-image
              :src="item.coverImage || '/default-book-cover.jpg'"
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
              <p class="book-name">{{ item.bookName }}</p>
              <p class="book-author">作者：{{ item.author }}</p>
              <p class="notification-time">通知时间：{{ item.updateTime }}</p>
            </div>
          </div>
          <div class="notification-actions">
            <el-button type="primary" size="small" @click="handleGoBorrow(item)">
              立即借阅
            </el-button>
          </div>
        </div>
      </template>
    </el-alert>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getMyReservations } from '@/api/reservation'
import { useRouter } from 'vue-router'

const router = useRouter()
const notifications = ref([])

// 获取已完成的预约通知
const fetchNotifications = async () => {
  try {
    const response = await getMyReservations({
      pageNum: 1,
      pageSize: 10,
      status: 'completed'
    })
    notifications.value = response.data.records || []
  } catch (error) {
    console.error('获取预约通知失败:', error)
  }
}

// 关闭通知
const handleClose = (id) => {
  const index = notifications.value.findIndex(item => item.id === id)
  if (index !== -1) {
    notifications.value.splice(index, 1)
  }
  // 这里可以调用API标记通知为已读，但根据当前需求，completed状态的预约记录已经是最终状态
  // 可以考虑在后端添加一个"已读"标记字段来优化
}

// 去借阅
const handleGoBorrow = (item) => {
  router.push(`/book/${item.bookId}`)
}

onMounted(() => {
  fetchNotifications()
  // 可以设置定时刷新通知
  setInterval(() => {
    fetchNotifications()
  }, 60000) // 每分钟刷新一次
})
</script>

<style scoped>
.reservation-notifications {
  margin-bottom: 20px;
}

.notification-item {
  margin-bottom: 15px;
}

.notification-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.book-info {
  display: flex;
  gap: 15px;
  align-items: center;
  flex: 1;
}

.book-cover {
  width: 60px;
  height: 80px;
  border-radius: 4px;
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
  font-size: 24px;
}

.book-details {
  flex: 1;
}

.book-name {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.book-author {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #606266;
}

.notification-time {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.notification-actions {
  flex-shrink: 0;
  margin-left: 20px;
}
</style>
