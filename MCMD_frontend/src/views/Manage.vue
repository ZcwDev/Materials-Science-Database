<template>
  <v-container fluid class="manage-page pa-6">
    <v-row>
      <v-col cols="12">
        <v-card class="mb-6">
          <v-card-title class="d-flex align-center py-4 px-6 bg-primary">
            <v-icon color="white" class="mr-2">mdi-database-cog</v-icon>
            <span class="text-h5 font-weight-bold white--text">数据库管理</span>
          </v-card-title>

          <!-- 操作按钮组 -->
          <v-card-text class="pa-6">
            <v-btn-group class="mb-6">
              <v-btn
                v-for="op in operations"
                :key="op.value"
                :color="selectedOperation === op.value ? 'primary' : ''"
                :variant="selectedOperation === op.value ? 'flat' : 'outlined'"
                @click="setOperation(op.value)"
                class="operation-btn"
                prepend-icon="mdi-database-plus"
              >
                {{ op.text }}
              </v-btn>
            </v-btn-group>

            <!-- 文件上传按钮 -->
            <div v-if="selectedOperation === '新增'" class="d-flex gap-4 mt-4">
              <v-btn
                color="primary"
                prepend-icon="mdi-file-upload"
                @click="$refs.fileInput.click()"
                variant="outlined"
              >
                上传 JSON 文件
              </v-btn>
              <v-btn
                color="primary"
                prepend-icon="mdi-folder-zip"
                @click="$refs.zipInput.click()"
                variant="outlined"
              >
                上传 ZIP 文件
              </v-btn>
              <input
                type="file"
                ref="fileInput"
                @change="handleFileUpload"
                accept=".json"
                style="display: none"
              >
              <input
                type="file"
                ref="zipInput"
                @change="handleZipUpload"
                accept=".zip"
                style="display: none"
              >
            </div>

            <!-- 删除全部按钮 -->
            <div v-if="selectedOperation === '删除'" class="mt-4">
              <v-btn
                color="error"
                prepend-icon="mdi-delete-sweep"
                @click="confirmDeleteAll"
                variant="outlined"
              >
                删除全部数据
              </v-btn>
            </div>
          </v-card-text>
        </v-card>

        <!-- 上传进度条 -->
        <v-card v-if="isUploading" class="mb-6">
          <v-card-text class="pa-6">
            <v-progress-linear
              v-model="uploadProgress"
              color="primary"
              height="20"
              striped
            >
              <template v-slot:default="{ value }">
                <strong>{{ Math.ceil(value) }}%</strong>
              </template>
            </v-progress-linear>
            <div class="text-center mt-2">
              <span class="text-subtitle-1">{{ processedFiles }}/{{ totalFiles }} 文件已上传</span>
            </div>
          </v-card-text>
        </v-card>

        <!-- 新增表单 -->
        <v-card v-if="selectedOperation === '新增'" class="mb-6">
          <v-card-title class="py-4 px-6">新增数据</v-card-title>
          <v-card-text class="pa-6">
            <v-form @submit.prevent="submitData">
              <v-row>
                <v-col
                  v-for="(value, key) in formData"
                  :key="key"
                  cols="12"
                  sm="6"
                  md="4"
                >
                  <v-text-field
                    v-if="key !== 'Path'"
                    v-model="formData[key]"
                    :label="key"
                    :type="getFieldType(key)"
                    variant="outlined"
                    density="comfortable"
                    hide-details
                  ></v-text-field>
                  <v-textarea
                    v-else
                    v-model="formData[key]"
                    :label="key"
                    variant="outlined"
                    auto-grow
                    rows="4"
                    hide-details
                  ></v-textarea>
                </v-col>
              </v-row>
              
              <v-alert
                v-if="error"
                type="error"
                variant="tonal"
                class="mt-4"
                closable
                @click:close="error = ''"
              >
                {{ error }}
              </v-alert>

              <v-btn
                type="submit"
                color="primary"
                class="mt-6"
                :loading="submitting"
                block
              >
                提交
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>

        <!-- 修改表单 -->
        <template v-if="selectedOperation === '修改'">
          <v-card v-if="!materialToModify" class="mb-6">
            <v-card-title class="py-4 px-6">查询要修改的数据</v-card-title>
            <v-card-text class="pa-6">
              <v-form @submit.prevent="fetchMaterialById(selectedMaterialId, 'modify')">
                <v-text-field
                  v-model="selectedMaterialId"
                  label="输入要修改的数据的ID"
                  prefix="2D-"
                  variant="outlined"
                  density="comfortable"
                  hide-details
                  class="mb-4"
                ></v-text-field>
                <v-btn
                  type="submit"
                  color="primary"
                  :loading="loading"
                  block
                >
                  查询
                </v-btn>
              </v-form>
            </v-card-text>
          </v-card>

          <v-card v-else class="mb-6">
            <v-card-title class="py-4 px-6">
              修改数据
              <v-chip class="ml-4" color="primary" variant="outlined">
                ID: 2D-{{ selectedMaterialId }}
              </v-chip>
            </v-card-title>
            <v-card-text class="pa-6">
              <v-form @submit.prevent="updateData" v-model="isValid">
                <v-row>
                  <v-col
                    v-for="(value, key) in formData"
                    :key="key"
                    cols="12"
                    sm="6"
                    md="4"
                    v-if="key !== 'id'"
                  >
                    <v-text-field
                      v-if="key !== 'Path'"
                      v-model="formData[key]"
                      :label="key"
                      :type="getFieldType(key)"
                      variant="outlined"
                      density="comfortable"
                      hide-details
                    ></v-text-field>
                    <v-textarea
                      v-else
                      v-model="formData[key]"
                      :label="key"
                      variant="outlined"
                      auto-grow
                      rows="4"
                      hide-details
                    ></v-textarea>
                  </v-col>
                </v-row>
                <v-btn
                  type="submit"
                  color="primary"
                  class="mt-6"
                  :loading="submitting"
                  :disabled="!isValid || submitting"
                  block
                >
                  提交修改
                </v-btn>
              </v-form>
            </v-card-text>
          </v-card>
        </template>

        <!-- 删除表单 -->
        <template v-if="selectedOperation === '删除'">
          <v-card v-if="!materialToDelete" class="mb-6">
            <v-card-title class="py-4 px-6">删除数据</v-card-title>
            <v-card-text class="pa-6">
              <v-form @submit.prevent="fetchMaterialById(selectedMaterialId, 'delete')">
                <v-text-field
                  v-model="selectedMaterialId"
                  label="输入要删除的数据的ID"
                  prefix="2D-"
                  variant="outlined"
                  density="comfortable"
                  hide-details
                  class="mb-4"
                ></v-text-field>
                <v-btn
                  type="submit"
                  color="error"
                  :loading="loading"
                  block
                >
                  查询
                </v-btn>
              </v-form>
            </v-card-text>
          </v-card>

          <v-card v-else class="mb-6">
            <v-card-title class="py-4 px-6">
              确认删除以下数据
              <v-chip class="ml-4" color="error" variant="outlined">
                ID: 2D-{{ selectedMaterialId }}
              </v-chip>
            </v-card-title>
            <v-card-text class="pa-6">
              <v-alert
                type="warning"
                variant="tonal"
                class="mb-6"
              >
                此操作不可撤销，请确认是否删除该数据。
              </v-alert>
              <pre class="material-data">{{ JSON.stringify(materialToDelete, null, 2) }}</pre>
              <div class="d-flex gap-4 mt-6">
                <v-btn
                  variant="outlined"
                  @click="cancelDeletion"
                  :disabled="submitting"
                  width="140"
                >
                  取消
                </v-btn>
                <v-btn
                  color="error"
                  @click="deleteData"
                  :loading="submitting"
                  width="140"
                >
                  确认删除
                </v-btn>
              </div>
            </v-card-text>
          </v-card>
        </template>
      </v-col>
    </v-row>

    <!-- 确认删除全部对话框 -->
    <v-dialog v-model="showDeleteAllDialog" max-width="500">
      <v-card>
        <v-card-title class="text-h5 pa-6">
          确认删除全部数据
        </v-card-title>
        <v-card-text class="pa-6">
          <v-alert
            type="warning"
            variant="tonal"
            border="start"
          >
            此操作将删除数据库中的所有数据，且不可撤销。请确认是否继续？
          </v-alert>
        </v-card-text>
        <v-card-actions class="pa-6">
          <v-spacer></v-spacer>
          <v-btn
            variant="outlined"
            @click="showDeleteAllDialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="error"
            @click="deleteAllMaterials"
            :loading="submitting"
            class="ml-4"
          >
            确认删除
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 成功提示 -->
    <v-snackbar
      v-model="showSnackbar"
      :color="snackbarColor"
      :timeout="3000"
      location="top"
      class="text-center custom-snackbar"
      rounded="pill"
    >
      <div class="d-flex align-center">
        <v-icon
          :icon="snackbarIcon"
          class="mr-3"
          size="24"
        ></v-icon>
        <span class="text-subtitle-1 font-weight-medium letter-spacing-1">{{ snackbarText }}</span>
      </div>

      <template v-slot:actions>
        <v-btn
          variant="text"
          icon="mdi-close"
          @click="showSnackbar = false"
          class="ml-3"
        ></v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>
