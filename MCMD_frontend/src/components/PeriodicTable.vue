<template>
  <div class="periodic-table">
    <v-container fluid class="pa-0">
      <div class="periodic-table-grid">
        <!-- 主族元素 -->
        <template v-for="(element, index) in mainElements" :key="index">
          <div :class="[
            'element-card', 
            element.category.toLowerCase().replace(/ /g, '-'),
            { 'selected': selectedElements.includes(element.symbol) },
            { 'locked': element.symbol === 'La-Lu' || element.symbol === 'Ac-Lr' }
          ]"
               v-ripple
               :style="{ gridRow: getGridPosition(element).row, gridColumn: getGridPosition(element).column }"
               @click="selectElement(element)">
            <span class="v-btn__overlay"></span>
            <span class="v-btn__underlay"></span>
            <div class="atomic-number">{{ element.number }}</div>
            <div class="symbol">{{ element.symbol }}</div>
          </div>
        </template>

        <!-- 镧系元素 -->
        <div class="lanthanide-row">
          <div class="lanthanide-label">La-Lu</div>
          <template v-for="(element, index) in lanthanideElements" :key="'la-'+index">
            <div :class="[
              'element-card',
              'lanthanide',
              { 'selected': selectedElements.includes(element.symbol) }
            ]"
                 v-ripple
                 @click="selectElement(element)">
              <span class="v-btn__overlay"></span>
              <span class="v-btn__underlay"></span>
              <div class="atomic-number">{{ element.number }}</div>
              <div class="symbol">{{ element.symbol }}</div>
            </div>
          </template>
        </div>

        <!-- 锕系元素 -->
        <div class="actinide-row">
          <div class="actinide-label">Ac-Lr</div>
          <template v-for="(element, index) in actinideElements" :key="'ac-'+index">
            <div :class="[
              'element-card',
              'actinide',
              { 'selected': selectedElements.includes(element.symbol) }
            ]"
                 v-ripple
                 @click="selectElement(element)">
              <span class="v-btn__overlay"></span>
              <span class="v-btn__underlay"></span>
              <div class="atomic-number">{{ element.number }}</div>
              <div class="symbol">{{ element.symbol }}</div>
            </div>
          </template>
        </div>
      </div>
    </v-container>
  </div>
</template>

<script>
import { periodicTableData } from '@/data/periodicTableData'

