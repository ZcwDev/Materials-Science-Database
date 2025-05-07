<template>
  <v-container fluid class="material-detail">
    <!-- Loading and Error States -->
    <loading-overlay :loading="loading" />
    <error-alert v-model:message="error" />

    <!-- No Data State -->
    <v-row v-if="!loading && !material">
      <v-col cols="12" class="text-center">
        <v-icon size="64" color="grey">mdi-alert</v-icon>
        <h2 class="text-h5 grey--text">No Data Available</h2>
      </v-col>
    </v-row>

    <!-- Material Content -->
    <template v-if="!loading && material">
      <!-- Header with Back Button and Title -->
      <v-row>
        <v-col cols="12">
          <div class="d-flex align-center mb-6">
            <v-btn
              color="primary"
              @click="$router.back()"
              variant="outlined"
              class="text-uppercase"
            >
              <v-icon start>mdi-arrow-left</v-icon>
              Back
            </v-btn>
            <h1
              class="text-h3 font-weight-bold text-center flex-grow-1"
              v-html="formatFormula(material.Formula)"
            ></h1>
            <div style="width: 68px"></div>
            <!-- 平衡左侧按钮宽度，保持标题居中 -->
          </div>
        </v-col>
      </v-row>

      <!-- Main Content Grid -->
      <v-row>
        <!-- 3D Structure and Unit Cell Parameters Row -->
        <v-col cols="12" class="mb-6">
          <v-row>
            <!-- 3D Structure -->
            <v-col cols="12" md="6">
              <v-card class="rounded-lg crystal-structure-card">
                <v-card-title class="d-flex align-center py-4 px-6 bg-primary">
                  <v-icon color="white" class="mr-2">mdi-atom</v-icon>
                  <span class="text-h6 font-weight-bold white--text"
                    >Crystal Structure</span
                  >
                </v-card-title>
                <v-card-text class="pa-6 structure-container">
                  <ChemDoodle :cifs="cifs" :key="componentKey" />
                </v-card-text>
              </v-card>
            </v-col>

            <!-- Unit Cell Parameters和Symmetry Information放在一起 -->
            <v-col cols="12" md="6">
              <div class="d-flex flex-column h-100">
                <!-- 材料ID卡片 - 只有表头 -->
                <v-card class="rounded-lg mb-4 flex-grow-0">
                  <v-card-title class="d-flex align-center py-4 px-6 bg-primary">
                    <span class="text-h6 font-weight-bold white--text">ID: {{ material.id || $route.params.id }}</span>
                  </v-card-title>
                </v-card>
                
                <!-- Unit Cell Parameters -->
                <v-card class="rounded-lg mb-4 flex-grow-0 table-card">
                  <v-card-title class="d-flex align-center py-4 px-6 bg-primary">
                    <v-icon color="white" class="mr-2">mdi-cube-outline</v-icon>
                    <span class="text-h6 font-weight-bold white--text"
                      >Unit Cell Parameters</span
                    >
                  </v-card-title>
                  <v-card-text class="pa-0">
                    <v-table class="custom-table">
                      <tbody>
                        <tr>
                          <td class="param-label">a (Å)</td>
                          <td class="param-value">{{ formatNumber(material.a) }}</td>
                          <td class="param-label">α (°)</td>
                          <td class="param-value">{{ formatNumber(material.a1) }}</td>
                        </tr>
                        <tr>
                          <td class="param-label">b (Å)</td>
                          <td class="param-value">{{ formatNumber(material.b) }}</td>
                          <td class="param-label">β (°)</td>
                          <td class="param-value">{{ formatNumber(material.b1) }}</td>
                        </tr>
                        <tr>
                          <td class="param-label">c (Å)</td>
                          <td class="param-value">{{ formatNumber(material.c) }}</td>
                          <td class="param-label">γ (°)</td>
                          <td class="param-value">{{ formatNumber(material.c1) }}</td>
                        </tr>
                      </tbody>
                    </v-table>
                  </v-card-text>
                </v-card>

                <!-- Symmetry Information -->
                <v-card class="rounded-lg flex-grow-1 table-card">
                  <v-card-title class="d-flex align-center py-4 px-6 bg-primary">
                    <v-icon color="white" class="mr-2">mdi-hexagon-multiple</v-icon>
                    <span class="text-h6 font-weight-bold white--text"
                      >Symmetry Information</span
                    >
                  </v-card-title>
                  <v-card-text class="pa-0">
                    <v-table class="custom-table">
                      <tbody>
                        <tr>
                          <td class="param-label">Space Group Number</td>
                          <td class="param-value">{{ material.Space_group_num }}</td>
                        </tr>
                        <tr>
                          <td class="param-label">Space Group</td>
                          <td
                            class="param-value"
                            v-html="formatSpaceGroup(material.Space_group_sym)"
                          ></td>
                        </tr>
                        <tr>
                          <td class="param-label">Point Group</td>
                          <td class="param-value">{{ material.Point_group }}</td>
                        </tr>
                        <tr>
                          <td class="param-label">Crystal System</td>
                          <td class="param-value">{{ material.Crystal_system }}</td>
                        </tr>
                        <tr>
                          <td class="param-label">Magnetic Lattice</td>
                          <td class="param-value">{{ material.magnetic_lattice }}</td>
                        </tr>
                      </tbody>
                    </v-table>
                  </v-card-text>
                </v-card>
              </div>
            </v-col>
          </v-row>
        </v-col>

        <!-- XRD Pattern 整行 -->
        <v-col cols="12" class="mb-6">
          <XRDChart :xrd-data="xrdData" />
        </v-col>

        <!-- Basic Properties和Magnetic Properties并排 -->
        <!-- Left Column - Basic Properties -->
        <v-col cols="12" md="6">
          <!-- Basic Properties -->
          <v-card class="rounded-lg table-card">
            <v-card-title class="d-flex align-center py-4 px-6 bg-primary">
              <v-icon color="white" class="mr-2">mdi-chart-box-outline</v-icon>
              <span class="text-h6 font-weight-bold white--text"
                >Basic Properties</span
              >
            </v-card-title>
            <v-card-text class="pa-0">
              <v-table class="custom-table">
                <tbody>
                  <tr>
                    <td class="param-label-full">Density (g/cm³)</td>
                    <td
                      class="param-value-full"
                      v-html="formatWithUnit(material.Density)"
                    ></td>
                  </tr>
                  <tr>
                    <td class="param-label-full">V<sub>unit cell</sub> (nm³)</td>
                    <td
                      class="param-value-full"
                      v-html="formatWithUnit(material.V_unit_cell)"
                    ></td>
                  </tr>
                  <tr>
                    <td class="param-label-full">Mole Mass (g/mol)</td>
                    <td
                      class="param-value-full"
                      v-html="formatWithUnit(material.Mole_Mass)"
                    ></td>
                  </tr>
                  <tr>
                    <td class="param-label-full">Mole Volume (cm³/mol)</td>
                    <td
                      class="param-value-full"
                      v-html="formatWithUnit(material.Mole_V)"
                    ></td>
                  </tr>
                  <tr>
                    <td class="param-label-full">GGA band gap (eV)</td>
                    <td class="param-value-full">{{ material.gga_band_gap || 'N/A' }}</td>
                  </tr>
                  <tr>
                    <td class="param-label-full">E<sub>hull</sub> (eV/atom)</td>
                    <td class="param-value-full">{{ material.e_hull || 'N/A' }}</td>
                  </tr>
                </tbody>
              </v-table>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Right Column - Magnetic Properties -->
        <v-col cols="12" md="6">
          <!-- Magnetic Properties -->
          <v-card class="rounded-lg table-card">
            <v-card-title class="d-flex align-center py-4 px-6 bg-primary">
              <v-icon color="white" class="mr-2">mdi-magnet</v-icon>
              <span class="text-h6 font-weight-bold white--text"
                >Magnetic Properties</span
              >
            </v-card-title>
            <v-card-text class="pa-0">
              <v-table class="custom-table">
                <tbody>
                  <tr>
                    <td class="param-label-full">Mag. ion</td>
                    <td
                      class="param-value-full"
                      v-html="formatIon(material.Mag_ion)"
                    ></td>
                  </tr>
                  <tr>
                    <td class="param-label-full">Mag. ion/V (nm⁻³)</td>
                    <td
                      class="param-value-full"
                      v-html="formatWithUnit(material.Mag_ion_V)"
                    ></td>
                  </tr>
                  <tr>
                    <td class="param-label-full">Mag. ion/m (g⁻³)</td>
                    <td
                      class="param-value-full"
                      v-html="formatWithUnit(material.Mag_ion_m)"
                    ></td>
                  </tr>
                  <tr>
                    <td class="param-label-full">Magnetic moment (μ<sub>B</sub>)</td>
                    <td class="param-value-full">{{ material.magnetic_moment || 'N/A' }}</td>
                  </tr>
                  <tr>
                    <td class="param-label-full">Effective Spin (J)</td>
                    <td class="param-value-full">{{ material.effective_spin || 'N/A' }}</td>
                  </tr>
                  <tr>
                    <td class="param-label-full">S<sub>GS</sub>(Vol)=S<sub>GS</sub>(Mass)*ρ (mJ/K⋅cm³)</td>
                    <td
                      class="param-value-full"
                      v-html="formatWithUnit(material.S_GS_Vol)"
                    ></td>
                  </tr>
                  <tr>
                    <td class="param-label-full">S<sub>GS</sub>(Mass)=S<sub>GS</sub>(mol)/M (mJ/K⋅g)</td>
                    <td
                      class="param-value-full"
                      v-html="formatWithUnit(material.S_GS_Mass)"
                    ></td>
                  </tr>
                  <tr>
                    <td class="param-label-full">T<sub>N</sub>* (mK)</td>
                    <td
                      class="param-value-full"
                      v-html="formatWithUnit(material.T_N)"
                    ></td>
                  </tr>
                  <tr>
                    <td class="param-label-full">T<sub>access</sub> (mK)</td>
                    <td
                      class="param-value-full"
                      v-html="formatWithUnit(material.T_access)"
                    ></td>
                  </tr>
                </tbody>
              </v-table>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- Associated Publications 单独一行 -->
        <v-col cols="12" class="mt-6">
          <!-- Associated Publications -->
          <v-card class="rounded-lg table-card">
            <v-card-title class="d-flex align-center py-4 px-6 bg-primary">
              <v-icon color="white" class="mr-2"
                >mdi-book-open-page-variant</v-icon
              >
              <span class="text-h6 font-weight-bold white--text"
                >Associated Publications</span
              >
            </v-card-title>
            <v-card-text class="pa-0">
              <v-table class="custom-table">
                <tbody>
                  <tr>
                    <td class="param-label">DOI</td>
                    <td class="param-value">
                      <a
                        :href="material.DOI"
                        target="_blank"
                        rel="noopener noreferrer"
                        class="text-primary text-decoration-none"
                      >
                        {{ material.DOI }}
                      </a>
                    </td>
                  </tr>
                </tbody>
              </v-table>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </template>
  </v-container>
