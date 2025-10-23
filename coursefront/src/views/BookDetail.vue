<template>
  <div class="book-detail">
    <el-card v-if="book">
      <template #header>
        <div class="card-header">
          <span>图书详情</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <div class="detail-content">
        <!-- 左侧封面 -->
        <div class="book-cover">
          <el-image
            :src="book.coverImage || '/default-book-cover.jpg'"
            fit="cover"
            class="cover-image"
          >
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>

        <!-- 右侧信息 -->
        <div class="book-info">
          <h1 class="book-title">{{ book.bookName }}</h1>

          <el-descriptions :column="1" border class="info-descriptions">
            <el-descriptions-item label="作者">{{ book.author }}</el-descriptions-item>
            <el-descriptions-item label="出版社">{{ book.publisher || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="ISBN">{{ book.isbn || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="分类">{{ book.categoryName }}</el-descriptions-item>
            <el-descriptions-item label="价格">¥{{ book.price }}</el-descriptions-item>
            <el-descriptions-item label="出版日期">{{ book.publishDate || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="馆藏总数">{{ book.totalCount }}</el-descriptions-item>
            <el-descriptions-item label="可借数量">
              <el-tag :type="book.availableCount > 0 ? 'success' : 'danger'">
                {{ book.availableCount }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="book.status === 1 ? 'success' : 'info'">
                {{ book.status === 1 ? '上架' : '下架' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button
              type="primary"
              size="large"
              :disabled="book.status === 0 || book.availableCount === 0"
              @click="handleBorrow"
            >
              <el-icon><Reading /></el-icon>
              借阅图书
            </el-button>
            <el-button
              type="warning"
              size="large"
              :disabled="book.status === 0 || book.availableCount > 0"
              @click="handleReserve"
            >
              <el-icon><Clock /></el-icon>
              预约图书
            </el-button>
          </div>
        </div>
      </div>

      <!-- 图书简介 -->
      <el-divider>图书简介</el-divider>
      <div class="book-description">
        <p v-if="book.description">{{ book.description }}</p>
        <el-empty v-else description="暂无简介" :image-size="100" />
      </div>
    </el-card>

    <!-- 加载状态 -->
    <el-card v-else>
      <el-skeleton :rows="10" animated />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture, Reading, Clock } from '@element-plus/icons-vue'
import { useBookStore } from '@/store/book'

const route = useRoute()
const router = useRouter()
const bookStore = useBookStore()

const book = ref(null)

// 获取图书详情
const fetchBookDetail = async () => {
  try {
    const id = route.params.id
    book.value = await bookStore.fetchBookDetail(id)
  } catch (error) {
    ElMessage.error('获取图书详情失败')
    router.push('/books')
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 借阅图书
const handleBorrow = () => {
  ElMessage.info('借阅功能将在借阅管理模块中实现')
  // TODO: 实现借阅功能
}

// 预约图书
const handleReserve = () => {
  ElMessage.info('预约功能将在预约管理模块中实现')
  // TODO: 实现预约功能
}

onMounted(() => {
  fetchBookDetail()
})
</script>

<style scoped>
.book-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-content {
  display: flex;
  gap: 40px;
  margin-bottom: 30px;
}

.book-cover {
  flex-shrink: 0;
  width: 300px;
  height: 400px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.cover-image {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 80px;
}

.book-info {
  flex: 1;
}

.book-title {
  font-size: 28px;
  font-weight: bold;
  margin: 0 0 20px 0;
}

.info-descriptions {
  margin-bottom: 30px;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.book-description {
  font-size: 16px;
  line-height: 1.8;
  color: #666;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
  min-height: 150px;
}

.book-description p {
  margin: 0;
  text-indent: 2em;
}
</style>
