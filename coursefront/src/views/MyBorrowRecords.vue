<template>
  <div class="my-borrow-records">
    <el-card>
      <template #header>
        <span>我的借阅记录</span>
      </template>

      <!-- 筛选栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="借阅中" value="borrowing" />
            <el-option label="已归还" value="returned" />
            <el-option label="已逾期" value="overdue" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 借阅记录表格 -->
      <el-table :data="borrowList" stripe border>
        <el-table-column prop="id" label="借阅ID" width="80" />
        <el-table-column label="图书封面" width="100">
          <template #default="{ row }">
            <el-image
              :src="row.coverImage || '/default-book-cover.jpg'"
              fit="cover"
              style="width: 60px; height: 80px; border-radius: 4px;"
              :preview-src-list="[row.coverImage]"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="bookName" label="图书名称" min-width="150" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="borrowDate" label="借阅日期" width="180" />
        <el-table-column prop="dueDate" label="应还日期" width="180">
          <template #default="{ row }">
            <span :class="{'overdue-text': isOverdue(row)}">
              {{ row.dueDate }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="returnDate" label="归还日期" width="180">
          <template #default="{ row }">
            {{ row.returnDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="续借次数" width="100">
          <template #default="{ row }">
            {{ row.renewCount }} 次
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'borrowing'" type="primary">借阅中</el-tag>
            <el-tag v-else-if="row.status === 'returned'" type="success">已归还</el-tag>
            <el-tag v-else-if="row.status === 'overdue'" type="danger">已逾期</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="逾期天数" width="100">
          <template #default="{ row }">
            <span v-if="row.overdueDays > 0" class="overdue-text">
              {{ row.overdueDays }} 天
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'borrowing'"
              type="success"
              size="small"
              @click="handleReturn(row)"
            >
              归还
            </el-button>
            <el-button
              v-if="row.status === 'borrowing' && row.renewCount < 1"
              type="primary"
              size="small"
              @click="handleRenew(row)"
            >
              续借
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        class="pagination"
      />
    </el-card>

    <!-- 借阅详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="借阅详情"
      width="600px"
    >
      <el-descriptions v-if="currentRecord" :column="1" border>
        <el-descriptions-item label="借阅ID">{{ currentRecord.id }}</el-descriptions-item>
        <el-descriptions-item label="图书名称">{{ currentRecord.bookName }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ currentRecord.author }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ currentRecord.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="借阅日期">{{ currentRecord.borrowDate }}</el-descriptions-item>
        <el-descriptions-item label="应还日期">{{ currentRecord.dueDate }}</el-descriptions-item>
        <el-descriptions-item label="归还日期">{{ currentRecord.returnDate || '未归还' }}</el-descriptions-item>
        <el-descriptions-item label="续借次数">{{ currentRecord.renewCount }} 次</el-descriptions-item>
        <el-descriptions-item label="逾期天数">{{ currentRecord.overdueDays }} 天</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentRecord.status === 'borrowing'" type="primary">借阅中</el-tag>
          <el-tag v-else-if="currentRecord.status === 'returned'" type="success">已归还</el-tag>
          <el-tag v-else-if="currentRecord.status === 'overdue'" type="danger">已逾期</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { useBorrowStore } from '@/store/borrow'

const borrowStore = useBorrowStore()

// 搜索表单
const searchForm = reactive({
  status: ''
})

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 借阅列表
const borrowList = ref([])

// 详情对话框
const detailDialogVisible = ref(false)
const currentRecord = ref(null)

// 获取数据
const fetchData = async () => {
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      status: searchForm.status || undefined
    }
    const res = await borrowStore.fetchMyBorrowRecords(params)
    borrowList.value = res.records
    pagination.total = res.total
  } catch (error) {
    ElMessage.error('获取借阅记录失败')
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

// 重置
const handleReset = () => {
  searchForm.status = ''
  pagination.pageNum = 1
  fetchData()
}

// 判断是否逾期
const isOverdue = (row) => {
  if (row.status === 'overdue') return true
  if (row.status === 'borrowing' && row.dueDate) {
    return new Date(row.dueDate) < new Date()
  }
  return false
}

// 归还图书
const handleReturn = async (row) => {
  try {
    await ElMessageBox.confirm('确认归还该图书吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await borrowStore.returnBook(row.id)
    ElMessage.success('归还成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '归还失败')
    }
  }
}

// 续借图书
const handleRenew = async (row) => {
  try {
    await ElMessageBox.confirm('确认续借该图书吗？续借期限为15天', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await borrowStore.renewBook(row.id)
    ElMessage.success('续借成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '续借失败')
    }
  }
}

// 查看详情
const handleViewDetail = (row) => {
  currentRecord.value = row
  detailDialogVisible.value = true
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.my-borrow-records {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.image-error {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}

.overdue-text {
  color: #f56c6c;
  font-weight: bold;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
