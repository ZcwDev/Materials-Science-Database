<template>
  <v-container fluid class="pa-0">
    <v-row>
      <v-col cols="12" class="pa-0">
        <div class="canvas-container">
          <canvas id="chemdoodle-viewer"></canvas>
          <div class="element-legend" v-if="elementColors.length > 0">
            <div v-for="(element, index) in elementColors" :key="index" class="element-item">
              <div class="color-box" :style="{ backgroundColor: element.color }"></div>
              <span class="element-symbol">{{ element.symbol }}</span>
            </div>
          </div>
        </div>
      </v-col>
      <v-col cols="12" class="pa-4">
        <v-container fluid class="pa-0">
          <v-row no-gutters class="controls-row">
            <!-- 超晶胞控制部分 -->
            <v-col cols="12" sm="8" md="8" lg="9" class="d-flex align-center mb-3 mb-sm-0">
              <div class="supercell-inputs me-4">
                <v-text-field v-model="nx" placeholder="2" class="custom-input custom-label" variant="outlined" density="compact"
                  hide-details type="number" min="1" max="5" label="α"></v-text-field>
                <v-text-field v-model="ny" placeholder="2" class="custom-input custom-label" variant="outlined" density="compact"
                  hide-details type="number" min="1" max="5" label="β"></v-text-field>
                <v-text-field v-model="nz" placeholder="2" class="custom-input custom-label" variant="outlined" density="compact"
                  hide-details type="number" min="1" max="5" label="γ"></v-text-field>
              </div>
              <div class="button-container">
                <v-btn color="info" @click="rebuild" class="control-btn aligned-button" prepend-icon="mdi-cube-outline">
                  Set supercell
                </v-btn>
              </div>
            </v-col>
            
            <!-- Primary Cell 按钮 -->
            <v-col cols="12" sm="4" md="4" lg="3" class="d-flex justify-end align-center">
              <v-btn color="primary" @click="setCell1" class="control-btn" prepend-icon="mdi-cube" variant="outlined">
                Primary
              </v-btn>
            </v-col>
          </v-row>
          
          <!-- 显示控制按钮 -->
          <v-row class="mt-4">
            <v-col cols="12">
              <div class="button-grid">
                <v-btn color="info" @click="toggleRep" class="control-btn" prepend-icon="mdi-molecule">
                  {{ rep === 'BS' ? 'Ball+Stick' : 'Spacefill' }}
                </v-btn>
                <v-btn color="info" @click="toggleLabel" class="control-btn" prepend-icon="mdi-label-outline">
                  Atom Label
                </v-btn>
                <v-btn color="info" @click="resetView" class="control-btn" prepend-icon="mdi-refresh"
                  variant="outlined">
                  Reset
                </v-btn>
              </div>
            </v-col>
          </v-row>
        </v-container>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'ChemDoodle',

  data() {
    return {
      background: false,
      canvas: null,
      cell: null,
      nx: 2,
      ny: 2,
      nz: 2,
      rep: 'BS',
      cellType: 'primitive',
      elementColors: [],
      resizeObserver: null
    }
  },

  props: {
    cifs: {
      type: Object,
      required: true
    }
  },

  watch: {
    cifs: {
      handler(newVal) {
        if (newVal && newVal.primitive) {
          this.$nextTick(() => {
            if (!this.canvas) {
              this.setupCanvas()
            } else {
              this.rebuild()
            }
          })
        }
      },
      immediate: true
    }
  },

  mounted() {
    // 创建 ResizeObserver 来监听容器大小变化
    this.resizeObserver = new ResizeObserver(this.handleResize)
    const container = document.getElementById('chemdoodle-viewer')?.parentElement
    if (container) {
      this.resizeObserver.observe(container)
    }

    // 初始化画布
    this.$nextTick(() => {
      this.setupCanvas()
    })
  },

  beforeDestroy() {
    // 清理 ResizeObserver
    if (this.resizeObserver) {
      this.resizeObserver.disconnect()
    }

    // 清理 WebGL 上下文
    if (this.canvas) {
      const gl = this.canvas.gl
      if (gl) {
        gl.getExtension('WEBGL_lose_context')?.loseContext()
      }
      this.canvas = null
    }

    // 移除 canvas 元素
    const canvas = document.getElementById('chemdoodle-viewer')
    if (canvas) {
      canvas.remove()
    }
  },

  methods: {
    handleResize(entries) {
      for (const entry of entries) {
        const width = Math.floor(entry.contentRect.width)
        const height = 400

        if (this.canvas) {
          // 更新 canvas 大小
          const canvas = document.getElementById('chemdoodle-viewer')
          if (canvas) {
            canvas.width = width
            canvas.height = height
            this.canvas.resize(width, height)
            // 重新渲染场景
            if (this.cell) {
              this.renderAllUnitCells()
            }
          }
        }
      }
    },

    setupCanvas() {
      try {
        // 检查是否已存在 canvas，如果存在则先移除
        let existingCanvas = document.getElementById('chemdoodle-viewer')
        if (existingCanvas) {
          existingCanvas.remove()
        }

        // 创建新的 canvas 元素
        const container = document.querySelector('.canvas-container')
        if (!container) return

        const canvas = document.createElement('canvas')
        canvas.id = 'chemdoodle-viewer'
        container.insertBefore(canvas, container.firstChild)

        const width = Math.floor(container.clientWidth)
        const height = 400

        canvas.width = width
        canvas.height = height

        // 初始化 ChemDoodle canvas
        this.canvas = new window.ChemDoodle.TransformCanvas3D('chemdoodle-viewer', width, height)

        this.canvas.specs.set3DRepresentation('Ball and Stick')
        this.canvas.specs.backgroundColor = '#ffffff'
        this.canvas.specs.projectionPerspective_3D = false
        this.canvas.specs.projectionOrthoWidth_3D = 40

        this.canvas.specs.crystals_unitCellLineWidth = 1.5
        this.canvas.specs.shapes_color = '#000000'
        this.canvas.specs.shapes_lineWidth = 1.5
        this.canvas.specs.crystals_displayUnitCell = true

        this.canvas.specs.atoms_useJMOLColors = true
        this.canvas.specs.atoms_sphereDiameter_3D = 0.5
        this.canvas.specs.bonds_cylinderDiameter_3D = 0.2
        this.canvas.specs.atoms_displayLabels_3D = false

        // 改进原子标签设置
        this.canvas.specs.atoms_font_size_2D = 18
        this.canvas.specs.atoms_font_families_2D = ['Arial', 'sans-serif']
        this.canvas.specs.atoms_font_bold_2D = true
        this.canvas.specs.atoms_font_size_3D = 18
        this.canvas.specs.atoms_font_families_3D = ['Arial', 'sans-serif']
        this.canvas.specs.atoms_font_bold_3D = true
        this.canvas.specs.text_font_size = 18
        this.canvas.specs.text_font_families = ['Arial', 'sans-serif']
        this.canvas.specs.text_font_bold = true
        
        // 添加标签背景和边缘增强
        this.canvas.specs.atoms_useTextShadow_3D = true  // 使用文本阴影
        this.canvas.specs.atoms_textShadowBlur_3D = 6    // 阴影模糊半径
        this.canvas.specs.atoms_textShadowColor_3D = '#FFFFFF' // 白色阴影
        this.canvas.specs.atoms_textColor_3D = '#000000' // 黑色文本
        this.canvas.specs.atoms_implicitHydrogens_2D = true

        this.canvas.specs.projectionFrontCulling_3D = 0.1
        this.canvas.specs.projectionBackCulling_3D = 10000
        this.canvas.specs.shadow_3D = false
        this.canvas.specs.fog_3D_enabled = false
        this.canvas.specs.compass_display = true
        this.canvas.specs.compass_size_3D = 50
        this.canvas.specs.compass_displayText_3D = true
        this.canvas.specs.compass_offsetX_3D = -10
        this.canvas.specs.compass_offsetY_3D = -10
        this.canvas.specs.atoms_displayAllCarbonLabels_3D = true
        this.canvas.specs.legend_display = true
        this.canvas.specs.legend_fontSize = 12
        this.canvas.specs.legend_fontColor = '#000000'
        this.canvas.specs.legend_backgroundColor = 'rgba(255, 255, 255, 0.8)'
        this.canvas.specs.legend_borderColor = 'rgba(0, 0, 0, 0.1)'
        this.canvas.specs.legend_offsetX = -10
        this.canvas.specs.legend_offsetY = -10
        this.canvas.specs.legend_maxWidth = 120
        this.canvas.specs.atoms_resolution_3D = 30
        this.canvas.specs.bonds_resolution_3D = 30
        this.canvas.specs.crystals_unitCellLineStyle = 'solid'

        if (this.cifs && this.cifs[this.cellType]) {
          this.rebuild()
        }
      } catch (error) {
        console.error('Error in setupCanvas:', error)
      }
    },

    rebuild() {
      try {
        if (!this.cifs || !this.cifs[this.cellType]) {
          return
        }

        let cell = window.ChemDoodle.readCIF(this.cifs[this.cellType], this.nx, this.ny, this.nz)
        if (!cell || !cell.molecule) {
          return
        }
        this.canvas.loadContent([cell.molecule], [cell.unitCell])
        this.cell = cell
        this.renderAllUnitCells()

        this.updateElementColors(cell.molecule)

        // 通知父组件结构已准备好
        this.$emit('structure-ready')
      } catch (error) {
        console.error('Error in rebuild:', error)
      }
    },

    updateElementColors(molecule) {
      const elements = new Set()
      molecule.atoms.forEach(atom => {
        elements.add(atom.label)
      })

      this.elementColors = Array.from(elements).map(symbol => ({
        symbol,
        color: this.getJmolColor(symbol)
      }))
    },

    getJmolColor(symbol) {
      const jmolColors = {
        // 第一周期
        'H': '#FFFFFF', 'He': '#D9FFFF',

        // 第二周期
        'Li': '#CC80FF', 'Be': '#C2FF00', 'B': '#FFB5B5', 'C': '#909090',
        'N': '#3050F8', 'O': '#FF0D0D', 'F': '#90E050', 'Ne': '#B3E3F5',

        // 第三周期
        'Na': '#AB5CF2', 'Mg': '#8AFF00', 'Al': '#BFA6A6', 'Si': '#F0C8A0',
        'P': '#FF8000', 'S': '#FFFF30', 'Cl': '#1FF01F', 'Ar': '#80D1E3',

        // 第四周期
        'K': '#8F40D4', 'Ca': '#3DFF00', 'Sc': '#E6E6E6', 'Ti': '#BFC2C7',
        'V': '#A6A6AB', 'Cr': '#8A99C7', 'Mn': '#9C7AC7', 'Fe': '#E06633',
        'Co': '#F090A0', 'Ni': '#50D050', 'Cu': '#C88033', 'Zn': '#7D80B0',
        'Ga': '#C28F8F', 'Ge': '#668F8F', 'As': '#BD80E3', 'Se': '#FFA100',
        'Br': '#A62929', 'Kr': '#5CB8D1',

        // 第五周期
        'Rb': '#702EB0', 'Sr': '#00FF00', 'Y': '#94FFFF', 'Zr': '#94E0E0',
        'Nb': '#73C2C9', 'Mo': '#54B5B5', 'Tc': '#3B9E9E', 'Ru': '#248F8F',
        'Rh': '#0A7D8C', 'Pd': '#006985', 'Ag': '#C0C0C0', 'Cd': '#FFD98F',
        'In': '#A67573', 'Sn': '#668080', 'Sb': '#9E63B5', 'Te': '#D47A00',
        'I': '#940094', 'Xe': '#429EB0',

        // 第六周期
        'Cs': '#57178F', 'Ba': '#00C900',
        // 镧系
        'La': '#70D4FF', 'Ce': '#FFFFC7', 'Pr': '#D9FFC7', 'Nd': '#C7FFC7',
        'Pm': '#A3FFC7', 'Sm': '#8FFFC7', 'Eu': '#61FFC7', 'Gd': '#45FFC7',
        'Tb': '#30FFC7', 'Dy': '#1FFFC7', 'Ho': '#00FF9C', 'Er': '#00E675',
        'Tm': '#00D452', 'Yb': '#00BF38', 'Lu': '#00AB24',
        // 第六周期（续）
        'Hf': '#4DC2FF', 'Ta': '#4DA6FF', 'W': '#2194D6', 'Re': '#267DAB',
        'Os': '#266696', 'Ir': '#175487', 'Pt': '#D0D0E0', 'Au': '#FFD123',
        'Hg': '#B8B8D0', 'Tl': '#A6544D', 'Pb': '#575961', 'Bi': '#9E4FB5',
        'Po': '#AB5C00', 'At': '#754F45', 'Rn': '#428296',

        // 第七周期
        'Fr': '#420066', 'Ra': '#007D00',
        // 锕系
        'Ac': '#70ABFA', 'Th': '#00BAFF', 'Pa': '#00A1FF', 'U': '#008FFF',
        'Np': '#0080FF', 'Pu': '#006BFF', 'Am': '#545CF2', 'Cm': '#785CE3',
        'Bk': '#8A4FE3', 'Cf': '#A136D4', 'Es': '#B31FD4', 'Fm': '#B31FBA',
        'Md': '#B30DA6', 'No': '#BD0D87', 'Lr': '#C70066',
        // 第七周期（续）
        'Rf': '#CC0059', 'Db': '#D1004F', 'Sg': '#D90045', 'Bh': '#E00038',
        'Hs': '#E6002E', 'Mt': '#EB0026', 'Ds': '#EF001C', 'Rg': '#F30012',
        'Cn': '#F60009', 'Nh': '#F90001', 'Fl': '#FC0001', 'Mc': '#FF0000',
        'Lv': '#FF0D0D', 'Ts': '#FF1A1A', 'Og': '#FF2828'
      }
      return jmolColors[symbol] || '#808080'
    },
    renderAllUnitCells() {
      if (!this.canvas || !this.cell) return

      const { molecule, unitCell } = this.cell
      const cells = []

      // 获取基础向量
      const a = [
        unitCell.unitCell.x[0] - unitCell.unitCell.o[0],
        unitCell.unitCell.x[1] - unitCell.unitCell.o[1],
        unitCell.unitCell.x[2] - unitCell.unitCell.o[2]
      ]
      const b = [
        unitCell.unitCell.y[0] - unitCell.unitCell.o[0],
        unitCell.unitCell.y[1] - unitCell.unitCell.o[1],
        unitCell.unitCell.y[2] - unitCell.unitCell.o[2]
      ]
      const c = [
        unitCell.unitCell.z[0] - unitCell.unitCell.o[0],
        unitCell.unitCell.z[1] - unitCell.unitCell.o[1],
        unitCell.unitCell.z[2] - unitCell.unitCell.o[2]
      ]

      // 为每个原胞创建一个新的UnitCell实例
      for (let i = 0; i < this.nx; i++) {
        for (let j = 0; j < this.ny; j++) {
          for (let k = 0; k < this.nz; k++) {
            // 计算偏移向量
            const offset = {
              x: i * a[0] + j * b[0] + k * c[0],
              y: i * a[1] + j * b[1] + k * c[1],
              z: i * a[2] + j * b[2] + k * c[2]
            }

            // 创建新的原胞顶点
            const newCell = {
              o: [unitCell.unitCell.o[0] + offset.x, unitCell.unitCell.o[1] + offset.y, unitCell.unitCell.o[2] + offset.z],
              x: [unitCell.unitCell.x[0] + offset.x, unitCell.unitCell.x[1] + offset.y, unitCell.unitCell.x[2] + offset.z],
              y: [unitCell.unitCell.y[0] + offset.x, unitCell.unitCell.y[1] + offset.y, unitCell.unitCell.y[2] + offset.z],
              z: [unitCell.unitCell.z[0] + offset.x, unitCell.unitCell.z[1] + offset.y, unitCell.unitCell.z[2] + offset.z],
              xy: [unitCell.unitCell.xy[0] + offset.x, unitCell.unitCell.xy[1] + offset.y, unitCell.unitCell.xy[2] + offset.z],
              xz: [unitCell.unitCell.xz[0] + offset.x, unitCell.unitCell.xz[1] + offset.y, unitCell.unitCell.xz[2] + offset.z],
              yz: [unitCell.unitCell.yz[0] + offset.x, unitCell.unitCell.yz[1] + offset.y, unitCell.unitCell.yz[2] + offset.z],
              xyz: [unitCell.unitCell.xyz[0] + offset.x, unitCell.unitCell.xyz[1] + offset.y, unitCell.unitCell.xyz[2] + offset.z]
            }

            cells.push(new window.ChemDoodle.structures.d3.UnitCell(newCell))
          }
        }
      }

      // 渲染所有原胞
      this.canvas.loadContent([molecule], cells)
    },
    repaint() {
      if (this.canvas && this.cell) {
        this.renderAllUnitCells()
      }
    },

    setCell1() {
      this.nx = this.ny = this.nz = 1
      this.rebuild()
    },

    toggleRep() {
      if (this.rep === 'BS') {
        this.rep = 'vdW'
        this.canvas.specs.set3DRepresentation('van der Waals Spheres')
      } else {
        this.rep = 'BS'
        this.canvas.specs.set3DRepresentation('Ball and Stick')
      }
      this.repaint()
    },

    toggleLabel() {
      if (this.canvas.specs.atoms_displayLabels_3D) {
        this.canvas.specs.atoms_displayLabels_3D = false
      } else {
        this.canvas.specs.atoms_displayLabels_3D = true
         
        // 优化标签显示
        this.canvas.specs.atoms_font_size_3D = 18
        this.canvas.specs.atoms_font_families_3D = ['Arial', 'sans-serif']
        this.canvas.specs.atoms_font_bold_3D = true
        
        // 加强文本轮廓清晰度
        this.canvas.specs.atoms_useTextShadow_3D = true
        this.canvas.specs.atoms_textShadowBlur_3D = 6
        this.canvas.specs.atoms_textShadowColor_3D = '#FFFFFF'
        this.canvas.specs.atoms_textColor_3D = '#000000'
        
        // 确保所有原子都显示标签
        this.canvas.specs.atoms_displayAllCarbonLabels_3D = true
      }
      this.repaint()
    },

    resetView() {
      this.cellType = 'primitive'
      this.nx = this.ny = this.nz = 2
      this.setupCanvas()
    }
  }
}
</script>

