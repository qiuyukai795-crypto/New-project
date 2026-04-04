<template>
  <main class="page">
    <section class="hero">
      <span class="eyebrow">Vue 3 Frontend</span>
      <h1>做一个前后端分离版的大众点评首页</h1>
      <p>
        前端使用 Vue 3 + Vite，后端使用 Spring Boot API。
        你可以搜索商户、浏览详情，并通过独立的前端页面提交评价。
      </p>

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
