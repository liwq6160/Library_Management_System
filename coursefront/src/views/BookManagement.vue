<template>
  <div class="book-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>图书管理</span>
          <el-button type="primary" @click="handleAdd">添加图书</el-button>
        </div>
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

      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedRows.length > 0">
        <el-button type="danger" @click="handleBatchDelete">
          批量删除 ({{ selectedRows.length }})
        </el-button>
      </div>

      <!-- 图书列表表格 -->
      <el-table
        :data="bookList"
        stripe
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="封面" width="100">
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
        <el-table-column prop="bookName" label="书名" min-width="150" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="publisher" label="出版社" width="150" />
        <el-table-column prop="isbn" label="ISBN" width="130" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="price" label="价格" width="80">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column label="库存" width="120">
          <template #default="{ row }">
            <span>{{ row.availableCount }}/{{ row.totalCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
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
      width="700px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="书名" prop="bookName">
          <el-input v-model="formData.bookName" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="formData.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="formData.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="formData.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="formData.categoryId" placeholder="请选择分类">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.categoryName"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="formData.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker
            v-model="formData.publishDate"
            type="date"
            placeholder="请选择出版日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="馆藏总数" prop="totalCount">
          <el-input-number v-model="formData.totalCount" :min="0" />
        </el-form-item>
        <el-form-item label="可借数量" prop="availableCount">
          <el-input-number v-model="formData.availableCount" :min="0" />
        </el-form-item>
        <el-form-item label="封面图片" prop="coverImage">
          <el-upload
            class="cover-uploader"
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
          >
            <img v-if="formData.coverImage" :src="formData.coverImage" class="cover-preview" />
            <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <span class="upload-tip">建议尺寸：300x400像素，支持jpg、png格式</span>
        </el-form-item>
        <el-form-item label="图书简介" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入图书简介"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
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
import { Picture, Plus } from '@element-plus/icons-vue'
import { useBookStore } from '@/store/book'
import { useCategoryStore } from '@/store/category'

const bookStore = useBookStore()
const categoryStore = useCategoryStore()

// 搜索表单
const searchForm = reactive({
  bookName: '',
  author: '',
  publisher: '',
  categoryId: null,
  status: null
})

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 图书列表
const bookList = ref([])
const categories = ref([])
const selectedRows = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加图书')
const formRef = ref(null)
const formData = reactive({
  id: null,
  bookName: '',
  author: '',
  publisher: '',
  isbn: '',
  categoryId: null,
  totalCount: 0,
  availableCount: 0,
  price: 0,
  publishDate: null,
  coverImage: '',
  description: '',
  status: 1
})
const submitting = ref(false)

// 表单验证规则
const formRules = {
  bookName: [
    { required: true, message: '请输入书名', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入作者', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  totalCount: [
    { required: true, message: '请输入馆藏总数', trigger: 'blur' }
  ],
  availableCount: [
    { required: true, message: '请输入可借数量', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ]
}

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
  searchForm.status = null
  pagination.pageNum = 1
  fetchData()
}

// 选择行
const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

// 添加
const handleAdd = () => {
  dialogTitle.value = '添加图书'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑图书'
  formData.id = row.id
  formData.bookName = row.bookName
  formData.author = row.author
  formData.publisher = row.publisher
  formData.isbn = row.isbn
  formData.categoryId = row.categoryId
  formData.totalCount = row.totalCount
  formData.availableCount = row.availableCount
  formData.price = row.price
  formData.publishDate = row.publishDate
  formData.coverImage = row.coverImage
  formData.description = row.description
  formData.status = row.status
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该图书吗？删除后将无法恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await bookStore.deleteBook(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedRows.value.length} 本图书吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const ids = selectedRows.value.map(row => row.id)
    await bookStore.deleteBatchBooks(ids)
    ElMessage.success('批量删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    await bookStore.updateBookStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error(error.message || '状态更新失败')
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 上传前验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

// 上传图片
const handleUpload = async ({ file }) => {
  try {
    const coverUrl = await bookStore.uploadCover(file)
    formData.coverImage = coverUrl
    ElMessage.success('封面上传成功')
  } catch (error) {
    ElMessage.error(error.message || '封面上传失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    // 验证可借数量不能大于馆藏总数
    if (formData.availableCount > formData.totalCount) {
      ElMessage.error('可借数量不能大于馆藏总数')
      return
    }

    submitting.value = true

    const data = {
      bookName: formData.bookName,
      author: formData.author,
      publisher: formData.publisher,
      isbn: formData.isbn,
      categoryId: formData.categoryId,
      totalCount: formData.totalCount,
      availableCount: formData.availableCount,
      price: formData.price,
      publishDate: formData.publishDate,
      coverImage: formData.coverImage,
      description: formData.description,
      status: formData.status
    }

    if (formData.id) {
      await bookStore.updateBook(formData.id, data)
      ElMessage.success('修改成功')
    } else {
      await bookStore.addBook(data)
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
  formData.bookName = ''
  formData.author = ''
  formData.publisher = ''
  formData.isbn = ''
  formData.categoryId = null
  formData.totalCount = 0
  formData.availableCount = 0
  formData.price = 0
  formData.publishDate = null
  formData.coverImage = ''
  formData.description = ''
  formData.status = 1
  formRef.value?.clearValidate()
}

onMounted(() => {
  fetchCategories()
  fetchData()
})
</script>

<style scoped>
.book-management {
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

.batch-actions {
  margin-bottom: 15px;
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.cover-uploader {
  display: inline-block;
}

.cover-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.cover-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 160px;
  text-align: center;
  line-height: 160px;
}

.cover-preview {
  width: 120px;
  height: 160px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  display: block;
  margin-top: 5px;
  font-size: 12px;
  color: #999;
}
</style>
