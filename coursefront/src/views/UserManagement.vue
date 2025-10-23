<template>
  <div class="user-management-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="真实姓名">
            <el-input v-model="searchForm.realName" placeholder="请输入真实姓名" clearable />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="searchForm.role" placeholder="请选择角色" clearable>
              <el-option label="管理员" value="admin" />
              <el-option label="普通用户" value="user" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 用户列表表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'danger' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadUserList"
        @current-change="loadUserList"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 用户详情/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" :disabled="dialogMode === 'view'" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" :disabled="dialogMode === 'view'" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" :disabled="dialogMode === 'view'" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" :disabled="dialogMode === 'view'" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-tag :type="userForm.status === 1 ? 'success' : 'danger'">
            {{ userForm.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="创建时间">
          <span>{{ userForm.createTime }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button v-if="dialogMode === 'edit'" type="primary" @click="handleSave">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import userApi from '@/api/user'

// 搜索表单
const searchForm = reactive({
  username: '',
  realName: '',
  role: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogMode = ref('view') // view 或 edit
const dialogTitle = ref('')

// 用户表单
const userForm = reactive({
  id: null,
  username: '',
  realName: '',
  phone: '',
  email: '',
  role: '',
  status: 1,
  createTime: ''
})

const userFormRef = ref(null)

// 用户表单验证规则
const userRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 加载用户列表
const loadUserList = async () => {
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const res = await userApi.getUserList(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载用户列表失败：', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadUserList()
}

// 重置
const handleReset = () => {
  searchForm.username = ''
  searchForm.realName = ''
  searchForm.role = ''
  pagination.page = 1
  loadUserList()
}

// 查看
const handleView = async (row) => {
  try {
    const res = await userApi.getUserDetail(row.id)
    Object.assign(userForm, res.data)
    dialogMode.value = 'view'
    dialogTitle.value = '用户详情'
    dialogVisible.value = true
  } catch (error) {
    console.error('获取用户详情失败：', error)
  }
}

// 编辑
const handleEdit = async (row) => {
  try {
    const res = await userApi.getUserDetail(row.id)
    Object.assign(userForm, res.data)
    dialogMode.value = 'edit'
    dialogTitle.value = '编辑用户'
    dialogVisible.value = true
  } catch (error) {
    console.error('获取用户详情失败：', error)
  }
}

// 保存
const handleSave = async () => {
  if (!userFormRef.value) return

  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const { id, username, status, createTime, updateTime, ...data } = userForm
        data.role = userForm.role
        await userApi.updateUser(id, data)
        ElMessage.success('修改成功')
        dialogVisible.value = false
        loadUserList()
      } catch (error) {
        console.error('修改用户失败：', error)
      }
    }
  })
}

// 切换用户状态
const handleToggleStatus = (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'

  ElMessageBox.confirm(`确定要${statusText}该用户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await userApi.updateUserStatus(row.id, newStatus)
      ElMessage.success(`${statusText}成功`)
      loadUserList()
    } catch (error) {
      console.error('修改用户状态失败：', error)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.user-management-container {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
}
</style>