import axios from 'axios'
import JSZip from 'jszip'

const getInitialFormData = () => ({
  id: '',
  Path: '',
  Structure: '',
  Formula: '',
  a: '',
  b: '',
  c: '',
  a1: '',
  b1: '',
  c1: '',
  Surface: '',
  Space_group_sym: '',
  Space_group_num: '',
  Point_group: '',
  Crystal_system: '',
  Formation_energy: '',
  Gap: '',
  Direct: '',
  Work: '',
  CBM: '',
  VBM: '',
  DOS_ratio: '',
  xx_e: '',
  yy_e: '',
  zz_e: '',
  xx_i: '',
  yy_i: '',
  zz_i: '',
  xx_a: '',
  yy_a: '',
  zz_a: '',
  layer_distance: '',
  direct_thickness: '',
  supercell_height: '',
  dielectric_const_in_plane_e: '',
  dielectric_const_out_plane_e: '',
  dielectric_const_in_plane_a: '',
  dielectric_const_out_plane_a: '',
  hall_symbol: '',
  layer_group_sym: '',
  layer_group_num: '',
  Magnetic_state: '',
  Total_magnetic_moment: '',
  Local_spin_magnetic_moment: '',
  P: '',
  Ea: '',
  FE: '',
  inversion: '',
  polar: ''
})

