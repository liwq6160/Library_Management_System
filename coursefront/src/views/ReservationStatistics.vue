<template>
  <div class="reservation-statistics">
    <el-card>
      <template #header>
        <div class="header-container">
          <span>预约统计</span>
          <el-button type="primary" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出数据
          </el-button>
        </div>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="statistics-cards">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <el-statistic title="总预约数" :value="statistics.totalReservations">
              <template #prefix>
                <el-icon color="#409EFF"><Document /></el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <el-statistic title="待审核" :value="statistics.pendingCount">
              <template #prefix>
                <el-icon color="#E6A23C"><Clock /></el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <el-statistic title="已通过" :value="statistics.approvedCount">
              <template #prefix>
                <el-icon color="#67C23A"><CircleCheck /></el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <el-statistic title="已完成" :value="statistics.completedCount">
              <template #prefix>
                <el-icon color="#409EFF"><Check /></el-icon>
              </template>
            </el-statistic>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表区域 -->
      <el-row :gutter="20" class="charts-section">
        <!-- 预约趋势图 -->
        <el-col :span="16">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <span>预约数据趋势</span>
            </template>
            <div ref="trendChartRef" style="width: 100%; height: 400px;"></div>
          </el-card>
        </el-col>

        <!-- 状态分布饼图 -->
        <el-col :span="8">
          <el-card shadow="hover" class="chart-card">
            <template #header>
              <span>状态分布</span>
            </template>
            <div ref="pieChartRef" style="width: 100%; height: 400px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 热门预约图书排行 -->
      <el-card shadow="hover" class="popular-books-card">
        <template #header>
          <span>热门预约图书排行</span>
        </template>
        <el-table :data="popularBooks" stripe border>
          <el-table-column type="index" label="排名" width="80" />
          <el-table-column label="图书封面" width="100">
            <template #default="{ row }">
              <el-image
                :src="row.coverImage || '/default-book-cover.jpg'"
                fit="cover"
                style="width: 60px; height: 80px; border-radius: 4px;"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column prop="bookName" label="图书名称" min-width="200" />
          <el-table-column prop="author" label="作者" width="150" />
          <el-table-column prop="reservationCount" label="预约次数" width="120" sortable />
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Document, Clock, CircleCheck, Check, Picture } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getAllReservations } from '@/api/reservation'

// 统计数据
const statistics = reactive({
  totalReservations: 0,
  pendingCount: 0,
  approvedCount: 0,
  cancelledCount: 0,
  completedCount: 0
})

// 热门预约图书
const popularBooks = ref([])

// 图表引用
const trendChartRef = ref(null)
const pieChartRef = ref(null)

// 图表实例
let trendChart = null
let pieChart = null

// 获取统计数据
const fetchStatistics = async () => {
  try {
    // 获取所有预约记录（不分页，用于统计）
    const response = await getAllReservations({
      pageNum: 1,
      pageSize: 10000 // 获取所有数据用于统计
    })
    const records = response.data.records || []

    // 计算各状态数量
    statistics.totalReservations = records.length
    statistics.pendingCount = records.filter(r => r.status === 'pending').length
    statistics.approvedCount = records.filter(r => r.status === 'approved').length
    statistics.cancelledCount = records.filter(r => r.status === 'cancelled').length
    statistics.completedCount = records.filter(r => r.status === 'completed').length

    // 处理趋势数据
    const trendData = processTrendData(records)
    renderTrendChart(trendData)

    // 渲染饼图
    renderPieChart()

    // 处理热门图书数据
    processPopularBooks(records)
  } catch (error) {
    ElMessage.error(error.message || '获取统计数据失败')
  }
}

// 处理趋势数据（最近30天）
const processTrendData = (records) => {
  const today = new Date()
  const trendMap = {}

  // 初始化最近30天的数据
  for (let i = 29; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    const dateStr = date.toISOString().split('T')[0]
    trendMap[dateStr] = 0
  }

  // 统计每天的预约数量
  records.forEach(record => {
    const dateStr = record.reservationDate?.split(' ')[0]
    if (dateStr && trendMap.hasOwnProperty(dateStr)) {
      trendMap[dateStr]++
    }
  })

  return {
    dates: Object.keys(trendMap),
    counts: Object.values(trendMap)
  }
}

// 渲染趋势图
const renderTrendChart = (trendData) => {
  if (!trendChart && trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: trendData.dates,
      axisLabel: {
        rotate: 45,
        interval: 4
      }
    },
    yAxis: {
      type: 'value',
      name: '预约数量',
      minInterval: 1
    },
    series: [
      {
        name: '预约数量',
        type: 'line',
        data: trendData.counts,
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        },
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }

  trendChart.setOption(option)
}

// 渲染饼图
const renderPieChart = () => {
  if (!pieChart && pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '预约状态',
        type: 'pie',
        radius: '60%',
        data: [
          { value: statistics.pendingCount, name: '待审核', itemStyle: { color: '#E6A23C' } },
          { value: statistics.approvedCount, name: '已通过', itemStyle: { color: '#67C23A' } },
          { value: statistics.cancelledCount, name: '已取消', itemStyle: { color: '#909399' } },
          { value: statistics.completedCount, name: '已完成', itemStyle: { color: '#409EFF' } }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  pieChart.setOption(option)
}

// 处理热门图书数据
const processPopularBooks = (records) => {
  const bookMap = {}

  records.forEach(record => {
    if (record.bookId) {
      if (!bookMap[record.bookId]) {
        bookMap[record.bookId] = {
          bookId: record.bookId,
          bookName: record.bookName,
          author: record.author,
          coverImage: record.coverImage,
          reservationCount: 0
        }
      }
      bookMap[record.bookId].reservationCount++
    }
  })

  popularBooks.value = Object.values(bookMap)
    .sort((a, b) => b.reservationCount - a.reservationCount)
    .slice(0, 10) // 取前10名
}

// 导出数据
const handleExport = () => {
  try {
    // 构建CSV数据
    let csvContent = '图书名称,作者,预约次数\n'
    popularBooks.value.forEach(book => {
      csvContent += `"${book.bookName}","${book.author}",${book.reservationCount}\n`
    })

    // 创建Blob对象
    const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)

    link.setAttribute('href', url)
    link.setAttribute('download', `预约统计_${new Date().toISOString().split('T')[0]}.csv`)
    link.style.visibility = 'hidden'

    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    ElMessage.success('数据导出成功')
  } catch (error) {
    ElMessage.error('数据导出失败')
  }
}

// 窗口大小改变时调整图表
const handleResize = () => {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  fetchStatistics()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.reservation-statistics {
  padding: 20px;
  height: 100vh;
  overflow: auto;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.charts-section {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.popular-books-card {
  margin-top: 20px;
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
