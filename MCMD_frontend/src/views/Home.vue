<template>
  <v-container fluid class="pa-0">
    <!-- 浮动搜索框 -->
    <div class="floating-search-bar">
      <div class="search-container">
        <!-- 搜索模式切换和搜索框 -->
        <div class="search-wrapper">
          <v-row no-gutters align="center" class="search-row">
            <!-- 搜索模式选择 -->
            <v-col cols="12" sm="5" md="5" class="pr-sm-3 button-col">
              <div class="custom-toggle-wrapper">
                <div class="custom-toggle">
                  <button 
                    class="toggle-btn" 
                    :class="{ active: searchMode === 'exactly' }"
                    @click="selectSearchMode('exactly')"
                  >
                    <span class="btn-content">
                      <v-icon icon="mdi-equal-box" size="small" class="mr-1"></v-icon>
                      Exactly Match Elements
                    </span>
                  </button>
                  <button 
                    class="toggle-btn" 
                    :class="{ active: searchMode === 'include' }"
                    @click="selectSearchMode('include')"
                  >
                    <span class="btn-content">
                      <v-icon icon="mdi-contain" size="small" class="mr-1"></v-icon>
                      Include Elements
                    </span>
                  </button>
                  <!-- 滑动指示器 -->
                  <div 
                    class="slider-indicator" 
                    :class="{
                      'slider-left': searchMode === 'exactly',
                      'slider-right': searchMode === 'include'
                    }"
                  ></div>
                </div>
              </div>
            </v-col>
            
            <!-- 搜索输入框 -->
            <v-col cols="12" sm="7" md="7" class="pt-2 pt-sm-0">
              <v-text-field
                v-model="searchQuery"
                placeholder="Search Materials..."
                @keyup.enter="search"
                clearable
                hide-details
                class="search-field"
                variant="solo-filled"
                density="comfortable"
                bg-color="white"
                rounded="pill"
                :prepend-inner-icon="'mdi-atom'"
                append-inner-icon="mdi-magnify"
                @click:append-inner="search"
                color="primary"
                elevation="0"
              ></v-text-field>
            </v-col>
          </v-row>
        </div>
      </div>
    </div>

    <!-- 已登录内容 -->
    <!-- 主卡片：欢迎信息、元素周期表和搜索指示 -->
    <v-row>
      <v-col cols="12">
        <v-card class="mx-auto no-hover" max-width="1200">
          <v-card-title class="text-h4 font-weight-bold text-center pb-2 card-title">
            Materials Science Database
          </v-card-title>
          <v-card-text class="text-center pt-0 pb-3">
            <v-chip
              class="mt-2"
              color="primary"
              :loading="isLoading"
              variant="elevated"
            >
              <v-icon start icon="mdi-database"></v-icon>
              Total Materials: {{ totalCount }}
            </v-chip>
          </v-card-text>
          
          <!-- Periodic Table Component -->
          <v-card-text class="pt-0">
            <periodic-table 
              ref="periodicTable"
              @element-click="handleElementClick"
            />
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 搜索结果 -->
    <v-row v-if="showResults" class="mt-6">
      <v-col cols="12">
        <div class="mx-auto px-0" style="max-width: 1200px">
          <!-- 搜索结果内容 -->
          <div class="d-flex align-center mb-4">
            <v-btn
              color="primary"
              variant="outlined"
              @click="showFilterDrawer = !showFilterDrawer"
              class="filter-btn"
              prepend-icon="mdi-filter-variant"
            >
              {{ showFilterDrawer ? 'Hide Filters' : 'Show Filters' }}
            </v-btn>
            <v-spacer></v-spacer>
          </div>
          <search-results
            ref="searchResults"
            :search-query="searchQuery"
            :search-mode="searchMode"
            :space-group-number="spaceGroupNumber"
            :selected-lattice="selectedLattice"
            :excluded-elements="excludedElements"
            class="search-results-container"
            @search-completed="handleSearchCompleted"
          />
        </div>
      </v-col>
    </v-row>
    
    <!-- 筛选抽屉 -->
    <v-navigation-drawer
      v-model="showFilterDrawer"
      location="left"
      width="360"
      class="filter-drawer"
      scrim="false"
      floating
    >
      <div class="filter-header d-flex align-center px-6">
        <div class="d-flex align-center">
          <v-icon icon="mdi-filter-variant" class="mr-2" color="primary"></v-icon>
          <span class="text-h6 font-weight-medium">Filter Options</span>
        </div>
        <v-spacer></v-spacer>
        <v-btn
          icon="mdi-close"
          variant="text"
          @click="showFilterDrawer = false"
          class="close-btn"
        ></v-btn>
      </div>
      
      <v-divider></v-divider>
      
      <div class="filter-content px-6 py-10">
        <!-- Space Group Number -->
        <v-text-field
          v-model="spaceGroupNumber"
          label="Space Group Number"
          variant="outlined"
          hide-details
          density="comfortable"
          class="mb-8"
          placeholder="e.g.: 59,164"
        >
          <template v-slot:append-inner>
            <v-btn
              icon="mdi-table"
              variant="text"
              size="small"
              @click="showSpaceGroupsTable = true"
              :title="'Click to select space group'"
            ></v-btn>
          </template>
        </v-text-field>

        <SpaceGroupTable
          v-model="showSpaceGroupsTable"
          :space-groups="spaceGroups"
          :current-value="spaceGroupNumber"
          @select="handleSpaceGroupSelect"
        />

        <!-- Magnetic Lattice -->
        <v-select
          v-model="selectedLattice"
          :items="latticeOptions"
          label="Magnetic Lattice"
          variant="outlined"
          density="comfortable"
          class="mb-8"
          hide-details
        ></v-select>

        <!-- Exclude Elements -->
        <v-text-field
          v-model="excludedElements"
          label="Exclude Elements"
          placeholder="e.g.: O N"
          variant="outlined"
          density="comfortable"
          class="mb-8"
          hide-details
        >
          <template v-slot:append>
            <v-tooltip text="Recommended to use in 'Fuzzy Search' mode">
              <template v-slot:activator="{ props }">
                <v-icon v-bind="props" icon="mdi-information"></v-icon>
              </template>
            </v-tooltip>
          </template>
        </v-text-field>

        <!-- Reset Button -->
        <v-btn
          color="primary"
          block
          @click="resetFilters"
          class="mt-8 filter-reset-btn"
          height="44"
        >
          Reset Filters
        </v-btn>
      </div>
    </v-navigation-drawer>
    
    <!-- 添加空间群表格对话框 -->
  </v-container>
