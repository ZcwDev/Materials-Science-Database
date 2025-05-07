<template>
    <v-dialog
      :model-value="modelValue"
      @update:model-value="$emit('update:modelValue', $event)"
      max-width="800"
      scrollable
    >
      <v-card>
        <v-card-title class="d-flex justify-space-between align-center pa-4 bg-grey-lighten-4">
          <div class="d-flex align-center">
            <h3 class="text-h6 font-weight-bold mb-0">Space Group Number : Space Group</h3>
            <v-btn
              icon="mdi-open-in-new"
              variant="text"
              size="small"
              href="http://img.chem.ucl.ac.uk/sgp/large/sgp.htm"
              target="_blank"
              class="ml-2"
            ></v-btn>
          </div>
          <v-btn
            icon="mdi-close"
            size="small"
            @click="$emit('update:modelValue', false)"
          ></v-btn>
        </v-card-title>

        <v-divider></v-divider>

        <v-card-text class="pa-4">
          <template v-for="(category, categoryIndex) in spaceGroups" :key="categoryIndex">
            <div class="group-category">
              <div class="group-category-title">
                {{ category.category }}
              </div>
              <div class="group-container">
                <template v-for="group in category.groups" :key="group.number">
                  <div 
                    class="group-item-pair" 
                    @click="selectGroup(group.number)"
                    :title="'Click to select Space Group ' + group.number"
                  >
                    <div class="group-item number">
                      {{ group.number }}
                    </div>
                    <div class="group-item name">
                      <span v-html="formatGroupName(group.name)"></span>
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </template>
        </v-card-text>
      </v-card>
    </v-dialog>
  </template>
  
  <script>
  export default {
    name: 'SpaceGroupTable',
  
    props: {
      modelValue: {
        type: Boolean,
        required: true
      },
      spaceGroups: {
        type: Array,
        required: true
      },
      currentValue: {
        type: String,
        default: ''
      }
    },
  
    emits: ['update:modelValue', 'select'],
  
    methods: {
      selectGroup(number) {
        let newValue;
        
        if (this.currentValue.trim() === '') {
          newValue = number.toString();
        } else {
          newValue = this.currentValue + ',' + number;
        }
        
        this.$emit('select', newValue);
        this.$emit('update:modelValue', false);
      },
  
      formatGroupName(name) {
        // 处理字母为斜体
        let formatted = name.replace(/([A-Za-z])/g, '<i>$1</i>')
        // 先处理带横线的文本
        formatted = formatted.replace(/-(\d)/g, '<span style="text-decoration: overline;">$1</span>')
        // 然后处理下标文本（只处理下划线后的数字）
        formatted = formatted.replace(/_(\d)/g, '<sub>$1</sub>')
          .replace(/_/g, '')
        return formatted
      }
    }
  }
  </script>
  
  <style scoped>
  .group-category {
    margin-bottom: 24px;
  }

  .group-category:last-child {
    margin-bottom: 0;
  }

  .group-category-title {
    color: var(--v-primary-base);
    font-weight: 600;
    font-size: 1rem;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 2px solid var(--v-primary-lighten-4);
  }

  .group-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 8px;
  }

  .group-item-pair {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    background: var(--v-surface-variant);
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s ease;
    border: 1px solid rgba(0, 0, 0, 0.12);
  }

  .group-item-pair:hover {
    background: var(--v-primary-lighten-5);
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .group-item {
    font-size: 0.9rem;
  }

  .group-item.number {
    font-weight: 600;
    margin-right: 8px;
    color: var(--v-primary-darken-1);
    min-width: 24px;
  }

  .group-item.name {
    color: var(--v-text-primary);
  }

  .group-item.name :deep(i) {
    font-style: italic;
  }

  .group-item.name :deep(sub) {
    font-size: 0.75em;
    vertical-align: sub;
  }

  :deep(.v-card-title) {
    border-top-left-radius: inherit;
    border-top-right-radius: inherit;
  }
  </style>