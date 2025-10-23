<template>
  <div class="home-container">
    <el-card>
      <h2>欢迎使用图书管理系统</h2>
      <p>当前用户：{{ userStore.realName || userStore.username }}</p>
      <p>用户角色：{{ userStore.isAdmin ? '管理员' : '普通用户' }}</p>
    </el-card>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
</script>

<style scoped>
.home-container {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

p {
  margin: 10px 0;
  color: #666;
  font-size: 16px;
}
</style>
