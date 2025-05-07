<template>
  <div>
    <div class="d-flex align-center mb-4">
      <h3 class="text-h6">Search Results</h3>
      <span class="text-subtitle-1 ml-2">{{ totalCount }} results found</span>
    </div>

    <v-data-table
      :headers="headers"
      :items="materials"
      :loading="loading"
      :options.sync="tableOptions"
      :server-items-length="totalCount"
      :footer-props="{
        'items-per-page-options': [10, 20, 50],
        'items-per-page-text': 'Items per page',
        'items-per-page-all-text': 'All'
      }"
      :header-props="{
        'font-size': '16px',
        'font-weight': '700',
        'color': 'rgba(0, 0, 0, 0.87)'
      }"
      :show-select="false"
      :show-expand="false"
      density="comfortable"
      hover
      fixed-header
      class="search-results-table elevation-1"
      @update:options="handleTableUpdate"
    >
      <template v-slot:item.id="{ item }">
        <span class="formula-text">{{ item.id }}</span>
      </template>
      
      <template v-slot:item.Space_group_sym="{ item }">
        <span v-html="formatSpaceGroup(item.Space_group_sym)" class="formula-text"></span>
      </template>

      <template v-slot:item.Mag_ion_V="{ item }">
        <span v-html="formatIon(item.Mag_ion_V)" class="formula-text"></span>
      </template>

      <template v-slot:item.Mag_ion_m="{ item }">
        <span v-html="formatIon(item.Mag_ion_m)" class="formula-text"></span>
      </template>

      <template v-slot:item.Nm_Rln_2J_1_="{ item }">
        <span class="formula-text">{{ formatNumber(item.Nm_Rln_2J_1_) }}</span>
      </template>

      <template v-slot:item.Formula="{ item }">
        <span v-html="formatFormula(item.Formula)" class="formula-text"></span>
      </template>

      <template v-slot:item.actions="{ item }">
        <v-btn
          color="primary"
          variant="text"
          size="small"
          class="details-btn"
          @click="viewDetail(item)"
        >
          Details
          <v-icon start size="small" class="mr-1">mdi-chevron-right</v-icon>
        </v-btn>
      </template>

      <template v-slot:loading>
        <v-skeleton-loader
          v-for="n in 5"
          :key="n"
          type="list-item-two-line"
        ></v-skeleton-loader>
      </template>

      <template v-slot:no-data>
        <div class="text-center pa-4">
          <v-icon large color="grey lighten-1">mdi-database-search</v-icon>
          <div class="text-subtitle-1 grey--text mt-2">No results found</div>
        </div>
      </template>
    </v-data-table>
  </div>
</template>

<script>
import { searchApi } from '@/utils/api'

