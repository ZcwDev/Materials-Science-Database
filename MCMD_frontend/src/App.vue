<template>
  <v-app>
    <v-app-bar color="primary" dark>
      <v-app-bar-title>
        <router-link to="/" class="text-white text-decoration-none d-flex align-center">
          <div>
            <v-img
              src="/MCMD_newLogo.png"
              alt="Logo"
              width="32"
              height="32"
              class="logo-image"
            />
          </div>
          <div class="ml-2">
            Materials Science Database
          </div>
        </router-link>
      </v-app-bar-title>

      <v-spacer></v-spacer>

      <v-btn to="/" text class="text-uppercase">
        Home
      </v-btn>
      
      <v-btn to="/manage" text class="text-uppercase">
        Manage Database
      </v-btn>
      
      <v-btn to="/about" text class="text-uppercase">
        About
      </v-btn>
    </v-app-bar>

    <v-main>
      <router-view></router-view>
    </v-main>
  </v-app>
</template>

<script>
import AppBar from '@/components/AppBar.vue'
import { auth } from '@/utils/auth'
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'

export default {
  name: 'App',
  components: {
    AppBar
  },
  setup() {
    const router = useRouter()
    const username = ref(sessionStorage.getItem('username'))
    const isLoggedIn = ref(auth.isLoggedIn())

    const goToLogin = () => {
      router.push('/login')
    }

    const logout = async () => {
      try {
        await auth.logout()
        username.value = null
        isLoggedIn.value = false
        router.push('/')
      } catch (error) {
        console.error('Logout failed:', error)
      }
    }

    // 监听登录状态变化
    const checkLoginStatus = () => {
      username.value = sessionStorage.getItem('username')
      isLoggedIn.value = auth.isLoggedIn()
    }

    onMounted(() => {
      checkLoginStatus()
      window.addEventListener('storage', checkLoginStatus)
    })

    onUnmounted(() => {
      window.removeEventListener('storage', checkLoginStatus)
    })

    return {
      username,
      isLoggedIn,
      goToLogin,
      logout
    }
  }
}
</script>

<style>
.v-application {
  font-family: 'Roboto', sans-serif;
}

.main-container {
  height: 100%;
}

.position-relative {
  position: relative;
  height: 100%;
}

.main-content {
  transition: all 0.3s ease-in-out;
  width: 100%;
  margin-left: 0;
}

.content-shifted {
  width: 66.667%;
  margin-left: 33.333%;
}

.v-main {
  padding-top: 64px !important;
}

.main-container {
  height: 100%;
  position: relative;
  overflow-y: auto;
  overflow-x: hidden;
}

/* AI Chat Button Styles */
.ai-chat-group {
  margin-top: 12px;
  margin-right: 10px;
}

.ai-chat-btn {
  height: 36px !important;
  width: 36px !important;
  position: relative;
}

.ai-chat-btn .v-icon {
  transition: transform 0.3s ease;
  opacity: 0.9;
}

.ai-chat-btn:hover .v-icon {
  transform: scale(1.2);
  opacity: 1;
}

.ai-divider {
  border-color: rgba(255, 255, 255, 0.3) !important;
  margin: 6px 0 !important;
  opacity: 0.7;
}

/* 聊天窗口样式 */
.chat-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.chat-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
}

.chat-window {
  position: relative;
  width: 90%;
  max-width: 800px;
  height: 80vh;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 1001;
}

.logo-image {
  background-color: white;
  border-radius: 4px;
  padding: 2px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.v-application {
  background-color: #f5f5f5;
}

.app-bar {
  border-bottom: 1px solid #e0e0e0;
  padding: 2px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 右上角按钮样式 */
.v-app-bar .v-btn {
  font-size: 15px;
  font-weight: 500;
  text-transform: uppercase !important;
}

/* 确保所有带有text-uppercase类的元素都保持大写 */
.text-uppercase {
  text-transform: uppercase !important;
}
</style>