</template>

<script>
import PeriodicTable from '@/components/PeriodicTable.vue'
import SearchResults from '@/components/SearchResults.vue'
import SpaceGroupTable from '@/components/SpaceGroupTable.vue'
import config from '@/config'
import { searchApi } from '@/utils/api'
import { spaceGroups } from '@/data/spaceGroups'

export default {
  name: 'Home',
  
  components: {
    PeriodicTable,
    SearchResults,
    SpaceGroupTable
  },

  data() {
    return {
      searchQuery: '',
      searchMode: 'exactly',
      modeOptions: [
        { value: 'exactly', text: 'Exactly Match Elements' },
        { value: 'include', text: 'Include Elements' }
      ],
      selectedElements: new Set(),
      isLoading: true,
      totalCount: 0,
      showResults: false,
      showFilterDrawer: false,
      spaceGroupNumber: '',
      selectedLattice: '',
      excludedElements: '',
      latticeOptions: [
        '',
        'Triangular',
        'Kagome',
        'Shastry-Sutherland',
        'Kitaev',
        'Chain',
        'Other'
      ],
      showSpaceGroupsTable: false,
      spaceGroups,
      isSearchBarScrolled: false
    }
  },
  
  watch: {
    // 监听搜索框内容变化
    searchQuery(newValue) {
      // 确保 newValue 不为 null
      const value = newValue || '';
      
      // 将搜索框内容转换为元素集合
      const elements = new Set(
        value.split(' ')
          .filter(term => term !== '')
          .map(term => term.trim())
      );
      
      // 更新选中的元素集合
      this.selectedElements = elements;
      
      // 更新元素周期表的显示状态
      this.$refs.periodicTable?.updateSelectedElements(Array.from(elements));
    }
  },
  
  created() {
    this.fetchTotalCount();
  },
  
  mounted() {
    // 添加滚动事件监听
    window.addEventListener('scroll', this.handleScroll);
  },
  
  beforeUnmount() {
    // 移除滚动事件监听，避免内存泄漏
    window.removeEventListener('scroll', this.handleScroll);
  },

  methods: {
    // 处理页面滚动事件
    handleScroll() {
      const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
      const searchBar = document.querySelector('.floating-search-bar');
      const searchWrapper = document.querySelector('.search-wrapper');
      
      if (searchBar && searchWrapper) {
        if (scrollTop > 30 && !this.isSearchBarScrolled) {
          searchWrapper.classList.add('scrolled');
          this.isSearchBarScrolled = true;
        } else if (scrollTop <= 30 && this.isSearchBarScrolled) {
          searchWrapper.classList.remove('scrolled');
          this.isSearchBarScrolled = false;
        }
      }
    },
    
    async fetchTotalCount() {
      try {
        this.isLoading = true;
        const response = await searchApi.getCount();
        this.totalCount = parseInt(response.count || 0, 10).toLocaleString();
      } catch (error) {
        console.error('Error fetching total count:', error);
        this.totalCount = 0;
      } finally {
        this.isLoading = false;
      }
    },

    handleElementClick({ symbol, selected }) {
      const searchTerms = new Set((this.searchQuery || '').split(' ').filter(term => term !== ''));
      
      if (!selected) {
        searchTerms.delete(symbol);
      } else {
        searchTerms.add(symbol);
      }
      
      this.searchQuery = Array.from(searchTerms).join(' ');
    },

    resetFilters() {
      this.spaceGroupNumber = '';
      this.selectedLattice = '';
      this.excludedElements = '';
      if (this.$refs.periodicTable) {
        this.$refs.periodicTable.resetSelection();
      }
    },

    search() {
      if (!this.searchQuery && !this.spaceGroupNumber && !this.selectedLattice) {
        // 如果没有任何搜索条件，不执行搜索
        return;
      }
      
      this.showResults = true;
      // 手动触发搜索结果组件的搜索
      this.$nextTick(() => {
        this.$refs.searchResults?.performSearch();
      });
    },

    handleSpaceGroupSelect(value) {
      this.spaceGroupNumber = value;
      if (this.showResults) {
        // 如果已经显示了搜索结果，自动触发新的搜索
        this.$refs.searchResults?.performSearch();
      }
    },
    
    getCurrentSpaceGroupValue() {
      return this.spaceGroupNumber;
    },

    handleSearchCompleted() {
      // 滚动到搜索结果区域
      setTimeout(() => {
        if (this.$refs.searchResults) {
          this.$refs.searchResults.$el.scrollIntoView({
            behavior: 'smooth', 
            block: 'start'
          });
        }
      }, 100); // 短暂延迟确保DOM已更新
    },

    selectSearchMode(mode) {
      this.searchMode = mode;
    }
  }
}
</script>

