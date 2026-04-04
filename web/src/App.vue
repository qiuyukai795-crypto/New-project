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
      </nav>
    </header>

    <RouterView />
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from "vue-router";
</script>
