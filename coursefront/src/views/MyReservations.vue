<template>
  <div class="my-reservations">
    <el-card>
      <template #header>
        <span>我的预约记录</span>
      </template>

      <!-- 筛选栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已取消" value="cancelled" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 预约记录列表 -->
      <el-table :data="reservationList" stripe border v-loading="loading">
        <el-table-column prop="id" label="预约ID" width="80" />
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
        <el-table-column prop="reservationDate" label="预约时间" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending' || row.status === 'approved'"
              type="danger"
              size="small"
              @click="handleCancel(row)"
            >
              取消预约
            </el-button>
            <el-button
              v-if="row.status === 'completed'"
              type="primary"
              size="small"
              @click="handleGoBorrow(row)"
            >
              去借阅
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getMyReservations, cancelReservation } from '@/api/reservation'
import { useRouter } from 'vue-router'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  status: ''
})

// 预约记录列表
const reservationList = ref([])
const loading = ref(false)

// 分页配置
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 获取预约记录列表
const fetchReservationList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      status: searchForm.status || undefined
    }
    const response = await getMyReservations(params)
    reservationList.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error(error.message || '获取预约记录失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchReservationList()
}

// 重置
const handleReset = () => {
  searchForm.status = ''
  pagination.pageNum = 1
  fetchReservationList()
}

// 取消预约
const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确认要取消该预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelReservation(row.id)
    ElMessage.success('取消预约成功')
    fetchReservationList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消预约失败')
    }
  }
}

// 去借阅
const handleGoBorrow = (row) => {
  router.push(`/book/${row.bookId}`)
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

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  fetchReservationList()
}

// 页码改变
const handleCurrentChange = (page) => {
  pagination.pageNum = page
  fetchReservationList()
}

onMounted(() => {
  fetchReservationList()
})
</script>

<style scoped>
.my-reservations {
  padding: 20px;
  height: 100vh;
  overflow: auto;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #c0c4cc;
}
</style>
