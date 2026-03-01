<template>
  <div class="stats-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="24" :sm="12" :lg="6" v-for="item in statCards" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-content">
            <div class="stat-info">
              <div class="stat-title">{{ item.title }}</div>
              <div class="stat-value">{{ item.value }}</div>
            </div>
            <el-icon class="stat-icon" :style="{ color: item.color, backgroundColor: item.bgColor }">
              <component :is="item.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单趋势 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">订单趋势（近30天）</span>
          </template>
          <ECharts :option="orderLineOption" style="height: 350px" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">营业额趋势（近30天）</span>
          </template>
          <ECharts :option="revenueLineOption" style="height: 350px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 菜品销量排行 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">菜品销量排行（Top 10）</span>
          </template>
          <ECharts :option="dishBarOption" style="height: 400px" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts" name="statsDashboard">
import { ref, reactive, onMounted } from "vue";
import { User, Shop, List, Money } from "@element-plus/icons-vue";
import ECharts from "@/components/ECharts/index.vue";
import { getDashboardStats, getOrderStats, getRevenueStats, getDishRanking } from "@/api/modules/admin";

const statCards = reactive([
  { title: "用户总数", value: 0, icon: User, color: "#409EFF", bgColor: "#ecf5ff" },
  { title: "档口总数", value: 0, icon: Shop, color: "#67C23A", bgColor: "#f0f9eb" },
  { title: "订单总数", value: 0, icon: List, color: "#E6A23C", bgColor: "#fdf6ec" },
  { title: "总营业额", value: "¥0", icon: Money, color: "#F56C6C", bgColor: "#fef0f0" }
]);

const orderLineOption = ref({});
const revenueLineOption = ref({});
const dishBarOption = ref({});

const fetchStats = async () => {
  try {
    const res = await getDashboardStats();
    const stats = res.data as any;
    statCards[0].value = stats.userCount ?? 0;
    statCards[1].value = stats.stallCount ?? 0;
    statCards[2].value = stats.orderCount ?? 0;
    statCards[3].value = `¥${(stats.totalRevenue ?? 0).toFixed(2)}`;
  } catch (e) {
    console.error("获取统计数据失败", e);
  }
};

const fetchOrderTrend = async () => {
  try {
    const end = new Date();
    const start = new Date();
    start.setDate(end.getDate() - 30);
    const startDate = start.toISOString().split("T")[0];
    const endDate = end.toISOString().split("T")[0];
    const res = await getOrderStats({ startDate, endDate });
    const data = res.data as any;
    const dates = data.dates || [];
    const counts = data.counts || [];
    orderLineOption.value = {
      tooltip: { trigger: "axis" },
      grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true },
      xAxis: {
        type: "category",
        boundaryGap: false,
        data: dates,
        axisLabel: { rotate: 30, fontSize: 11 }
      },
      yAxis: { type: "value", minInterval: 1 },
      series: [
        {
          name: "订单数",
          type: "line",
          smooth: true,
          areaStyle: { opacity: 0.15 },
          itemStyle: { color: "#409EFF" },
          data: counts
        }
      ]
    };
  } catch (e) {
    console.error("获取订单趋势失败", e);
  }
};

const fetchRevenueTrend = async () => {
  try {
    const end = new Date();
    const start = new Date();
    start.setDate(end.getDate() - 30);
    const startDate = start.toISOString().split("T")[0];
    const endDate = end.toISOString().split("T")[0];
    const res = await getRevenueStats({ startDate, endDate });
    const data = res.data as any;
    const dates = data.dates || [];
    const amounts = data.amounts || [];
    revenueLineOption.value = {
      tooltip: {
        trigger: "axis",
        formatter: (params: any) => {
          const p = params[0];
          return `${p.axisValue}<br/>${p.marker}${p.seriesName}: ¥${p.value.toFixed(2)}`;
        }
      },
      grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true },
      xAxis: {
        type: "category",
        boundaryGap: false,
        data: dates,
        axisLabel: { rotate: 30, fontSize: 11 }
      },
      yAxis: { type: "value" },
      series: [
        {
          name: "营业额",
          type: "line",
          smooth: true,
          areaStyle: { opacity: 0.15 },
          itemStyle: { color: "#67C23A" },
          data: amounts
        }
      ]
    };
  } catch (e) {
    console.error("获取营业额趋势失败", e);
  }
};

const fetchDishRanking = async () => {
  try {
    const res = await getDishRanking({ top: 10 });
    const data = res.data as any;
    const names = (data.names || []).reverse();
    const sales = (data.sales || []).reverse();
    dishBarOption.value = {
      tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
      grid: { left: "3%", right: "6%", bottom: "3%", containLabel: true },
      xAxis: { type: "value", minInterval: 1 },
      yAxis: { type: "category", data: names, axisLabel: { fontSize: 12 } },
      series: [
        {
          name: "销量",
          type: "bar",
          data: sales,
          itemStyle: {
            color: {
              type: "linear",
              x: 0,
              y: 0,
              x2: 1,
              y2: 0,
              colorStops: [
                { offset: 0, color: "#409EFF" },
                { offset: 1, color: "#67C23A" }
              ]
            },
            borderRadius: [0, 4, 4, 0]
          },
          barWidth: 18
        }
      ]
    };
  } catch (e) {
    console.error("获取菜品排行失败", e);
  }
};

onMounted(() => {
  fetchStats();
  fetchOrderTrend();
  fetchRevenueTrend();
  fetchDishRanking();
});
</script>

<style scoped lang="scss">
.stats-dashboard {
  padding: 10px;
}
.stat-cards {
  margin-bottom: 20px;
}
.stat-card {
  margin-bottom: 12px;
  .stat-card-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .stat-info {
    .stat-title {
      font-size: 14px;
      color: #909399;
      margin-bottom: 8px;
    }
    .stat-value {
      font-size: 24px;
      font-weight: 700;
      color: #303133;
    }
  }
  .stat-icon {
    font-size: 40px;
    width: 64px;
    height: 64px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
.chart-row {
  margin-bottom: 20px;
  .el-col {
    margin-bottom: 12px;
  }
}
.card-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
