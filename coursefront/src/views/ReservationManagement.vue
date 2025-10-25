<template>
  <div class="reservation-management">
    <el-card>
      <template #header>
        <span>预约管理</span>
      </template>

      <!-- 筛选栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="图书名">
          <el-input v-model="searchForm.bookName" placeholder="请输入图书名" clearable />
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="searchForm.reservationDate"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            clearable
          />
        </el-form-item>
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

      <!-- 预约记录表格 -->
      <el-table :data="reservationList" stripe border v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
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
        <el-table-column prop="username" label="预约用户" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="reservationDate" label="预约时间" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleViewDetail(row)"
            >
              查看详情
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              size="small"
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              size="small"
              @click="handleReject(row)"
            >
              拒绝
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

    <!-- 审核弹窗 -->
    <reservation-audit-dialog
      v-model="auditDialogVisible"
      :reservation-info="currentReservation"
      @success="handleAuditSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getAllReservations, approveReservation, rejectReservation } from '@/api/reservation'
import ReservationAuditDialog from '@/components/ReservationAuditDialog.vue'

// 搜索表单
const searchForm = reactive({
  username: '',
  bookName: '',
  reservationDate: null,
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

// 审核弹窗
const auditDialogVisible = ref(false)
const currentReservation = ref(null)

// 获取预约记录列表
const fetchReservationList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      status: searchForm.status || undefined
    }
    const response = await getAllReservations(params)
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
  searchForm.username = ''
  searchForm.bookName = ''
  searchForm.reservationDate = null
  searchForm.status = ''
  pagination.pageNum = 1
  fetchReservationList()
}

// 查看详情
const handleViewDetail = (row) => {
  currentReservation.value = row
  auditDialogVisible.value = true
}

// 通过预约
const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm('确认通过该预约申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await approveReservation(row.id)
    ElMessage.success('审核通过')
    fetchReservationList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

// 拒绝预约
const handleReject = async (row) => {
  try {
    const { value: remark } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入拒绝原因',
      inputValidator: (value) => {
        if (!value || value.trim() === '') {
          return '请输入拒绝原因'
        }
        return true
      }
    })
    await rejectReservation(row.id, remark)
    ElMessage.success('已拒绝预约')
    fetchReservationList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

// 审核成功回调
const handleAuditSuccess = () => {
  fetchReservationList()
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
.reservation-management {
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
