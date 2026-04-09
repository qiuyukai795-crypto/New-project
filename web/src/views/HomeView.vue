<template>
  <main class="page">
    <section class="hero">
      <span class="eyebrow">Vue 3 Frontend</span>
      <h1>做一个前后端分离版的大众点评首页</h1>
      <p>
        前端使用 Vue 3 + Vite，后端使用 Spring Boot API。
        你可以搜索商户、浏览详情；发点评前，需要先完成登录。
      </p>

      <div class="auth-banner" v-if="authState.user">
        <span class="badge">已登录</span>
        <strong>{{ authState.user.displayName }}</strong>
        <span class="muted">现在可以在店铺详情页直接发布点评了。</span>
      </div>

      <div class="auth-banner" v-else-if="authState.providers.length">
        <span class="badge">需要登录</span>
        <span class="muted">浏览保持公开，发点评前请先登录。</span>
      </div>

      <div class="stats">
        <article class="stat-card">
          <strong>{{ shops.length }}</strong>
          <span>当前商户数</span>
        </article>
        <article class="stat-card">
          <strong>{{ categoryCount }}</strong>
          <span>餐饮分类</span>
        </article>
        <article class="stat-card">
          <strong>5173 / 8080</strong>
          <span>前后端开发端口</span>
        </article>
      </div>

      <form class="search-bar" @submit.prevent="submitSearch">
        <input
          v-model.trim="keywordInput"
          type="text"
          placeholder="搜索商户、分类、区域，比如：火锅 / 徐汇区"
        />
        <button class="button" type="submit">开始搜索</button>
        <button class="button secondary" type="button" @click="clearSearch">清空条件</button>
      </form>
    </section>

    <div class="section-head">
      <div>
        <h2>热门商户</h2>
        <p>{{ route.query.keyword ? `当前关键词：${route.query.keyword}` : "精选推荐给你的商户列表" }}</p>
      </div>
      <p class="muted">接口来源：`/api/shops`</p>
    </div>

    <section v-if="errorMessage" class="alert error">{{ errorMessage }}</section>
    <section v-else-if="loading" class="empty">正在加载商户列表...</section>

    <section v-else-if="shops.length" class="grid">
      <ShopCard v-for="shop in shops" :key="shop.id" :shop="shop" />
    </section>

    <section v-else class="empty">
      没有找到符合条件的商户，试试搜索 “川菜” 或 “徐汇区”。
    </section>
  </main>
</template>

<script setup>
import { computed, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { authState } from "../auth";
import ShopCard from "../components/ShopCard.vue";
import { fetchShops } from "../api";

const route = useRoute();
const router = useRouter();

const shops = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const keywordInput = ref(typeof route.query.keyword === "string" ? route.query.keyword : "");

const categoryCount = computed(() => new Set(shops.value.map((shop) => shop.category)).size);

async function loadShops(keyword = "") {
  loading.value = true;
  errorMessage.value = "";

  try {
    shops.value = await fetchShops(keyword);
  } catch (error) {
    errorMessage.value = error.message || "加载商户失败，请稍后重试。";
  } finally {
    loading.value = false;
  }
}

function submitSearch() {
  router.push({
    path: "/",
    query: keywordInput.value ? { keyword: keywordInput.value } : {}
  });
}

function clearSearch() {
  keywordInput.value = "";
  router.push({ path: "/" });
}

watch(
  () => route.query.keyword,
  (keyword) => {
    const normalized = typeof keyword === "string" ? keyword : "";
    keywordInput.value = normalized;
    loadShops(normalized);
  },
  { immediate: true }
);
</script>
