<template>
  <main class="page">
    <section class="panel login-page-card">
      <span class="eyebrow">Auth</span>
      <h1>登录点评系统</h1>
      <p>本地开发可以直接用账号密码登录；如果同时开启了 GitHub，也可以在这里切换。</p>

      <div v-if="errorMessage" class="alert error">{{ errorMessage }}</div>
      <div v-if="successMessage" class="alert success">{{ successMessage }}</div>

      <div class="auth-switch">
        <button
          class="switch-button"
          :class="{ active: mode === 'login' }"
          type="button"
          @click="mode = 'login'"
        >
          账号登录
        </button>
        <button
          class="switch-button"
          :class="{ active: mode === 'register' }"
          type="button"
          @click="mode = 'register'"
        >
          注册账号
        </button>
      </div>

      <div v-if="localProvider" class="login-panel">
        <form class="review-form" @submit.prevent="submit">
          <input v-model.trim="form.username" type="text" placeholder="用户名，比如 qiu" required />
          <input v-model="form.password" type="password" placeholder="密码，至少 6 位" required />

          <template v-if="mode === 'register'">
            <input v-model.trim="form.displayName" type="text" placeholder="显示名称，可选" />
            <input v-model.trim="form.email" type="email" placeholder="邮箱，可选" />
          </template>

          <button class="button" type="submit" :disabled="submitting">
            {{ submitting ? "提交中..." : mode === "login" ? "登录" : "注册并登录" }}
          </button>
        </form>
      </div>

      <div v-else class="login-panel">
        <p>当前环境没有开启账号密码登录。</p>
      </div>

      <div v-if="oauthProviders.length" class="login-panel">
        <p>也可以使用第三方登录：</p>
        <div class="provider-actions">
          <button
            v-for="provider in oauthProviders"
            :key="provider.registrationId"
            class="button secondary"
            type="button"
            @click="loginWithProvider(provider)"
          >
            {{ provider.clientName }} 登录
          </button>
        </div>
      </div>
    </section>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import {
  authState,
  clearAuthSession,
  consumeLoginRedirectPath,
  isPasswordProvider,
  loadCurrentUser,
  peekLoginRedirectPath,
  refreshAuthProviders,
  setAccessToken,
  startOAuthLogin
} from "../auth";
import { loginWithPassword, registerWithPassword } from "../api";

const router = useRouter();

const mode = ref("login");
const submitting = ref(false);
const errorMessage = ref("");
const successMessage = ref("");
const form = reactive({
  username: "",
  password: "",
  displayName: "",
  email: ""
});

const localProvider = computed(() => authState.providers.find((provider) => isPasswordProvider(provider)) || null);
const oauthProviders = computed(() => authState.providers.filter((provider) => !isPasswordProvider(provider)));

onMounted(async () => {
  await refreshAuthProviders();
  if (authState.user) {
    successMessage.value = "当前已经是登录状态。";
  }
});

async function submit() {
  errorMessage.value = "";
  successMessage.value = "";
  submitting.value = true;

  try {
    const token = mode.value === "login"
      ? await loginWithPassword({
        username: form.username,
        password: form.password
      })
      : await registerWithPassword({
        username: form.username,
        password: form.password,
        displayName: form.displayName,
        email: form.email
      });

    setAccessToken(token.accessToken);
    await loadCurrentUser();
    successMessage.value = mode.value === "login" ? "登录成功，正在返回..." : "注册成功，正在返回...";
    await router.replace(consumeLoginRedirectPath());
  } catch (error) {
    clearAuthSession();
    errorMessage.value = error.message || "登录失败，请稍后重试。";
  } finally {
    submitting.value = false;
  }
}

function loginWithProvider(provider) {
  startOAuthLogin(provider, peekLoginRedirectPath());
}
</script>
