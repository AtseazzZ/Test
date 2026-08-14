<template>
  <div ref="chartContainer" class="w-full h-full" :style="{ height: height }">
    <div v-if="loading" class="absolute inset-0 flex items-center justify-center bg-white bg-opacity-75 z-10">
      <span class="text-gray-500">{{ loadingText }}</span>
    </div>
    <div v-if="error" class="absolute inset-0 flex items-center justify-center bg-white bg-opacity-75 z-10">
      <span class="text-red-500 text-sm">{{ error }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue';
import * as echarts from 'echarts';

interface Props {
  option: any;
  height?: string;
  loading?: boolean;
  loadingText?: string;
  error?: string;
}

const props = withDefaults(defineProps<Props>(), {
  height: '300px',
  loading: false,
  loadingText: '加载中...',
  error: ''
});

const chartContainer = ref<HTMLDivElement | null>(null);
const chartInstance = ref<echarts.ECharts | null>(null);
const isMounted = ref(false);

// 安全地初始化图表
const initChart = async () => {
  if (!chartContainer.value || !isMounted.value) return;
  
  try {
    // 确保容器存在且已挂载
    await nextTick();
    if (!chartContainer.value || !isMounted.value) return;
    
    // 销毁旧实例
    if (chartInstance.value) {
      chartInstance.value.dispose();
      chartInstance.value = null;
    }
    
    // 创建新实例
    chartInstance.value = echarts.init(chartContainer.value);
    
    // 设置选项
    if (props.option && chartInstance.value) {
      chartInstance.value.setOption(props.option, true);
    }
  } catch (error) {
    console.error('初始化图表失败:', error);
  }
};

// 安全地更新图表
const updateChart = async () => {
  if (!chartInstance.value || !isMounted.value) return;
  
  try {
    await nextTick();
    if (!chartInstance.value || !isMounted.value) return;
    
    chartInstance.value.setOption(props.option, true);
  } catch (error) {
    console.error('更新图表失败:', error);
  }
};

// 安全地销毁图表
const destroyChart = () => {
  if (chartInstance.value) {
    try {
      chartInstance.value.dispose();
    } catch (error) {
      console.error('销毁图表失败:', error);
    }
    chartInstance.value = null;
  }
};

// 监听选项变化
watch(() => props.option, () => {
  if (isMounted.value && chartInstance.value) {
    updateChart();
  }
}, { deep: true });

// 监听加载状态
watch(() => props.loading, (newLoading: boolean) => {
  if (!newLoading && isMounted.value && chartInstance.value) {
    // 加载完成后重新渲染
    nextTick(() => {
      if (isMounted.value && chartInstance.value) {
        updateChart();
      }
    });
  }
});

onMounted(async () => {
  isMounted.value = true;
  
  // 延迟初始化，确保DOM完全稳定
  setTimeout(() => {
    if (isMounted.value) {
      initChart();
    }
  }, 100);
});

onUnmounted(() => {
  isMounted.value = false;
  destroyChart();
});
</script> 