<style>
.canvas-container {
  width: 100%;
  height: 400px;
  position: relative;
  background: #ffffff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

#chemdoodle-viewer {
  width: 100%;
  height: 100%;
  display: block;
  border: none;
  outline: none;
}

.element-legend {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.95);
  padding: 8px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid rgba(0, 0, 0, 0.1);
  min-width: 60px;
}

.element-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 2px;
}

.color-box {
  width: 16px;
  height: 16px;
  border-radius: 3px;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.element-symbol {
  font-size: 12px;
  font-family: 'Roboto', sans-serif;
  font-weight: 600;
  color: rgba(0, 0, 0, 0.87);
  letter-spacing: 0.5px;
}

.supercell-inputs {
  display: flex;
  gap: 12px;
  background: rgba(0, 0, 0, 0.02);
  padding: 8px 12px;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.05);
  width: fit-content;
  margin-right: 9px !important;
}

.custom-input {
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  width: 60px;
}

.custom-input :deep(.v-field__field) {
  padding-top: 12px !important;
  padding-bottom: 0 !important;
}

.custom-input :deep(.v-field__input) {
  padding-top: 0 !important;
  padding-bottom: 0 !important;
  min-height: 28px !important;
}

.custom-input :deep(.v-label) {
  font-size: 11px;
  opacity: 0.7;
  font-weight: 500;
  margin-top: -2px;
}

