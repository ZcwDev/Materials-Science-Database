<template>
  <v-card class="rounded-lg">
    <v-card-title class="d-flex align-center py-4 px-6 bg-primary">
      <v-icon color="white" class="mr-2">mdi-chart-line</v-icon>
      <span class="text-h6 font-weight-bold white--text"
        >X-Ray Diffraction Pattern</span
      >
      <v-spacer></v-spacer>
      
      <!-- 调整按钮对齐和菜单配置 -->
      <div class="d-flex justify-end">
        <v-menu
          v-model="menuVisible"
          :close-on-content-click="true"
          location="bottom"
          origin="auto"
          offset-y
          max-width="180"
        >
          <template v-slot:activator="{ props }">
            <v-btn
              variant="elevated" 
              color="white"
              class="wavelength-btn"
              v-bind="props"
            >
              {{ getSelectedWavelengthLabel() }}
              <v-icon size="small" class="ml-1">mdi-chevron-down</v-icon>
            </v-btn>
          </template>
          
          <v-card class="wavelength-menu" width="180">
            <v-list density="compact" class="pa-0">
              <v-list-item
                v-for="(item, index) in wavelengthOptions"
                :key="index"
                :value="item.value"
                @click="selectedXRDType = item.value"
                :active="selectedXRDType === item.value"
                :class="{ 'bg-primary-lighten-5': selectedXRDType === item.value }"
                class="wavelength-item"
              >
                <v-list-item-title>{{ item.title }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-card>
        </v-menu>
      </div>
    </v-card-title>
    <v-card-text class="pa-6">
      <div class="chart-container">
        <div
          v-if="loading"
          class="d-flex justify-center align-center"
          style="height: 400px"
        >
          <v-progress-circular
            indeterminate
            color="primary"
            size="64"
          ></v-progress-circular>
        </div>
        <div
          v-else-if="error"
          class="d-flex justify-center align-center flex-column"
          style="height: 400px"
        >
          <v-icon color="error" size="64">mdi-alert-circle</v-icon>
          <div class="text-center mt-4 text-body-1">{{ error }}</div>
          <v-btn color="primary" class="mt-4" @click="initializeData"
            >Retry</v-btn
          >
        </div>
        <div
          v-else-if="!currentData.length"
          class="d-flex justify-center align-center flex-column"
          style="height: 400px"
        >
          <v-icon color="grey" size="64">mdi-chart-bell-curve</v-icon>
          <div class="text-center mt-4 text-body-1">No XRD data available</div>
        </div>
        <div
          v-else
          ref="chartContainer"
          style="width: 100%; height: 400px"
        ></div>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
import * as echarts from "echarts";
import { markRaw } from "vue";

export default {
  name: "XRDChart",

  props: {
    xrdData: {
      type: Object,
      required: true,
      default: () => ({
        CuKa: [],
        CuKa1: [],
        CuKa2: [],
      }),
    },
  },

  data() {
    return {
      myChart: null,
      currentData: [],
      selectedXRDType: "CuKa",
      loading: false,
      error: null,
      zoomTimer: null,
      menuVisible: false,
      wavelengthOptions: [
        { title: 'CuKa (1.54184 Å)', value: 'CuKa' },
        { title: 'CuKa1 (1.54056 Å)', value: 'CuKa1' },
        { title: 'CuKa2 (1.54439 Å)', value: 'CuKa2' }
      ],
    };
  },

  watch: {
    selectedXRDType: {
      handler() {
        this.onXRDTypeChange();
      },
      immediate: false,
    },
    xrdData: {
      handler() {
        this.initializeData();
      },
      deep: true,
      immediate: true,
    },
  },

  methods: {
    preprocessData(data, maxPoints = 2000) {
      if (!data || data.length <= maxPoints) return data;

      const result = [];
      const step = Math.ceil(data.length / maxPoints);

      result.push(data[0]);

      for (let i = step; i < data.length - step; i += step) {
        let maxVal = data[i][1];
        let maxIdx = i;

        for (let j = i; j < Math.min(i + step, data.length); j++) {
          if (data[j][1] > maxVal) {
            maxVal = data[j][1];
            maxIdx = j;
          }
        }

        if (i !== maxIdx) {
          result.push(data[i]);
          result.push(data[maxIdx]);
        } else {
          result.push(data[i]);
        }
      }

      if (result[result.length - 1] !== data[data.length - 1]) {
        result.push(data[data.length - 1]);
      }

      return result;
    },

    renderChart(data) {
      this.$nextTick(() => {
        const chartDom = this.$refs.chartContainer;
        if (!chartDom) return;

        if (this.myChart != null) {
          this.myChart.dispose();
          this.myChart = null;
        }

        try {
          const chart = echarts.init(chartDom, null, {
            renderer: "canvas",
            useDirtyRect: true,
          });
          this.myChart = markRaw(chart);

          const processedData = this.preprocessData(data);
          const maxIntensity = Math.max(...data.map((point) => point[1]));
          const yAxisMax = Math.ceil((maxIntensity * 1.1) / 10) * 10;

          const option = {
            title: {
              text: `X-Ray Diffraction (XRD) Pattern - ${this.selectedXRDType}`,
              left: "center",
              textStyle: {
                fontSize: 18,
                fontWeight: "bold",
              },
            },
            tooltip: {
              trigger: "axis",
              formatter: (params) =>
                `2θ: ${params[0].value[0].toFixed(
                  2
                )}°<br/>Intensity: ${params[0].value[1].toFixed(1)}`,
              confine: true,
              enterable: false,
              showDelay: 0,
              hideDelay: 50,
              transitionDuration: 0,
            },
            grid: {
              left: "8%",
              right: "5%",
              bottom: "15%",
              top: "15%",
              containLabel: true,
            },
            xAxis: {
              name: "2θ (°)",
              nameLocation: "middle",
              nameGap: 35,
              nameTextStyle: {
                fontSize: 14,
                fontWeight: "bold",
              },
              type: "value",
              min: Math.max(0, data[0][0]),
              max: data[data.length - 1][0],
              axisLabel: {
                formatter: "{value}°",
                fontSize: 14,
                margin: 16,
                showMaxLabel: true,
                showMinLabel: true,
              },
              splitLine: {
                show: false,
              },
              axisLine: {
                show: true,
                lineStyle: {
                  color: "#333",
                },
              },
              axisTick: {
                show: true,
                length: 6,
                lineStyle: {
                  color: "#333",
                },
              },
              axisPointer: {
                label: {
                  show: false,
                },
              },
              animation: false,
            },
            yAxis: {
              name: "Intensity (a.u.)",
              nameRotate: 90,
              nameLocation: "middle",
              nameGap: 45,
              nameTextStyle: {
                fontSize: 14,
                fontWeight: "bold",
              },
              type: "value",
              min: 0,
              max: yAxisMax,
              scale: false,
              axisLabel: {
                fontSize: 14,
                margin: 16,
              },
              splitNumber: 10,
              splitLine: {
                show: false,
              },
              axisLine: {
                show: true,
                lineStyle: {
                  color: "#333",
                },
              },
              axisTick: {
                show: true,
                length: 6,
                lineStyle: {
                  color: "#333",
                },
              },
              animation: false,
            },
            series: [
              {
                name: this.selectedXRDType,
                type: "line",
                data: processedData,
                smooth: false,
                symbol: "none",
                sampling: "max",
                animation: true,
                animationDuration: 1000,
                animationEasing: "linear",
                hoverAnimation: false,
                lineStyle: {
                  width: 1.5,
                  color: "#1976d2",
                },
                emphasis: {
                  focus: "none",
                },
                silent: false,
                large: true,
                largeThreshold: 1000,
              },
            ],
            dataZoom: [
              {
                type: "slider",
                xAxisIndex: 0,
                start: 0,
                end: 100,
                bottom: "2%",
                height: 28,
                borderColor: "transparent",
                backgroundColor: "#f5f5f5",
                fillerColor: "rgba(25, 118, 210, 0.1)",
                handleSize: "100%",
                textStyle: {
                  color: "#666",
                },
                borderRadius: 2,
                throttle: 0,
                zoomLock: false,
                moveOnMouseMove: true,
                animation: false,
                animationDurationUpdate: 0,
              },
              {
                type: "inside",
                xAxisIndex: 0,
                start: 0,
                end: 100,
                zoomOnMouseWheel: true,
                moveOnMouseMove: true,
                moveOnMouseWheel: true,
              },
            ],
            toolbox: {
              right: "20px",
              top: "20px",
              itemSize: 20,
              itemGap: 8,
              feature: {
                saveAsImage: {
                  title: "Save Image",
                  icon: "path://M4.7,22.9L29.3,45.5L54.7,23.4M4.6,43.6L4.6,58L53.8,58L53.8,43.6M29.2,45.1L29.2,0",
                },
                restore: {
                  title: "Reset Zoom",
                },
              },
            },
            animation: true,
            animationThreshold: 5000,
            animationDuration: 1000,
            animationEasing: "linear",
            animationDurationUpdate: 0,
            progressive: 500,
            progressiveThreshold: 2000,
            progressiveChunkMode: "sequential",
            hoverLayerThreshold: Infinity,
            useUTC: false,
          };

          this.myChart.setOption(option);

          let zoomTimer = null;
          this.myChart.on("datazoom", () => {
            if (zoomTimer) {
              cancelAnimationFrame(zoomTimer);
            }

            zoomTimer = requestAnimationFrame(() => {
              zoomTimer = null;
            });
          });

          this.myChart.on("restore", () => {
            this.myChart.setOption({
              xAxis: {
                min: Math.max(0, data[0][0]),
                max: data[data.length - 1][0],
              },
            });
          });
        } catch (error) {
          console.error("渲染图表错误:", error);
          this.error = "渲染图表失败，请稍后重试";
        }
      });
    },

    initializeData() {
      this.loading = true;
      this.error = null;

      try {
        const hasAnyData = Object.values(this.xrdData).some(
          (data) => data && data.length > 0
        );

        if (!hasAnyData) {
          this.error = "No XRD data available";
          return;
        }

        this.currentData = this.xrdData[this.selectedXRDType] || [];

        if (this.currentData.length > 0) {
          this.$nextTick(() => {
            this.renderChart(this.currentData);
          });
        } else {
          this.error = "No XRD data available";
        }
      } catch (error) {
        this.error = "Failed to load XRD data, please try again later";
      } finally {
        this.loading = false;
      }
    },

    onXRDTypeChange() {
      this.currentData = this.xrdData[this.selectedXRDType] || [];

      if (this.currentData.length > 0) {
        this.$nextTick(() => {
          this.renderChart(this.currentData);
        });
      } else if (this.myChart) {
        this.myChart.clear();
        this.error = "No XRD data available for this wavelength";
      }
    },

    handleResize() {
      if (this.myChart) {
        this.myChart.resize();
      }
    },

    getSelectedWavelengthLabel() {
      const selectedItem = this.wavelengthOptions.find(item => item.value === this.selectedXRDType);
      return selectedItem ? selectedItem.title : 'Select a wavelength';
    },

    toggleMenu() {
      this.menuVisible = !this.menuVisible;
    },
    
    selectOption(value) {
      this.selectedXRDType = value;
      this.menuVisible = false;
    },
  },

  mounted() {
    window.addEventListener("resize", this.handleResize);
  },

  beforeUnmount() {
    if (this.myChart) {
      this.myChart.dispose();
      this.myChart = null;
    }
    window.removeEventListener("resize", this.handleResize);
  },
};
</script>

<style scoped>
.chart-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.wavelength-btn {
  width: 180px !important;
  text-transform: none !important;
  letter-spacing: normal !important;
  font-weight: 500;
  text-align: left;
  justify-content: space-between;
  height: 36px;
  border-radius: 4px;
  padding: 0 12px !important;
  font-size: 0.95rem !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2) !important;
}

.wavelength-menu {
  width: 180px !important;
  border-radius: 4px;
  overflow: hidden;
}

.wavelength-item {
  font-size: 0.95rem;
  letter-spacing: normal;
  padding: 10px 12px;
  min-height: 40px;
  width: 180px;
}

.wavelength-item :deep(.v-list-item-title) {
  font-size: 0.95rem !important;
  font-weight: 500;
}

.wavelength-item:hover {
  background-color: rgba(25, 118, 210, 0.08);
}
</style>
