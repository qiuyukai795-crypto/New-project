<template>
  <div class="app-shell">
    <header class="topbar">
      <RouterLink class="brand" to="/">
        <span class="brand-mark">DP</span>
        <div>
          <strong>大众点评 Demo</strong>
          <p>Vue 3 + Spring Boot 前后端分离版</p>
        </div>
      </RouterLink>
      <nav class="topnav">
        <RouterLink to="/">首页</RouterLink>
        <template v-if="authState.user">
          <span class="user-pill">{{ authState.user.displayName }}</span>
          <button class="button secondary topbar-button" type="button" @click="logout">
            退出登录
          </button>
        </template>
        <template v-else-if="authState.providers.length">
          <button
            v-for="provider in authState.providers"
            :key="provider.registrationId"
            class="button secondary topbar-button"
            type="button"
            @click="login(provider)"
          >
            {{ provider.clientName }} 登录
          </button>
        </template>
      </nav>
    </header>

    <RouterView />
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { RouterLink, RouterView, useRoute, useRouter } from "vue-router";
import {
  authState,
  bootstrapAuth,
  clearAuthSession,
  isPasswordProvider,
  rememberLoginRedirectPath,
  startOAuthLogin
} from "./auth";

const route = useRoute();
const router = useRouter();

onMounted(() => {
  bootstrapAuth().catch(() => {});
});

function login(provider) {
  if (isPasswordProvider(provider)) {
    rememberLoginRedirectPath(route.fullPath);
    router.push("/login");
    return;
  }

  startOAuthLogin(provider, route.fullPath);
}

function logout() {
  clearAuthSession();
}
</script>