.custom-input :deep(.v-field) {
  padding-top: 0 !important;
  padding-bottom: 0 !important;
}

.control-btn {
  border-radius: 8px !important;
  font-weight: 500 !important;
  letter-spacing: 0.5px;
  min-width: 160px;
  height: 36px;
  transition: all 0.2s ease;
}

.control-btn.wide-btn {
  min-width: 180px;
}

.view-control-btn {
  flex: 1;
  min-width: 120px !important;
  max-width: 180px;
}

.controls-row {
  margin: 0 -4px;
}

.controls-row > [class*="col-"] {
  padding: 0 4px;
}

.control-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

/* 添加响应式样式，处理按钮缩放问题 */
@media (max-width: 1200px) {
  .control-btn {
    min-width: 140px;
    font-size: 0.875rem;
  }
  
  .control-btn.wide-btn {
    min-width: 160px;
  }
  
  .view-control-btn {
    min-width: 100px !important;
  }
}

@media (max-width: 960px) {
  .control-btn {
    min-width: 120px;
    font-size: 0.8125rem;
    height: 34px;
  }
  
  .control-btn.wide-btn {
    min-width: 140px;
  }
  
  .view-control-btn {
    min-width: 90px !important;
  }
  
  .supercell-inputs {
    padding: 6px 10px;
    gap: 8px;
  }
  
  .custom-input {
    width: 50px;
  }
}