export default {
  name: 'Manage',

  data() {
    return {
      selectedOperation: '新增',
      operations: [
        { value: '新增', text: '新增数据' },
        { value: '修改', text: '修改数据' },
        { value: '删除', text: '删除数据' }
      ],
      selectedMaterialId: '',
      formData: getInitialFormData(),
      materialToModify: null,
      materialToDelete: null,
      isUploading: false,
      uploadProgress: 0,
      processedFiles: 0,
      totalFiles: 0,
      loading: false,
      submitting: false,
      isValid: false,
      error: '',
      showDeleteAllDialog: false,
      showSnackbar: false,
      snackbarText: '',
      snackbarColor: 'success',
      snackbarIcon: 'mdi-check-circle'
    }
  },

  methods: {
    setOperation(operation) {
      this.selectedOperation = operation
      this.materialToModify = null
      this.materialToDelete = null
      this.selectedMaterialId = ''
      this.formData = getInitialFormData()
    },

    getFieldType(key) {
      if (key === 'id' || key.includes('Formula') || key.includes('Structure')) {
        return 'text'
      }
      return 'number'
    },

    showSuccessMessage(text) {
      this.snackbarText = text
      this.snackbarColor = 'success'
      this.snackbarIcon = 'mdi-check-circle'
      this.showSnackbar = true
    },

    showErrorMessage(text) {
      this.snackbarText = text
      this.snackbarColor = 'error'
      this.snackbarIcon = 'mdi-alert-circle'
      this.showSnackbar = true
    },

    async handleFileUpload(event) {
      const file = event.target.files[0]
      if (!file) return

      try {
        const reader = new FileReader()
        reader.onload = async (e) => {
          try {
            const data = JSON.parse(e.target.result)
            await this.submitData(data)
            this.showSuccessMessage('成功添加 1 条数据')
            event.target.value = ''
          } catch (error) {
            console.error('Error parsing JSON:', error)
            this.error = '文件格式错误或数据无效'
            this.showErrorMessage('添加失败：文件格式错误或数据无效')
          }
        }
        reader.readAsText(file)
      } catch (error) {
        console.error('Error reading file:', error)
        this.error = '文件读取失败'
        this.showErrorMessage('添加失败：文件读取失败')
      }
    },

    async handleZipUpload(event) {
      const file = event.target.files[0]
      if (!file) return

      this.isUploading = true
      this.uploadProgress = 0
      this.processedFiles = 0
      this.totalFiles = 0
      let successCount = 0

      try {
        const zip = new JSZip()
        const zipContent = await zip.loadAsync(file)
        const jsonFiles = Object.keys(zipContent.files).filter(name => name.endsWith('.json'))
        
        this.totalFiles = jsonFiles.length
        
        for (const fileName of jsonFiles) {
          try {
            const content = await zipContent.files[fileName].async('text')
            const data = JSON.parse(content)
            await this.submitData(data)
            this.processedFiles++
            successCount++
            this.uploadProgress = (this.processedFiles / this.totalFiles) * 100
          } catch (error) {
            console.error(`Error processing ${fileName}:`, error)
          }
        }

        if (successCount > 0) {
          this.showSuccessMessage(`成功添加 ${successCount} 条数据`)
        }
      } catch (error) {
        console.error('Error processing ZIP file:', error)
        this.showErrorMessage('添加失败：ZIP文件处理失败')
      } finally {
        this.isUploading = false
        this.uploadProgress = 0
        this.processedFiles = 0
        this.totalFiles = 0
        event.target.value = ''
      }
    },

    async fetchMaterialById(materialId, operation) {
      if (!materialId) {
        // TODO: 显示错误提示
        return
      }

      this.loading = true
      try {
        const response = await axios.get(`/materials/2D-${materialId}`)
        if (!response.data) {
          this.showErrorMessage(`ID为 2D-${materialId} 的材料不存在`)
          return
        }
        
        if (operation === 'modify') {
          this.materialToModify = response.data
          this.formData = { ...getInitialFormData(), ...response.data }
        } else if (operation === 'delete') {
          this.materialToDelete = response.data
        }
      } catch (error) {
        console.error('Error fetching material:', error)
        this.showErrorMessage(error.response?.data?.message || `查询ID为 2D-${materialId} 的材料失败`)
      } finally {
        this.loading = false
      }
    },

    async submitData(event) {
      const submitData = event instanceof SubmitEvent ? this.formData : event
      console.log('提交的数据:', submitData)
      
      if (!submitData.id) {
        this.error = '请输入ID'
        return
      }

      const materialId = submitData.id.startsWith('2D-') ? submitData.id : `2D-${submitData.id}`
      
      this.submitting = true
      try {
        const response = await axios.post('/add_material', {
          ...submitData,
          id: materialId
        })
        if (event instanceof SubmitEvent) {
          this.formData = getInitialFormData()
          this.showSuccessMessage('成功添加 1 条数据')
        }
        this.error = ''
      } catch (error) {
        console.error('Error adding material:', error)
        this.error = error.response?.data?.message || '添加失败，请稍后重试'
        if (event instanceof SubmitEvent) {
          this.showErrorMessage(this.error)
        }
        throw error
      } finally {
        this.submitting = false
      }
    },

    async updateData() {
      if (!this.selectedMaterialId) {
        return
      }

      this.submitting = true
      try {
        await axios.put(`/update_material/2D-${this.selectedMaterialId}`, this.formData)
        this.formData = getInitialFormData()
        this.selectedMaterialId = ''
        this.materialToModify = null
        this.showSuccessMessage('修改成功')
      } catch (error) {
        console.error('Error updating material:', error)
        this.error = error.response?.data?.message || '修改失败，请稍后重试'
        this.showErrorMessage(this.error)
      } finally {
        this.submitting = false
      }
    },

    async deleteData() {
      if (!this.selectedMaterialId) {
        return
      }

      this.submitting = true
      try {
        await axios.delete(`/delete_material/2D-${this.selectedMaterialId}`)
        this.selectedMaterialId = ''
        this.materialToDelete = null
        this.showSuccessMessage('成功删除 1 条数据')
      } catch (error) {
        console.error('Error deleting material:', error)
        this.error = error.response?.data?.message || '删除失败，请稍后重试'
        this.showErrorMessage(this.error)
      } finally {
        this.submitting = false
      }
    },

    confirmDeleteAll() {
      this.showDeleteAllDialog = true
    },

    async deleteAllMaterials() {
      this.submitting = true
      try {
        const response = await axios.delete('/delete_all_materials')
        const count = response.data?.count || '所有'
        this.showDeleteAllDialog = false
        this.showSuccessMessage(`成功删除 ${count} 条数据`)
      } catch (error) {
        console.error('Error deleting all materials:', error)
        this.error = error.response?.data?.message || '删除失败，请稍后重试'
        this.showErrorMessage(this.error)
      } finally {
        this.submitting = false
      }
    },

    cancelDeletion() {
      this.materialToDelete = null
      this.selectedMaterialId = ''
    }
  }
}
</script>

