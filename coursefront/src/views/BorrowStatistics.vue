<template>
  <div class="borrow-statistics">
    <el-card class="statistics-header">
      <template #header>
        <span>借阅统计概览</span>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20">
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon total">
              <el-icon size="40"><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics?.totalBorrows || 0 }}</div>
              <div class="stat-label">总借阅量</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon borrowing">
              <el-icon size="40"><Reading /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics?.currentBorrowing || 0 }}</div>
              <div class="stat-label">借阅中</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon overdue">
              <el-icon size="40"><Warning /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics?.overdueCount || 0 }}</div>
              <div class="stat-label">逾期数量</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon returned">
              <el-icon size="40"><CircleCheck /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics?.returnedCount || 0 }}</div>
              <div class="stat-label">已归还</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon users">
              <el-icon size="40"><UserFilled /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics?.totalUsers || 0 }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon active">
              <el-icon size="40"><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ statistics?.activeUsers || 0 }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>借阅状态分布</span>
          </template>
          <div class="chart-container">
            <el-empty v-if="!statistics" description="暂无数据" :image-size="100" />
            <div v-else class="status-chart">
              <div class="chart-item">
                <div class="chart-bar borrowing" :style="{ width: getBorrowingPercent() + '%' }">
                  <span class="chart-label">借阅中 {{ statistics.currentBorrowing }}</span>
                </div>
              </div>
              <div class="chart-item">
                <div class="chart-bar overdue" :style="{ width: getOverduePercent() + '%' }">
                  <span class="chart-label">逾期 {{ statistics.overdueCount }}</span>
                </div>
              </div>
              <div class="chart-item">
                <div class="chart-bar returned" :style="{ width: getReturnedPercent() + '%' }">
                  <span class="chart-label">已归还 {{ statistics.returnedCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>用户活跃度</span>
          </template>
          <div class="chart-container">
            <el-empty v-if="!statistics" description="暂无数据" :image-size="100" />
            <div v-else class="user-chart">
              <div class="user-stat">
                <div class="user-circle active">
                  <div class="percentage">{{ getActiveUserPercent() }}%</div>
                  <div class="label">活跃用户</div>
                </div>
              </div>
              <div class="user-info">
                <div class="info-item">
                  <span class="info-label">活跃用户：</span>
                  <span class="info-value">{{ statistics.activeUsers }} 人</span>
                </div>
                <div class="info-item">
                  <span class="info-label">总用户数：</span>
                  <span class="info-value">{{ statistics.totalUsers }} 人</span>
                </div>
                <div class="info-item">
                  <span class="info-label">活跃率：</span>
                  <span class="info-value">{{ getActiveUserPercent() }}%</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 统计信息 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <span>统计分析</span>
      </template>
      <el-row :gutter="20" v-if="statistics">
        <el-col :span="12">
          <div class="analysis-item">
            <h4>借阅完成率</h4>
            <el-progress
              :percentage="getReturnRate()"
              :color="getReturnRateColor()"
              :stroke-width="20"
            >
              <span class="percentage-label">{{ getReturnRate() }}%</span>
            </el-progress>
            <p class="analysis-desc">
              {{ statistics.returnedCount }} / {{ statistics.totalBorrows }} 已归还
            </p>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="analysis-item">
            <h4>逾期率</h4>
            <el-progress
              :percentage="getOverdueRate()"
              :color="getOverdueRateColor()"
              :stroke-width="20"
            >
              <span class="percentage-label">{{ getOverdueRate() }}%</span>
            </el-progress>
            <p class="analysis-desc">
              {{ statistics.overdueCount }} / {{ statistics.totalBorrows }} 逾期
            </p>
          </div>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无数据" :image-size="100" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Reading, Warning, CircleCheck, UserFilled, User } from '@element-plus/icons-vue'
import { useBorrowStore } from '@/store/borrow'

const borrowStore = useBorrowStore()

const statistics = ref(null)

// 获取统计数据
const fetchStatistics = async () => {
  try {
    statistics.value = await borrowStore.fetchBorrowStatistics()
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 计算借阅中百分比
const getBorrowingPercent = () => {
  if (!statistics.value || statistics.value.totalBorrows === 0) return 0
  return Math.round((statistics.value.currentBorrowing / statistics.value.totalBorrows) * 100)
}

// 计算逾期百分比
const getOverduePercent = () => {
  if (!statistics.value || statistics.value.totalBorrows === 0) return 0
  return Math.round((statistics.value.overdueCount / statistics.value.totalBorrows) * 100)
}

// 计算已归还百分比
const getReturnedPercent = () => {
  if (!statistics.value || statistics.value.totalBorrows === 0) return 0
  return Math.round((statistics.value.returnedCount / statistics.value.totalBorrows) * 100)
}

// 计算活跃用户百分比
const getActiveUserPercent = () => {
  if (!statistics.value || statistics.value.totalUsers === 0) return 0
  return Math.round((statistics.value.activeUsers / statistics.value.totalUsers) * 100)
}

// 计算归还率
const getReturnRate = () => {
  if (!statistics.value || statistics.value.totalBorrows === 0) return 0
  return Math.round((statistics.value.returnedCount / statistics.value.totalBorrows) * 100)
}

// 归还率颜色
const getReturnRateColor = () => {
  const rate = getReturnRate()
  if (rate >= 80) return '#67c23a'
  if (rate >= 60) return '#e6a23c'
  return '#f56c6c'
}

// 计算逾期率
const getOverdueRate = () => {
  if (!statistics.value || statistics.value.totalBorrows === 0) return 0
  return Math.round((statistics.value.overdueCount / statistics.value.totalBorrows) * 100)
}

// 逾期率颜色
const getOverdueRateColor = () => {
  const rate = getOverdueRate()
  if (rate <= 5) return '#67c23a'
  if (rate <= 15) return '#e6a23c'
  return '#f56c6c'
}

onMounted(() => {
  fetchStatistics()
})
</script>

<style scoped>
.borrow-statistics {
  padding: 20px;
}

.statistics-header {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-icon.borrowing {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.stat-icon.overdue {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.stat-icon.returned {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.stat-icon.users {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.stat-icon.active {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart-container {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-chart {
  width: 100%;
  padding: 20px;
}

.chart-item {
  margin-bottom: 20px;
}

.chart-bar {
  height: 50px;
  border-radius: 25px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  transition: all 0.3s;
  min-width: 100px;
}

.chart-bar.borrowing {
  background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
}

.chart-bar.overdue {
  background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
}

.chart-bar.returned {
  background: linear-gradient(90deg, #43e97b 0%, #38f9d7 100%);
}

.chart-label {
  color: white;
  font-weight: bold;
  font-size: 14px;
}

.user-chart {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 20px;
}

.user-circle {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.percentage {
  font-size: 36px;
  font-weight: bold;
}

.label {
  font-size: 14px;
  margin-top: 5px;
}

.user-info {
  flex: 1;
  margin-left: 40px;
}

.info-item {
  margin-bottom: 15px;
  font-size: 16px;
}

.info-label {
  color: #909399;
}

.info-value {
  font-weight: bold;
  color: #303133;
  margin-left: 10px;
}

.analysis-item {
  padding: 20px;
}

.analysis-item h4 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #303133;
}

.percentage-label {
  font-weight: bold;
  margin-left: 10px;
}

.analysis-desc {
  margin-top: 10px;
  color: #909399;
  font-size: 14px;
}
</style>