@media (max-width: 768px) {
  .d-flex.justify-space-between {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .control-btn {
    flex: 1;
    min-width: auto;
    padding-left: 8px;
    padding-right: 8px;
  }
  
  .view-control-btn {
    flex-basis: calc(33.33% - 8px);
    max-width: none;
  }
  
  .control-btn.wide-btn {
    min-width: 100%;
    margin-bottom: 8px;
  }
  
  .supercell-inputs {
    flex: 1;
    margin-bottom: 8px;
    justify-content: center;
  }
}

@media (max-width: 600px) {
  .view-control-btn {
    flex-basis: 100%;
    margin-bottom: 8px;
  }
  
  .supercell-inputs {
    width: 100%;
  }
  
  .custom-input {
    flex: 1;
  }
}

.v-btn {
  text-transform: uppercase !important;
}

.d-flex.gap-6 {
  gap: 24px;
}

.custom-label .v-label {
  font-size: 16px;
}

.alignment-fix {
  min-width: 160px !important;
  width: 160px !important;
  margin-left: 0px !important;
}

/* 确保第二行按钮对齐 */
.d-flex.flex-wrap.justify-space-between {
  display: grid !important;
  grid-template-columns: repeat(3, 1fr) !important;
  gap: 16px !important;
}

.button-container {
  width: calc(100% / 3);
  max-width: 180px;
  min-width: 140px;
}

.aligned-button {
  width: 100% !important;
  min-width: 0 !important;
  max-width: none !important;
}

.button-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  width: 100%;
}