</template>

<script>
import LoadingOverlay from "@/components/LoadingOverlay.vue";
import ErrorAlert from "@/components/ErrorAlert.vue";
import ChemDoodle from "@/components/ChemDoodle.vue";
import XRDChart from "@/components/XRDChart.vue";

export default {
  name: "MaterialDetail",

  components: {
    LoadingOverlay,
    ErrorAlert,
    ChemDoodle,
    XRDChart,
  },

  data() {
    return {
      material: null,
      loading: false,
      error: null,
      cifs: {
        primitive: null,
        conventional: null,
      },
      componentKey: 0,
      xrdData: {
        CuKa: [],
        CuKa1: [],
        CuKa2: [],
      },
    };
  },

  methods: {
    async fetchMaterial() {
      try {
        this.loading = true;
        this.componentKey += 1;

        const data = await this.$http.get(
          `/materials/${this.$route.params.id}`
        );
        this.material = data;

        this.cifs = {
          primitive: data.Path,
          conventional: data.Path,
        };

        // 处理XRD数据
        this.xrdData = this.parseXRDData({
          CuKa: data.CuKa,
          CuKa1: data.CuKa1,
          CuKa2: data.CuKa2,
        });
      } catch (error) {
        this.error = "加载材料数据失败，请确保您已登录并有权限访问此资源";
      } finally {
        this.loading = false;
      }
    },

    formatNumber(value) {
      if (value === null || value === undefined || isNaN(value)) {
        return "N/A";
      }
      return Number(value).toFixed(2);
    },

    formatFormula(formula) {
      if (!formula) return "N/A";
      return formula.replace(/(\d+)/g, "<sub>$1</sub>");
    },

    formatSpaceGroup(spaceGroup) {
      if (!spaceGroup) return "N/A";
      // 处理上划线
      spaceGroup = spaceGroup.replace(
        /-(\d)/g,
        '<span style="text-decoration: overline;">$1</span>'
      );
      // 处理下标
      spaceGroup = spaceGroup.replace(/_(\d)/g, "<sub>$1</sub>");
      return spaceGroup;
    },

    formatIon(ion) {
      if (!ion) return "N/A";
      return ion.replace(/(\^)([-+\d]+)/g, "<sup>$2</sup>");
    },

    formatWithUnit(value) {
      if (!value) return "N/A";
      // 处理上标，如 nm^3 -> nm<sup>3</sup>
      return value.replace(/(\^)([-+\d]+)/g, "<sup>$2</sup>");
    },

    parseXRDData(data) {
      const result = {
        CuKa: [],
        CuKa1: [],
        CuKa2: [],
      };

      try {
        for (const key in data) {
          if (data[key] && typeof data[key] === "string") {
            try {
              const parsedData = JSON.parse(data[key]);
              if (Array.isArray(parsedData)) {
                result[key] = parsedData
                  .map((item) => {
                    if (Array.isArray(item) && item.length === 2) {
                      return [parseFloat(item[0]), parseFloat(item[1])];
                    }
                    return null;
                  })
                  .filter((point) => point !== null);
              }
            } catch (e) {
              // 忽略解析错误
            }
          }

          if (result[key].length > 0) {
            result[key].sort((a, b) => a[0] - b[0]);
          }
        }
      } catch (error) {
        // 忽略解析错误
      }

      return result;
    },
  },

  mounted() {
    this.fetchMaterial();
  },
};
</script>