<style scoped>
.no-hover {
  transition: none !important;
  transform: none !important;
  box-shadow: none !important;
  background-color: transparent;
}

.no-hover:hover {
  transform: none !important;
  box-shadow: none !important;
}

/* 自定义切换按钮的样式 */
.custom-toggle-wrapper {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 0;
  border-radius: 50px;
  overflow: hidden;
}

.custom-toggle {
  display: flex;
  width: 100%;
  background-color: #f5f5f5;
  border-radius: 50px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  position: relative;
}

/* 滑动指示器 */
.slider-indicator {
  position: absolute;
  width: 50%;
  height: 100%;
  background-color: #1976d2;
  border-radius: 50px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 0;
}

.slider-left {
  transform: translateX(0);
}

.slider-right {
  transform: translateX(100%);
}

.toggle-btn {
  flex: 1;
  padding: 10px 18px;
  background-color: transparent;
  border: none;
  cursor: pointer;
  font-size: 0.875rem;
  font-weight: 500;
  color: #616161;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.5, 1);
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
  overflow: hidden;
  border-radius: 50px;
}

.toggle-btn:first-child {
  border-top-left-radius: 50px;
  border-bottom-left-radius: 50px;
}

.toggle-btn:last-child {
  border-top-right-radius: 50px;
  border-bottom-right-radius: 50px;
}