.button-grid .control-btn {
  width: 100%;
  min-width: 0 !important;
  max-width: none !important;
  justify-content: center;
  height: 36px;
}

/* 添加响应式样式，处理按钮缩放问题 */
@media (max-width: 1200px) {
  .control-btn {
    min-width: 140px;
    font-size: 0.875rem;
  }
  
  .control-btn.wide-btn {
    min-width: 160px;
  }
  
  .view-control-btn {
    min-width: 100px !important;
  }
}

@media (max-width: 768px) {
  .button-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }
  
  .control-btn {
    min-width: 120px;
    font-size: 0.8125rem;
    height: 34px;
  }
  
  .control-btn.wide-btn {
    min-width: 140px;
  }
  
  .view-control-btn {
    min-width: 90px !important;
  }
  
  .supercell-inputs {
    padding: 6px 10px;
    gap: 8px;
  }
  
  .custom-input {
    width: 50px;
  }
}

@media (max-width: 600px) {
  .button-grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .d-flex.justify-space-between {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .control-btn {
    flex: 1;
    min-width: auto;
    padding-left: 8px;
    padding-right: 8px;
  }
  
  .view-control-btn {
    flex-basis: calc(33.33% - 8px);
    max-width: none;
  }
  
  .control-btn.wide-btn {
    min-width: 100%;
    margin-bottom: 8px;
  }
  
  .supercell-inputs {
    flex: 1;
    margin-bottom: 8px;
    justify-content: center;
  }
}
</style>
