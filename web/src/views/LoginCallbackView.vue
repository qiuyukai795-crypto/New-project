<template>
  <main class="page">
    <section class="panel auth-callback-card">
      <span class="eyebrow">OAuth2 Login</span>
      <h1>{{ errorMessage ? "登录没有完成" : "正在完成登录" }}</h1>
      <p v-if="errorMessage" class="alert error">{{ errorMessage }}</p>
      <p v-else>{{ statusMessage }}</p>
      <RouterLink class="button secondary" to="/">回到首页</RouterLink>
    </section>
  </main>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { RouterLink, useRouter } from "vue-router";
import {
  clearAuthSession,
  consumeLoginRedirectPath,
  loadCurrentUser,
  setAccessToken
} from "../auth";

const router = useRouter();
const statusMessage = ref("OAuth2 已授权，正在同步登录状态...");
const errorMessage = ref("");

onMounted(async () => {
  const hashParams = new URLSearchParams(window.location.hash.replace(/^#/, ""));
  const token = hashParams.get("token");
  const error = hashParams.get("error");

  window.history.replaceState({}, document.title, window.location.pathname);

  if (error || !token) {
    clearAuthSession();
    errorMessage.value = "OAuth2 登录失败，请返回首页后重试。";
    return;
  }

  try {
    setAccessToken(token);
    await loadCurrentUser();
    statusMessage.value = "登录成功，正在返回你刚才的页面...";
    await router.replace(consumeLoginRedirectPath());
  } catch (loadError) {
    clearAuthSession();
    errorMessage.value = loadError.message || "登录状态恢复失败，请重新登录。";
  }
});
</script>
