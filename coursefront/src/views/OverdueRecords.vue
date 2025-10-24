<template>
  <div class="overdue-records">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>逾期记录管理</span>
          <el-tag type="danger" size="large">
            逾期总数: {{ pagination.total }}
          </el-tag>
        </div>
      </template>

      <!-- 逾期记录表格 -->
      <el-table :data="overdueList" stripe border>
        <el-table-column prop="id" label="借阅ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="bookId" label="图书ID" width="80" />
        <el-table-column prop="bookName" label="图书名称" min-width="150" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="borrowDate" label="借阅日期" width="180" />
        <el-table-column prop="dueDate" label="应还日期" width="180">
          <template #default="{ row }">
            <span class="overdue-text">{{ row.dueDate }}</span>
          </template>
        </el-table-column>
        <el-table-column label="逾期天数" width="120">
          <template #default="{ row }">
            <el-tag type="danger" size="large">
              {{ row.overdueDays }} 天
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="续借次数" width="100">
          <template #default="{ row }">
            {{ row.renewCount }} 次
          </template>
        </el-table-column>
        <el-table-column label="联系方式" width="180">
          <template #default="{ row }">
            <div v-if="row.phone">
              <el-icon><Phone /></el-icon>
              {{ row.phone }}
            </div>
            <div v-if="row.email" style="margin-top: 5px;">
              <el-icon><Message /></el-icon>
              {{ row.email }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="success"
              size="small"
              @click="handleReturn(row)"
            >
              归还
            </el-button>
            <el-button
              type="warning"
              size="small"
              @click="handleRemind(row)"
            >
              催还
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
      title="逾期详情"
      width="700px"
    >
      <el-alert
        v-if="currentRecord"
        title="逾期提醒"
        :description="`该图书已逾期 ${currentRecord.overdueDays} 天，请尽快催促用户归还！`"
        type="error"
        :closable="false"
        show-icon
        style="margin-bottom: 20px;"
      />
      <el-descriptions v-if="currentRecord" :column="2" border>
        <el-descriptions-item label="借阅ID">{{ currentRecord.id }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentRecord.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentRecord.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentRecord.realName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRecord.phone || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentRecord.email || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="图书ID">{{ currentRecord.bookId }}</el-descriptions-item>
        <el-descriptions-item label="图书名称">{{ currentRecord.bookName }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ currentRecord.author }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ currentRecord.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="借阅日期" :span="2">{{ currentRecord.borrowDate }}</el-descriptions-item>
        <el-descriptions-item label="应还日期" :span="2">
          <span class="overdue-text">{{ currentRecord.dueDate }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="续借次数">{{ currentRecord.renewCount }} 次</el-descriptions-item>
        <el-descriptions-item label="逾期天数">
          <el-tag type="danger" size="large">{{ currentRecord.overdueDays }} 天</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Phone, Message } from '@element-plus/icons-vue'
import { useBorrowStore } from '@/store/borrow'

const borrowStore = useBorrowStore()

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 逾期列表
const overdueList = ref([])

// 详情对话框
const detailDialogVisible = ref(false)
const currentRecord = ref(null)

// 获取数据
const fetchData = async () => {
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await borrowStore.fetchOverdueRecords(params)
    overdueList.value = res.records
    pagination.total = res.total
  } catch (error) {
    ElMessage.error('获取逾期记录失败')
  }
}

// 归还图书
const handleReturn = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认为用户 ${row.realName} 归还图书《${row.bookName}》吗？该图书已逾期 ${row.overdueDays} 天`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await borrowStore.returnBook(row.id)
    ElMessage.success('归还成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '归还失败')
    }
  }
}

// 催还
const handleRemind = (row) => {
  ElMessageBox.alert(
    `用户：${row.realName} (${row.username})\n图书：《${row.bookName}》\n逾期天数：${row.overdueDays} 天\n\n联系方式：\n电话：${row.phone || '未填写'}\n邮箱：${row.email || '未填写'}\n\n建议通过电话或邮件方式催促用户尽快归还图书。`,
    '催还信息',
    {
      confirmButtonText: '确定',
      type: 'warning'
    }
  )
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
.overdue-records {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