export default {
  name: 'PeriodicTable',
  
  data: () => ({
    selectedElements: [],
    categoryColors: {
      'Alkali Metal': '#d8f8fe',           // 碱金属 - 淡蓝绿色
      'Alkaline Earth Metal': '#fee7e7',    // 碱土金属 - 淡粉色
      'Noble Gas': '#fee7e7',              // 稀有气体 - 与碱土金属相同的淡粉色
      'Transition Metal': '#f3e8fd',        // 过渡金属 - 淡紫色
      'Post-Transition Metal': '#d6f9e8',   // 主族金属 - 淡绿色
      'Metalloid': '#fef7e0',              // 半金属 - 淡黄色
      'Nonmetal': '#e2eeff',               // 非金属 - 淡蓝色
      'Halogen': '#dff3ff',                // 卤素 - 另一种淡蓝色
      'Lanthanide': '#e2eeff',             // 镧系元素 - 淡蓝色
      'Actinide': '#fee6d4',               // 锕系元素 - 淡橙色
      'Super Heavy': '#e7e7ea'             // 超重元素 - 淡灰色
    },
    periodicTable: [],
    showTooltip: false,
    tooltipContent: '',
    tooltipX: 0,
    tooltipY: 0
  }),

  created() {
    this.periodicTable = periodicTableData
    // 添加特殊的镧系和锕系占位元素
    this.periodicTable.push(
      { 
        number: '57-71', 
        symbol: 'La-Lu', 
        name: 'Lanthanides', 
        chineseName: '镧系', 
        category: 'Lanthanide'
      },
      { 
        number: '89-103', 
        symbol: 'Ac-Lr', 
        name: 'Actinides', 
        chineseName: '锕系', 
        category: 'Actinide'
      }
    )
  },

  computed: {
    mainElements() {
      const elements = this.periodicTable.filter(element => 
        !element.category.includes('Lanthanide') && 
        !element.category.includes('Actinide')
      )
      
      // 添加特殊的镧系和锕系占位元素
      elements.push(
        { 
          number: '57-71', 
          symbol: 'La-Lu', 
          name: 'Lanthanides', 
          chineseName: '镧系', 
          category: 'Lanthanide'
        },
        { 
          number: '89-103', 
          symbol: 'Ac-Lr', 
          name: 'Actinides', 
          chineseName: '锕系', 
          category: 'Actinide'
        }
      )
      
      return elements
    },
    lanthanideElements() {
      return this.periodicTable.filter(element => 
        element.category === 'Lanthanide' && 
        element.symbol !== 'La-Lu'
      ).sort((a, b) => a.number - b.number)
    },
    actinideElements() {
      return this.periodicTable.filter(element => 
        element.category === 'Actinide' && 
        element.symbol !== 'Ac-Lr'
      ).sort((a, b) => a.number - b.number)
    }
  },

  methods: {
    getElementColor(category) {
      return this.categoryColors[category] || 'grey'
    },

    selectElement(element) {
      if (element.symbol === 'La-Lu' || element.symbol === 'Ac-Lr') return;
      const index = this.selectedElements.indexOf(element.symbol);
      if (index === -1) {
        this.selectedElements.push(element.symbol);
      } else {
        this.selectedElements.splice(index, 1);
      }
      this.$emit('element-click', { 
        symbol: element.symbol, 
        selected: index === -1 
      });
    },

    updateSelectedElements(elements) {
      this.selectedElements = elements;
    },

    getGridPosition(element) {
      // 镧系和锕系元素的特殊处理
      if (element.symbol === 'La-Lu') {
        return {
          row: 6,
          column: 3
        };
      } else if (element.symbol === 'Ac-Lr') {
        return {
          row: 7,
          column: 3
        };
      }

      // 镧系元素的特殊处理
      if (element.number >= 57 && element.number <= 71) {
        return {
          row: 8,  // 镧系元素行
          column: element.number - 56  // 从第1列开始
        };
      } else if (element.number >= 89 && element.number <= 103) {
        return {
          row: 9,  // 锕系元素行
          column: element.number - 88  // 从第1列开始
        };
      }

      // 第六周期特殊处理
      if (element.number >= 72 && element.number <= 86) {
        return {
          row: 6,
          column: element.number - 68  // 72号元素从第4列开始
        };
      }

      // 第七周期特殊处理
      if (element.number >= 104 && element.number <= 118) {
        return {
          row: 7,
          column: element.number - 100  // 104号元素从第4列开始
        };
      }

      // 其他元素使用默认的周期和族计算
      const periods = {
        1: 1, 2: 1,  // 第一周期
        3: 2, 4: 2,  // 第二周期
        5: 2, 6: 2, 7: 2, 8: 2, 9: 2, 10: 2,  // 第二周期
        11: 3, 12: 3, 13: 3, 14: 3, 15: 3, 16: 3, 17: 3, 18: 3,  // 第三周期
        19: 4, 20: 4, 21: 4, 22: 4, 23: 4, 24: 4, 25: 4, 26: 4, 27: 4, 28: 4, 29: 4, 30: 4, 31: 4, 32: 4, 33: 4, 34: 4, 35: 4, 36: 4,  // 第四周期
        37: 5, 38: 5, 39: 5, 40: 5, 41: 5, 42: 5, 43: 5, 44: 5, 45: 5, 46: 5, 47: 5, 48: 5, 49: 5, 50: 5, 51: 5, 52: 5, 53: 5, 54: 5,  // 第五周期
        55: 6, 56: 6,  // 第六周期前两个元素
        87: 7, 88: 7   // 第七周期前两个元素
      };

      const groups = {
        1: 1, 2: 18,  // 第一周期
        3: 1, 4: 2,   // 第二周期
        5: 13, 6: 14, 7: 15, 8: 16, 9: 17, 10: 18,  // 第二周期
        11: 1, 12: 2, 13: 13, 14: 14, 15: 15, 16: 16, 17: 17, 18: 18,  // 第三周期
        19: 1, 20: 2, 21: 3, 22: 4, 23: 5, 24: 6, 25: 7, 26: 8, 27: 9, 28: 10, 29: 11, 30: 12, 31: 13, 32: 14, 33: 15, 34: 16, 35: 17, 36: 18,  // 第四周期
        37: 1, 38: 2, 39: 3, 40: 4, 41: 5, 42: 6, 43: 7, 44: 8, 45: 9, 46: 10, 47: 11, 48: 12, 49: 13, 50: 14, 51: 15, 52: 16, 53: 17, 54: 18,  // 第五周期
        55: 1, 56: 2,  // 第六周期前两个元素
        87: 1, 88: 2   // 第七周期前两个元素
      };

      return {
        row: periods[element.number] || Math.ceil(element.number / 18),
        column: groups[element.number] || (element.number % 18 || 18)
      };
    },
  }
}
</script>

