<template>
  <RouterLink class="shop-card" :to="`/shops/${shop.id}`">
    <div class="badge-row">
      <span class="badge">{{ shop.category }}</span>
      <span class="badge">{{ shop.district }}</span>
    </div>

    <h3>{{ shop.name }}</h3>
    <p class="meta">{{ shop.description }}</p>

    <div class="price-row">
      <span>评分 {{ shop.rating }}</span>
      <span>人均 ￥{{ shop.avgPrice }}</span>
    </div>

    <p class="meta">招牌菜：{{ shop.signatureDish }}</p>

    <div class="tags">
      <span v-for="tag in shop.tags" :key="tag" class="tag">{{ tag }}</span>
    </div>
  </RouterLink>
</template>

<script setup>
import { RouterLink } from "vue-router";

defineProps({
  shop: {
    type: Object,
    required: true
  }
});
</script>