export default {
  name: 'SearchResults',

  props: {
    searchQuery: {
      type: String,
      default: ''
    },
    searchMode: {
      type: String,
      default: 'exactly'
    },
    spaceGroupNumber: {
      type: [String, Number],
      default: null
    },
    selectedLattice: {
      type: String,
      default: null
    },
    excludedElements: {
      type: String,
      default: ''
    }
  },

  data() {
    return {
      headers: [
        { 
          title: 'ID',
          key: 'id',
          align: 'start',
          sortable: true,
          width: '100px'
        },
        { 
          title: 'Formula',
          key: 'Formula',
          sortable: true,
          width: '200px'
        },
        { 
          title: 'Space group',
          key: 'Space_group_sym',
          sortable: true,
          width: '150px'
        },
        { 
          title: 'Atoms/Volume (nm⁻³)',
          key: 'Mag_ion_V',
          sortable: true,
          width: '180px'
        },
        { 
          title: 'Density (g/cm³)',
          key: 'Mag_ion_m',
          sortable: true,
          width: '180px'
        },
        { 
          title: 'Property Value',
          key: 'Nm_Rln_2J_1_',
          sortable: true,
          width: '180px'
        },
        { 
          title: 'Action',
          key: 'actions',
          sortable: false,
          width: '120px',
          align: 'center'
        }
      ],
      materials: [],
      loading: false,
      totalCount: 0,
      tableOptions: {
        page: 1,
        itemsPerPage: 10,
        sortBy: [],
        sortDesc: []
      },
      _lastSearchQuery: '',
      _lastSearchMode: '',
      _lastSpaceGroupNumber: null,
      _lastSelectedLattice: null,
      _lastExcludedElements: ''
    }
  },

  methods: {
    formatFormula(formula) {
      if (!formula) return 'N/A'
      return formula.replace(/(\d+)/g, '<sub>$1</sub>')
    },
    formatSpaceGroup(spaceGroup) {
      if (!spaceGroup) return 'N/A'
      // 处理上划线
      spaceGroup = spaceGroup.replace(/-(\d)/g, '<span style="text-decoration: overline;">$1</span>')
      // 处理下标
      spaceGroup = spaceGroup.replace(/_(\d)/g, '<sub>$1</sub>')
      return spaceGroup
    },
    formatIon(ion) {
      if (!ion) return 'N/A'
      return ion.replace(/(\^)([-+\d]+)/g, '<sup>$2</sup>')
    },
    formatNumber(value) {
      if (value == null || value === '') return '-';
      return typeof value === 'number' ? value.toFixed(2) : value;
    },
    async performSearch() {
      // 如果搜索框为空，不执行搜索
      if (!this.searchQuery.trim()) {
        this.materials = [];
        this.totalCount = 0;
        return;
      }

      this.loading = true;
      try {
        const params = {
          page: this.tableOptions.page,
          per_page: this.tableOptions.itemsPerPage,
          search_query: this.searchQuery.trim(),
          search_mode: this.searchMode
        };

        // 根据搜索模式添加具体的搜索参数
        if (this.searchMode === 'exactly') {
          params.Exactly_match_elements = this.searchQuery.trim();
        } else {
          params.Structure = this.searchQuery.trim();
        }

        // 添加其他过滤条件
        if (this.excludedElements) {
          params.Excluded_elements = this.excludedElements;
        }
        if (this.spaceGroupNumber) {
          params.SpaceGroup_number = this.spaceGroupNumber;
        }
        if (this.selectedLattice) {
          params.Magnetic_lattice = this.selectedLattice;
        }

        const response = await searchApi.searchMaterials(params);
        this.materials = response.materials || [];
        this.totalCount = response.count || 0;
        
        // 发出搜索完成事件，通知父组件
        this.$emit('search-completed', { count: this.totalCount });
      } catch (error) {
        console.error('搜索出错:', error);
        this.materials = [];
        this.totalCount = 0;
      } finally {
        this.loading = false;
      }
    },
    handleTableUpdate(options) {
      // 只处理分页变化，忽略排序
      if (options.page !== this.tableOptions.page || 
          options.itemsPerPage !== this.tableOptions.itemsPerPage) {
        this.tableOptions.page = options.page;
        this.tableOptions.itemsPerPage = options.itemsPerPage;
        this.performSearch();
      } else {
        // 仅更新排序选项，不触发搜索
        this.tableOptions.sortBy = options.sortBy;
        this.tableOptions.sortDesc = options.sortDesc;
      }
    },
    viewDetail(item) {
      this.$router.push(`/material/${item.id}`);
    }
  },

  watch: {
    spaceGroupNumber: {
      handler(newVal) {
        if (this.searchQuery.trim()) {
          this._lastSpaceGroupNumber = newVal;
          this.tableOptions.page = 1;
          this.performSearch();
        }
      }
    },
    selectedLattice: {
      handler(newVal) {
        if (this.searchQuery.trim()) {
          this._lastSelectedLattice = newVal;
          this.tableOptions.page = 1;
          this.performSearch();
        }
      }
    },
    excludedElements: {
      handler(newVal) {
        if (this.searchQuery.trim()) {
          this._lastExcludedElements = newVal;
          this.tableOptions.page = 1;
          this.performSearch();
        }
      }
    }
  },

  mounted() {
    this.performSearch();
  }
}
</script>

<style scoped>
/* 表头样式 */
:deep(.v-data-table__th) {
  font-size: 1.1rem !important;
  font-weight: 600 !important;
  color: rgba(0, 0, 0, 0.87) !important;
  background-color: #f5f5f5 !important;
  padding: 12px 16px !important;
  height: 56px !important;
  text-transform: none !important;
  white-space: nowrap !important;
  border-bottom: 2px solid rgba(0, 0, 0, 0.12) !important;
  font-family: 'Roboto', sans-serif !important;
}

:deep(.v-data-table__th .v-data-table-header__content) {
  font-size: 1.1rem !important;
  padding: 0 !important;
}

:deep(.v-data-table__th .v-data-table-header__content span) {
  font-size: 1.1rem !important;
  font-weight: 600 !important;
  font-family: 'Roboto', sans-serif !important;
}

/* 排序图标样式 */
:deep(.v-data-table-header__sort-icon) {
  font-size: 1rem !important;
  margin-left: 4px !important;
}

/* 表格基础样式 */
.search-results-table {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
}

/* 表格内容统一样式 */
:deep(.v-data-table__wrapper) {
  overflow-x: hidden !important;
}

:deep(.v-data-table-row td) {
  padding: 12px 16px !important;
  height: 52px !important;
}

.formula-text {
  font-size: 1rem !important;
  font-weight: 500 !important;
  font-family: 'Roboto', sans-serif !important;
  color: rgba(0, 0, 0, 0.87) !important;
  display: inline-block !important;
  padding: 4px 0 !important;
}

/* 操作按钮样式 */
.details-btn {
  text-transform: none !important;
  font-weight: 500 !important;
  letter-spacing: 0.3px;
  font-size: 1rem !important;
  height: 36px !important;
  min-width: 90px !important;
  opacity: 0.85;
  transition: opacity 0.2s ease !important;
}

.details-btn:hover {
  opacity: 1;
  background: transparent !important;
}

.details-btn :deep(.v-icon) {
  font-size: 18px !important;
}

/* 页脚样式 */
:deep(.v-data-table__footer) {
  background-color: white !important;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding: 16px !important;
}

/* 标题样式 */
.text-h6 {
  font-size: 1.5rem !important;
  font-weight: 600;
  color: rgba(0, 0, 0, 0.87);
}

.text-subtitle-1 {
  font-size: 1.1rem;
  color: rgba(0, 0, 0, 0.6);
}
</style>