<style scoped>
.periodic-table {
  max-width: 1400px;
  margin: 0 auto;
  padding: 10px;
}

.periodic-table-grid {
  display: grid;
  grid-template-columns: repeat(18, 1fr);
  /* grid-template-rows: repeat(9, 1fr); */
  gap: 2px;
  width: 100%;
}

.element-card {
  grid-column-end: span 1; /* 明确声明每元素占1列 */
  min-width: 0; /* 防止内容溢出 */
  aspect-ratio: 1;
  min-height: 45px;
  padding: 3px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
  cursor: pointer;
  position: relative;
  background: #f5f5f5;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  user-select: none;
  overflow: hidden;
  -webkit-tap-highlight-color: transparent;
  transition: transform 0s, box-shadow 0s;
}

.element-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.element-card:active {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.element-card.locked {
  cursor: not-allowed;
  opacity: 0.9;
}

.element-card.locked:hover {
  transform: none;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.element-card.locked:active {
  transform: none;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.element-card.locked::before {
  display: none;
}

.element-card.locked .symbol {
  font-size: 1.2rem;
  color: #666;
}

.element-card.locked .atomic-number {
  font-size: 0.8rem;
  color: #666;
}

.element-card.selected {
  box-shadow: inset 0 0 0 2px #1976d2;
  background-color: rgba(25, 118, 210, 0.05);
}

.lanthanide-row,
.actinide-row {
  grid-column: 3 / 18;
  display: grid;
  grid-template-columns: repeat(15, 1fr); /* 15个镧系元素 */
  gap: 2px;
  margin-top: 10px;
  position: relative;
}

.lanthanide-row {
  grid-row: 8;
}

.actinide-row {
  grid-row: 9;
}

.lanthanide-label, .actinide-label {
  position: absolute;
  left: -50px; /* 将标签移出网格区域 */
  width: 48px;
  height: 100%;
  background-color: #f0f0f0;
  cursor: default;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.1em;
  color: #333;
  min-width: 40px;
  padding: 0 4px;
}

.lanthanide-label {
  background-color: #e2eeff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.actinide-label {
  background-color: #fee6d4;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

/* 为不同类别的元素设置不同的符号颜色 */
.alkali-metal .symbol { 
  color: #12758c;    /* 碱金属 - 深青色 */
  font-weight: 450;
}
.alkaline-earth-metal .symbol { 
  color: #d50024;     /* 碱土金属 - 鲜红色 */
  font-weight: 450;
}
.noble-gas .symbol { 
  color: #cd1d5e;    /* 稀有气体 - 深粉红色 */
  font-weight: 450;
}
.transition-metal .symbol { 
  color: #6232ec;   /* 过渡金属 - 紫色 */
  font-weight: 450;
}
.post-transition-metal .symbol { 
  color: #032c00;      /* 主族金属 - 深绿色 */
  font-weight: 450;
}
.metalloid .symbol { 
  color: #90560a;    /* 半金属 - 棕色 */
  font-weight: 450;
}
.nonmetal .symbol,
.halogen .symbol { 
  color: #0b60f0;   /* 非金属和卤素 - 亮蓝色 */
  font-weight: 450;
}
.super-heavy .symbol { 
  color: #3f374f;     /* 超重元素（未知属性） - 深灰色 */
  font-weight: 450;
}

/* 镧系和锕系元素使用相同的字体样式，但字号较小 */
.lanthanide .symbol {
  /* font-size: 1.4rem; */
  font-weight: 450;
  color: #043355;  /* 镧系元素 - 深蓝色 */
}

.actinide .symbol {
  /* font-size: 1.4rem; */
  font-weight: 450;
  color: #c7320f;  /* 锕系元素 - 深橙红色 */
}

/* 为不同类别的元素设置原子序数颜色 */
.alkali-metal .atomic-number { color: #12758c; }
.alkaline-earth-metal .atomic-number { color: #d50024; }
.noble-gas .atomic-number { color: #cd1d5e; }
.transition-metal .atomic-number { color: #6232ec; }
.post-transition-metal .atomic-number { color: #227754; }
.metalloid .atomic-number { color: #90560a; }
.nonmetal .atomic-number,
.halogen .atomic-number { color: #0b60f0; }
.super-heavy .atomic-number { color: #3f374f; }
.lanthanide .atomic-number { 
  color: #043355;
  /* font-size: 0.8rem; */
}
.actinide .atomic-number { 
  color: #c7320f;
  /* font-size: 0.8rem; */
}

/* 为不同类别的元素设置背景颜色 */
.alkali-metal { background-color: #d8f8fe; }           /* 碱金属 - 淡蓝绿色 */
.alkaline-earth-metal { background-color: #fee7e7; }   /* 碱土金属 - 淡粉色 */
.transition-metal { background-color: #f3e8fd; }       /* 过渡金属 - 淡紫色 */
.post-transition-metal { background-color: #d6f9e8; }  /* 主族金属 - 淡绿色 */
.metalloid { background-color: #fef7e0; }             /* 半金属 - 淡黄色 */
.nonmetal,
.halogen { background-color: #e2eeff; }              /* 非金属和卤素 - 淡蓝色 */
.noble-gas { background-color: #ffe8f7; }            /* 稀有气体 - 浅粉紫色 */
.lanthanide { background-color: #e2eeff; }            /* 镧系元素 - 淡蓝色 */
.actinide { background-color: #fee6d4; }              /* 锕系元素 - 淡橙色 */
.super-heavy { background-color: #e7e7ea; }           /* 超重元素 - 淡灰色 */

.atomic-number {
  position: absolute;
  top: 1px;
  left: 3px;
  font-size: 0.65rem;
}

.symbol {
  font-size: 2.2rem;
  font-weight: 450;
  text-align: center;
  margin: 0;
  padding: 0;
  line-height: 1.2;
  transform: translateY(2px);
}

.chinese-name {
  font-size: 0.7rem;
  text-align: center;
  margin-bottom: 2px;
  position: absolute;
  bottom: 2px;
  width: 100%;
}

.v-btn__overlay {
  background-color: rgb(var(--v-theme-on-surface));
  border-radius: inherit;
  opacity: 0;
  pointer-events: none;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: opacity 0s;
}

.v-btn__underlay {
  border-radius: inherit;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  opacity: 0;
  background-color: rgb(var(--v-theme-on-surface));
}

.element-card:hover .v-btn__overlay {
  opacity: 0.08;
}

.element-card:active .v-btn__overlay {
  opacity: 0.16;
}

.element-card.locked .v-btn__overlay,
.element-card.locked .v-btn__underlay {
  display: none;
}

.v-ripple__container {
  color: rgba(var(--v-theme-on-surface), 0.7);
  border-radius: inherit;
  position: absolute;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  overflow: hidden;
  z-index: 0;
  pointer-events: none;
  contain: strict;
}

.v-ripple__animation {
  color: inherit;
  position: absolute;
  top: 0;
  left: 0;
  border-radius: 50%;
  background: currentColor;
  opacity: 0;
  pointer-events: none;
  overflow: hidden;
  will-change: transform, opacity;
}

.v-ripple__animation--enter {
  transition: none;
  opacity: 0;
}

.v-ripple__animation--in {
  transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.1s cubic-bezier(0.4, 0, 0.2, 1);
  opacity: 0.25;
}

.v-ripple__animation--out {
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .element-card {
    min-height: 45px;
  }
  .symbol {
    transform: translateY(2px);
  }
  .periodic-table-grid {
    gap: 2px;
  }
}

@media (max-width: 960px) {
  .periodic-table {
    padding: 5px;
  }
  .element-card {
    min-height: 35px;
  }
  .symbol {
    transform: translateY(1px);
  }
  .periodic-table-grid {
    gap: 1px;
  }
  .lanthanide-row,
  .actinide-row {
    gap: 1px;
  }
}

@media (max-width: 768px) {
  .periodic-table {
    padding: 3px;
  }
  .element-card {
    min-height: 30px;
    padding: 2px;
  }
  .symbol {
    transform: translateY(0);
    font-size: clamp(0.6rem, 2.5vw, 2.2rem);
  }
  .atomic-number {
    font-size: clamp(0.35rem, 0.8vw, 0.65rem);
  }
  .lanthanide .symbol,
  .actinide .symbol {
    font-size: clamp(0.6rem, 2.5vw, 2.2rem);
  }
  .lanthanide .atomic-number,
  .actinide .atomic-number {
    font-size: clamp(0.35rem, 0.8vw, 0.65rem);
  }
  .periodic-table-grid {
    gap: 1px;
  }
  .lanthanide-row,
  .actinide-row {
    gap: 1px;
    margin-top: 5px;
  }
}

@media (max-width: 600px) {
  .periodic-table {
    padding: 2px;
  }
  .element-card {
    min-height: 25px;
    padding: 1px;
  }
  .symbol {
    font-size: clamp(0.5rem, 2vw, 2.2rem);
  }
  .atomic-number {
    font-size: clamp(0.3rem, 0.7vw, 0.65rem);
    top: 0;
    left: 1px;
  }
  .lanthanide .symbol,
  .actinide .symbol {
    font-size: clamp(0.5rem, 2vw, 2.2rem);
  }
  .lanthanide .atomic-number,
  .actinide .atomic-number {
    font-size: clamp(0.3rem, 0.7vw, 0.65rem);
  }
  .periodic-table-grid {
    gap: 1px;
  }
  .lanthanide-row,
  .actinide-row {
    gap: 0.5px;
    margin-top: 3px;
  }
}

@media (max-width: 480px) {
  .periodic-table {
    padding: 1px;
  }
  .element-card {
    min-height: 20px;
    padding: 1px;
  }
  .symbol {
    font-size: clamp(0.45rem, 1.8vw, 2.2rem);
    transform: translateY(0);
  }
  .atomic-number {
    font-size: clamp(0.25rem, 0.6vw, 0.65rem);
    top: 0;
    left: 1px;
  }
  .lanthanide .symbol,
  .actinide .symbol {
    font-size: clamp(0.45rem, 1.8vw, 2.2rem);
  }
  .lanthanide .atomic-number,
  .actinide .atomic-number {
    font-size: clamp(0.25rem, 0.6vw, 0.65rem);
  }
  .lanthanide-row,
  .actinide-row {
    margin-top: 2px;
  }
  .element-card.locked .symbol {
    font-size: clamp(0.4rem, 1.5vw, 1.2rem);
  }
  .element-card.locked .atomic-number {
    font-size: clamp(0.25rem, 0.6vw, 0.8rem);
  }
}
</style>