.toggle-btn:hover {
  color: #1976d2;
  background-color: rgba(25, 118, 210, 0.05);
}

.toggle-btn.active {
  color: white;
  background-color: transparent;
  font-weight: 500;
  box-shadow: none;
  transform: none;
}

.toggle-btn .btn-content {
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 0 2px;
}

.toggle-btn:active .btn-content {
  transform: scale(0.96);
}

.toggle-btn:focus {
  outline: none;
}

.toggle-btn .v-icon {
  margin-right: 6px !important;
}

/* 为移动设备优化圆形按钮 */
@media (max-width: 600px) {
  .toggle-btn {
    padding: 8px 10px;
    font-size: 0.75rem;
  }
  
  .toggle-btn .v-icon {
    font-size: 16px !important;
    margin-right: 4px !important;
  }
  
  .custom-toggle {
    border-radius: 40px;
  }
  
  .slider-indicator {
    border-radius: 40px;
  }
}

/* 搜索框样式 */
.search-wrapper {
  display: flex;
  align-items: center;
  padding: 8px 12px;
}

.search-row {
  width: 100%;
}

.v-text-field.search-field {
  border-radius: 50px !important;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.v-text-field.search-field .v-field__input {
  padding-left: 6px !important;
}

@media (max-width: 600px) {
  .toggle-btn {
    padding: 6px 10px;
    font-size: 0.75rem;
  }
  
  .toggle-btn .v-icon {
    font-size: 16px !important;
  }
}

/* 移除不再需要的旧样式 */
.search-mode-toggle {
  display: none;
}

/* 设置搜索栏的整体间距 */
.search-wrapper {
  background-color: transparent;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: none;
  padding: 8px 0;
  border: none;
}

/* 调整滚动时的效果，避免阴影冲突 */
.search-wrapper.scrolled {
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1) !important;
  background-color: rgba(255, 255, 255, 0.95) !important;
  border-radius: 8px !important;
  padding: 8px 16px !important;
}

/* 修复按钮栏和搜索栏之间的间距问题 */
.button-col {
  padding-right: 12px;
  margin-bottom: 0;
}

.search-field {
  width: 100%;
}

.search-field :deep(.v-input__control) {
  min-height: 46px;
}

.search-field :deep(.v-field) {
  border-radius: 50px !important;
  box-shadow: none;
  background-color: rgba(255, 255, 255, 0.85) !important;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: none !important;
}

.search-field :deep(.v-field:hover) {
  background-color: rgba(255, 255, 255, 0.95) !important;
}

