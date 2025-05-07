<template>
  <div v-if="show" class="loading-overlay">
    <div class="loading-content">
      <div class="molecule-spinner">
        <div class="atom atom1"></div>
        <div class="atom atom2"></div>
        <div class="atom atom3"></div>
        <div class="orbit orbit1"></div>
        <div class="orbit orbit2"></div>
        <div class="orbit orbit3"></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoadingOverlay',
  
  props: {
    show: {
      type: Boolean,
      default: false
    }
  }
}
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  backdrop-filter: blur(4px);
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.molecule-spinner {
  position: relative;
  width: 80px;
  height: 80px;
  animation: rotate 12s linear infinite;
}

.atom {
  position: absolute;
  width: 12px;
  height: 12px;
  background: var(--v-primary-base);
  border-radius: 50%;
  box-shadow: 0 0 15px var(--v-primary-base);
}

.atom1 { top: 50%; left: 50%; transform: translate(-50%, -50%); }
.atom2 { top: 0; left: 50%; transform: translateX(-50%); }
.atom3 { top: 50%; right: 0; transform: translateY(-50%); }

.orbit {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 60px;
  height: 60px;
  border: 2px solid rgba(var(--v-primary-base), 0.2);
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.orbit1 { animation: orbit1 3s linear infinite; }
.orbit2 { animation: orbit2 4s linear infinite; }
.orbit3 { animation: orbit3 5s linear infinite; }

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes orbit1 {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to { transform: translate(-50%, -50%) rotate(360deg); }
}

@keyframes orbit2 {
  from { transform: translate(-50%, -50%) rotate(120deg); }
  to { transform: translate(-50%, -50%) rotate(480deg); }
}

@keyframes orbit3 {
  from { transform: translate(-50%, -50%) rotate(240deg); }
  to { transform: translate(-50%, -50%) rotate(600deg); }
}
</style>
  