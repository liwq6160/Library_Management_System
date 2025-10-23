<template>
  <div class="book-list">
    <el-card>
      <template #header>
        <span>图书列表</span>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="书名">
          <el-input v-model="searchForm.bookName" placeholder="请输入书名" clearable />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="searchForm.author" placeholder="请输入作者" clearable />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="searchForm.publisher" placeholder="请输入出版社" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 图书卡片列表 -->
      <div class="book-grid">
        <el-card
          v-for="book in bookList"
          :key="book.id"
          class="book-card"
          shadow="hover"
          @click="goToDetail(book.id)"
        >
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
            <el-tag
              v-if="book.status === 0"
              type="info"
              class="status-tag"
            >
              下架
            </el-tag>
            <el-tag
              v-else-if="book.availableCount === 0"
              type="danger"
              class="status-tag"
            >
              已借出
            </el-tag>
            <el-tag
              v-else
              type="success"
              class="status-tag"
            >
              可借
            </el-tag>
          </div>
          <div class="book-info">
            <h3 class="book-name" :title="book.bookName">{{ book.bookName }}</h3>
            <p class="book-author">作者：{{ book.author }}</p>
            <p class="book-category">分类：{{ book.categoryName }}</p>
            <p class="book-count">
              <span>馆藏：{{ book.totalCount }}</span>
              <span class="available">可借：{{ book.availableCount }}</span>
            </p>
            <p class="book-price">价格：¥{{ book.price }}</p>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="bookList.length === 0" description="暂无图书" />

      <!-- 分页 -->
      <el-pagination
        v-if="bookList.length > 0"
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[12, 24, 48, 96]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { useBookStore } from '@/store/book'
import { useCategoryStore } from '@/store/category'

const router = useRouter()
const bookStore = useBookStore()
const categoryStore = useCategoryStore()

// 搜索表单
const searchForm = reactive({
  bookName: '',
  author: '',
  publisher: '',
  categoryId: null,
  status: 1 // 默认只显示上架的图书
})

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 12,
  total: 0
})

// 图书列表
const bookList = ref([])
const categories = ref([])

// 获取数据
const fetchData = async () => {
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      bookName: searchForm.bookName || undefined,
      author: searchForm.author || undefined,
      publisher: searchForm.publisher || undefined,
      categoryId: searchForm.categoryId || undefined,
      status: searchForm.status !== null ? searchForm.status : undefined
    }
    const res = await bookStore.fetchBookList(params)
    bookList.value = res.records
    pagination.total = res.total
  } catch (error) {
    ElMessage.error('获取图书列表失败')
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    categories.value = await categoryStore.fetchAllCategories()
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.bookName = ''
  searchForm.author = ''
  searchForm.publisher = ''
  searchForm.categoryId = null
  searchForm.status = 1
  pagination.pageNum = 1
  fetchData()
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/books/${id}`)
}

onMounted(() => {
  fetchCategories()
  fetchData()
})
</script>

<style scoped>
.book-list {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.book-card {
  cursor: pointer;
  transition: all 0.3s;
}

.book-card:hover {
  transform: translateY(-5px);
}

.book-cover {
  position: relative;
  width: 100%;
  height: 300px;
  margin-bottom: 15px;
  border-radius: 4px;
  overflow: hidden;
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
  font-size: 50px;
}

.status-tag {
  position: absolute;
  top: 10px;
  right: 10px;
}

.book-info {
  padding: 0 10px;
}

.book-name {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author,
.book-category,
.book-count,
.book-price {
  font-size: 14px;
  color: #666;
  margin: 5px 0;
}

.book-count {
  display: flex;
  justify-content: space-between;
}

.book-count .available {
  color: #67c23a;
  font-weight: bold;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
