<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button v-if="!isEditing" type="primary" @click="handleEdit">编辑</el-button>
              <div v-else>
                <el-button @click="handleCancel">取消</el-button>
                <el-button type="primary" @click="handleSave">保存</el-button>
              </div>
            </div>
          </template>
          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="profileForm.realName" :disabled="!isEditing" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" :disabled="!isEditing" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" :disabled="!isEditing" />
            </el-form-item>
            <el-form-item label="角色">
              <el-tag :type="profileForm.role === 'admin' ? 'danger' : 'primary'">
                {{ profileForm.role === 'admin' ? '管理员' : '普通用户' }}
              </el-tag>
            </el-form-item>
            <el-form-item label="状态">
              <el-tag :type="profileForm.status === 1 ? 'success' : 'danger'">
                {{ profileForm.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="24" style="margin-top: 20px;">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>修改密码</span>
            </div>
          </template>
          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdatePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const isEditing = ref(false)

// 个人信息表单
const profileForm = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  role: '',
  status: 1
})

// 原始数据（用于取消编辑时恢复）
let originalData = {}

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 个人信息验证规则
const profileRules = {
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
  ]
}

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 加载用户信息
const loadUserInfo = async () => {
  const userInfo = await userStore.getUserInfo()
  if (userInfo) {
    Object.assign(profileForm, userInfo)
    originalData = { ...userInfo }
  }
}

// 编辑
const handleEdit = () => {
  isEditing.value = true
}

// 取消编辑
const handleCancel = () => {
  Object.assign(profileForm, originalData)
  isEditing.value = false
  profileFormRef.value?.clearValidate()
}

// 保存
const handleSave = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      const { username, role, status, ...data } = profileForm
      const success = await userStore.updateUserInfo(data)
      if (success) {
        isEditing.value = false
        originalData = { ...profileForm }
      }
    }
  })
}

// 修改密码
const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      const { confirmPassword, ...data } = passwordForm
      const success = await userStore.updatePassword(data)
      if (success) {
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
        passwordFormRef.value.resetFields()
      }
    }
  })
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  max-width: 800px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
