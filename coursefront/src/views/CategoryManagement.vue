<template>
  <div class="category-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="handleAdd">添加分类</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="分类名称">
          <el-input v-model="searchForm.categoryName" placeholder="请输入分类名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 分类列表表格 -->
      <el-table :data="categoryList" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="categoryName" label="分类名称" />
        <el-table-column prop="description" label="分类描述" />
        <el-table-column prop="sortOrder" label="排序顺序" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="formData.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        <el-form-item label="排序顺序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCategoryStore } from '@/store/category'

const categoryStore = useCategoryStore()

// 搜索表单
const searchForm = reactive({
  categoryName: ''
})

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 分类列表
const categoryList = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const formRef = ref(null)
const formData = reactive({
  id: null,
  categoryName: '',
  description: '',
  sortOrder: 0
})
const submitting = ref(false)

// 表单验证规则
const formRules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
}

// 获取数据
const fetchData = async () => {
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      categoryName: searchForm.categoryName || undefined
    }
    const res = await categoryStore.fetchCategoryList(params)
    categoryList.value = res.records
    pagination.total = res.total
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
  searchForm.categoryName = ''
  pagination.pageNum = 1
  fetchData()
}

// 添加
const handleAdd = () => {
  dialogTitle.value = '添加分类'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑分类'
  formData.id = row.id
  formData.categoryName = row.categoryName
  formData.description = row.description
  formData.sortOrder = row.sortOrder
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该分类吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await categoryStore.deleteCategory(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true

    const data = {
      categoryName: formData.categoryName,
      description: formData.description,
      sortOrder: formData.sortOrder
    }

    if (formData.id) {
      await categoryStore.updateCategory(formData.id, data)
      ElMessage.success('修改成功')
    } else {
      await categoryStore.addCategory(data)
      ElMessage.success('添加成功')
    }

    dialogVisible.value = false
    fetchData()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.categoryName = ''
  formData.description = ''
  formData.sortOrder = 0
  formRef.value?.clearValidate()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.category-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