<style>
.material-detail {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  min-height: 100vh;
}

.v-card {
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
  border: none !important;
}

.v-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12) !important;
}

.v-card-title {
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

/* 表格样式 */
.custom-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  background: white !important;
  table-layout: fixed;
  margin-bottom: 0;
}

.custom-table tbody tr {
  transition: background-color 0.2s ease;
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
}

.custom-table tbody tr:last-child {
  border-bottom: none;
}

.custom-table tbody tr:hover {
  background-color: rgba(0, 0, 0, 0.02);
}

.custom-table td {
  padding: 10px 12px !important;
  line-height: 1.3;
  word-wrap: break-word;
  overflow: hidden;
  vertical-align: middle;
}

.param-label {
  color: rgba(0, 0, 0, 0.7);
  font-weight: 500;
  font-size: 0.9rem;
  white-space: normal;
  width: 30%;
  letter-spacing: 0.01em;
  padding-right: 8px;
}

.param-value {
  color: rgba(0, 0, 0, 0.87);
  font-family: "Roboto", sans-serif;
  font-size: 0.9rem;
  font-weight: 500;
  text-align: right;
  width: 20%;
  letter-spacing: 0.02em;
  white-space: normal;
}

.param-label-full {
  color: rgba(0, 0, 0, 0.7);
  font-weight: 500;
  font-size: 0.9rem;
  white-space: normal;
  width: 50%;
  letter-spacing: 0.01em;
  padding-right: 8px;
}

.param-value-full {
  color: rgba(0, 0, 0, 0.87);
  font-family: "Roboto", sans-serif;
  font-size: 0.9rem;
  font-weight: 500;
  text-align: right;
  width: 50%;
  letter-spacing: 0.02em;
  white-space: normal;
}

/* 响应式调整 */
@media (max-width: 960px) {
  .material-detail {
    padding: 16px;
  }

  .custom-table td {
    padding: 8px 10px !important;
    font-size: 0.85rem !important;
  }

  .param-label,
  .param-value {
    font-size: 0.85rem;
    width: auto;
  }

  .v-card-title {
    padding: 16px !important;
  }
}

/* 链接样式 */
a.text-primary {
  transition: opacity 0.2s ease;
}

a.text-primary:hover {
  opacity: 0.8;
  text-decoration: underline !important;
}

/* 3D结构查看器容器 */
.structure-container {
  position: relative;
  min-height: 400px;
}

/* 表格类卡片内容区域样式 */
.table-card .v-card-text {
  padding: 0 !important;
}

/* Crystal Structure卡片保持原有样式 */
.crystal-structure-card .v-card-text {
  padding: 24px !important;
}
</style>
