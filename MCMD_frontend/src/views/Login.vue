<template>
  <div class="login-page bg-grey-lighten-4">
    <v-container fluid class="fill-height">
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="6" lg="4">
          <v-card class="elevation-4 rounded-lg login-card">
            <!-- Logo区域 -->
            <div class="text-center pt-6">
              <v-avatar
                color="primary"
                size="64"
                class="mb-3"
              >
                <span class="text-h5 font-weight-bold white--text">M</span>
              </v-avatar>
              <h1 class="text-h4 font-weight-bold mb-1">Welcome to MCMD</h1>
              <p class="text-subtitle-1 text-medium-emphasis">
                Sign in to continue to Magnetocaloric Materials Database
              </p>
            </div>

            <v-card-text class="pt-4">
              <v-form @submit.prevent="login" v-model="isValid">
                <v-text-field
                  v-model="username"
                  label="Username"
                  prepend-inner-icon="mdi-account"
                  variant="outlined"
                  :rules="[v => !!v || 'Username is required']"
                  required
                  class="mb-4"
                  density="comfortable"
                  bg-color="grey-lighten-5"
                ></v-text-field>

                <v-text-field
                  v-model="password"
                  label="Password"
                  prepend-inner-icon="mdi-lock"
                  :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                  @click:append-inner="showPassword = !showPassword"
                  :type="showPassword ? 'text' : 'password'"
                  variant="outlined"
                  :rules="[v => !!v || 'Password is required']"
                  required
                  class="mb-2"
                  density="comfortable"
                  bg-color="grey-lighten-5"
                ></v-text-field>

                <v-alert
                  v-if="error"
                  type="error"
                  variant="tonal"
                  class="mb-4"
                  closable
                  density="comfortable"
                >
                  {{ error }}
                </v-alert>

                <v-btn
                  type="submit"
                  color="primary"
                  block
                  size="large"
                  :loading="loading"
                  :disabled="!isValid || loading"
                  class="mb-2 login-btn"
                  elevation="2"
                  height="48"
                >
                  Sign In
                  <template v-slot:loader>
                    <v-progress-circular
                      indeterminate
                      color="white"
                      size="20"
                    ></v-progress-circular>
                  </template>
                </v-btn>
              </v-form>
            </v-card-text>

            <v-card-text class="text-center pb-6 text-medium-emphasis">
              <p class="text-caption">
                MCMD@TCPM2
              </p>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import { auth } from '@/utils/auth'

export default {
  name: 'Login',

  data() {
    return {
      username: '',
      password: '',
      error: '',
      loading: false,
      showPassword: false,
      isValid: false
    }
  },

  methods: {
    async login() {
      if (!this.isValid) return

      this.loading = true
      this.error = ''

      try {
        const response = await auth.login(this.username, this.password)
        
        if (response.success) {
          // 存储用户信息和令牌
          sessionStorage.setItem('username', this.username)
          
          // 触发一个自定义事件，通知App组件登录状态已更改
          window.dispatchEvent(new CustomEvent('login-success'))
          
          // 如果有重定向地址，则跳转到该地址
          const redirect = this.$route.query.redirect || '/manage'
          this.$router.push(redirect)
          
          // 添加一个短暂的延迟后刷新页面，确保状态更新和AI Chat显示
          setTimeout(() => {
            window.location.reload()
          }, 100)
        } else {
          this.error = response.message || 'Login failed, please check your credentials.'
        }
      } catch (error) {
        console.error('Login error:', error)
        this.error = 'Login failed, please try again later.'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 64px);
  width: 100%;
  display: flex;
  align-items: flex-start;
  padding-top: 80px;
}

.login-card {
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
  border-radius: 16px !important;
  overflow: hidden;
  border: none !important;
}

.login-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12) !important;
}

.login-btn {
  letter-spacing: 0.5px;
}

/* 输入框样式 */
:deep(.v-field) {
  border-radius: 8px !important;
}

:deep(.v-field__outline) {
  border-width: 1px !important;
}

:deep(.v-field--variant-outlined.v-field--focused .v-field__outline) {
  border-width: 2px !important;
}
</style>