.search-field :deep(.v-field--focused) {
  background-color: white !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-field :deep(.v-field--variant-solo-filled) {
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.search-field :deep(.v-field--variant-solo-filled.v-field--focused) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 调整内部输入框的样式 */
.search-field :deep(.v-field__input) {
  min-height: 44px;
  padding-top: 0;
  padding-bottom: 0;
  color: rgba(0, 0, 0, 0.87);
  font-weight: 400;
}

.search-field :deep(.v-field__prepend-inner) {
  margin-right: 8px;
  opacity: 0.7;
  color: #1976d2;
}

.search-field :deep(.v-field__append-inner) {
  margin-left: 8px;
  color: #1976d2;
}

.search-field :deep(.v-field__clearable) {
  padding: 0;
  margin: 0 4px;
}

.search-field :deep(.v-input__details) {
  display: none;
}

/* 占位符文字样式 */
.search-field :deep(input::placeholder) {
  color: rgba(0, 0, 0, 0.6);
  font-size: 0.95rem;
  font-weight: 400;
}

/* 修复输入框与外部边界的对齐问题 */
.search-field :deep(.v-text-field__slot) {
  margin: 0;
  padding: 0;
}

.filter-btn {
  border-radius: 8px;
  font-weight: 500;
  letter-spacing: 0.5px;
  height: 40px;
}

.filter-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.filter-drawer {
  margin-top: 0 !important;
  background-color: white !important;
  border-right: 1px solid rgba(0, 0, 0, 0.12) !important;
  box-shadow: 1px 0 5px rgba(0, 0, 0, 0.05) !important;
}

.filter-drawer :deep(.v-navigation-drawer__content) {
  background-color: white;
  border-right: 1px solid rgba(0, 0, 0, 0.12);
}

.filter-header {
  height: 64px;
  background-color: white;
}

.filter-content {
  background-color: white;
  height: calc(100% - 65px);
  overflow-y: auto;
}

.filter-content :deep(.v-field) {
  background-color: #f8f9fa;
}

.filter-content :deep(.v-field:hover) {
  background-color: #f0f2f5;
}

.filter-content :deep(.v-field--focused) {
  background-color: white;
}

.close-btn {
  opacity: 0.7;
}

.close-btn:hover {
  opacity: 1;
}

.filter-reset-btn {
  letter-spacing: 0.5px;
  font-weight: 500;
  border-radius: 8px;
  text-transform: none;
}

.filter-reset-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.search-results-container {
  width: 100%;
}

.search-results-container :deep(.v-data-table) {
  width: 100%;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.v-container {
  width: 100% !important;
  max-width: 100% !important;
  padding: 0 !important;
  margin: 0 !important;
}

.floating-search-bar {
  position: sticky;
  top: 63px;
  z-index: 99;
  padding: 0;
  margin-bottom: 0;
  transition: all 0.3s ease;
  background: transparent;
}

.search-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 16px;
}

.search-input-wrapper {
  padding: 8px 0;
  display: flex;
  align-items: center;
}

.search-field {
  border-radius: 8px;
  width: 100%;
}

.search-field :deep(.v-field__outline__start) {
  border-radius: 8px 0 0 8px;
}

.search-field :deep(.v-field__outline__end) {
  border-radius: 0 8px 8px 0;
}

/* 调整元素周期表上边距 */
.v-card-text {
  padding-top: 8px;
}

/* 第一个行的上边距调整 */
.v-container > .v-row:first-child {
  margin-top: 0;
  padding-top: 0;
}

/* 响应式调整 */
@media (max-width: 600px) {
  .floating-search-bar {
    top: 56px;
    margin-bottom: -5px;
  }
  
  .search-container {
    padding: 8px;
  }
  
  .search-input-wrapper {
    padding: 4px 0;
  }
  
  .search-button {
    margin-top: 16px;
  }

  .toggle-btn {
    font-size: 0.75rem;
    padding: 0 4px;
  }

  .button-col {
    margin-bottom: 8px;
  }
}

/* 调整间距，使搜索框更接近元素周期表 */
.floating-search-bar {
  margin-bottom: -5px;
}

.v-card {
  margin-top: 0 !important;
}

/* 移除卡片组件上边距，使元素周期表更靠近搜索框 */
.v-card-title {
  padding-top: 12px !important;
  margin-top: 0 !important;
}

.v-card-text:first-of-type {
  padding-top: 0;
}

/* 设置卡片透明，保持与搜索框融合 */
.no-hover {
  background-color: transparent;
  box-shadow: none !important;
}

.card-title {
  margin-top: 10px !important;
}

/* 媒体查询合并 */
@media (max-width: 959px) {
  .card-title {
    font-size: 1.5rem !important;
    margin-top: 5px !important;
  }
  
  .v-card-title {
    padding-top: 10px !important;
  }
  
  .button-col {
    margin-bottom: 8px;
  }
}
</style>