<style scoped>
.manage-page {
  background-color: #f8f9fa;
  min-height: calc(100vh - 64px);
}

.operation-btn {
  min-width: 120px;
}

.gap-4 {
  gap: 16px;
}

.material-data {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  font-family: monospace;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 400px;
  overflow-y: auto;
}

.v-card {
  border: 1px solid rgba(0, 0, 0, 0.05) !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05) !important;
}

/* 中文字体样式 */
.v-card-title {
  font-family: "PingFang SC", "Microsoft YaHei", "Hiragino Sans GB", sans-serif;
  font-size: 1.25rem;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.v-btn {
  font-family: "PingFang SC", "Microsoft YaHei", "Hiragino Sans GB", sans-serif;
  font-weight: 600;
}

.v-text-field,
.v-select {
  font-family: "PingFang SC", "Microsoft YaHei", "Hiragino Sans GB", sans-serif;
  font-weight: 500;
}

/* 数据库管理标题 */
.text-h5 {
  font-family: "PingFang SC", "Microsoft YaHei", "Hiragino Sans GB", sans-serif;
  font-size: 1.5rem !important;
  font-weight: 700;
  letter-spacing: 0.5px;
}

/* 操作按钮组样式 */
.operation-btn {
  font-family: "PingFang SC", "Microsoft YaHei", "Hiragino Sans GB", sans-serif;
  font-size: 1rem;
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 提示框样式 */
.custom-snackbar {
  margin-top: 20px !important;
}

:deep(.custom-snackbar .v-snackbar__wrapper) {
  min-width: 300px !important;
  padding: 16px 20px !important;
}

:deep(.custom-snackbar .v-snackbar__content) {
  padding: 0 !important;
}

.letter-spacing-1 {
  letter-spacing: 0.5px !important;
}

:deep(.custom-snackbar.v-snackbar--color-success .v-snackbar__wrapper) {
  background: linear-gradient(45deg, #4caf50, #66bb6a) !important;
  box-shadow: 0 3px 8px rgba(76, 175, 80, 0.3) !important;
}

:deep(.custom-snackbar.v-snackbar--color-error .v-snackbar__wrapper) {
  background: linear-gradient(45deg, #f44336, #ef5350) !important;
  box-shadow: 0 3px 8px rgba(244, 67, 54, 0.3) !important;
}
</style>