<template>
  <div class="layout-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px" class="sidebar">
        <div class="logo">
          <h3>图书管理系统</h3>
        </div>
        <el-menu
          :default-active="activeMenu"
          :router="true"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/home">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/books">
            <el-icon><Reading /></el-icon>
            <span>图书列表</span>
          </el-menu-item>
          <el-menu-item index="/my-borrows">
            <el-icon><Tickets /></el-icon>
            <span>我的借阅</span>
          </el-menu-item>
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
          <el-sub-menu v-if="userStore.isAdmin" index="admin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/users">
              <el-icon><UserFilled /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/book-management">
              <el-icon><Notebook /></el-icon>
              <span>图书管理</span>
            </el-menu-item>
            <el-menu-item index="/category-management">
              <el-icon><FolderOpened /></el-icon>
              <span>分类管理</span>
            </el-menu-item>
            <el-menu-item index="/borrow-management">
              <el-icon><DocumentCopy /></el-icon>
              <span>借阅管理</span>
            </el-menu-item>
            <el-menu-item index="/overdue-records">
              <el-icon><Warning /></el-icon>
              <span>逾期记录</span>
            </el-menu-item>
            <el-menu-item index="/borrow-statistics">
              <el-icon><DataAnalysis /></el-icon>
              <span>借阅统计</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 头部 -->
        <el-header class="header">
          <div class="header-left">
            <h4>{{ currentTitle }}</h4>
          </div>
          <div class="header-right">
            <span class="username">{{ userStore.realName || userStore.username }}</span>
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                <el-icon><Avatar /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 内容区 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { House, User, UserFilled, Avatar, Reading, Setting, Notebook, FolderOpened, Tickets, DocumentCopy, Warning, DataAnalysis } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 当前页面标题
const currentTitle = computed(() => route.meta.title || '')

// 下拉菜单命令处理
const handleCommand = async (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      await userStore.logout()
    }).catch(() => {})
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.el-container {
  height: 100%;
}

.sidebar {
  background-color: #304156;
  height: 100vh;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  background-color: #2b3a4b;
}

.logo h3 {
  margin: 0;
  font-size: 16px;
}

.el-menu {
  border-right: none;
}

.header {
  background-color: white;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left h4 {
  margin: 0;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  color: #666;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 20px;
  color: #666;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 60px);
}
</style>
