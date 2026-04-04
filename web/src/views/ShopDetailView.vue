<template>
  <main class="page">
    <RouterLink class="button secondary back-button" to="/">返回首页</RouterLink>

    <section v-if="loading" class="empty">正在加载店铺详情...</section>
    <section v-else-if="errorMessage" class="alert error">{{ errorMessage }}</section>

    <template v-else-if="shop">
      <section class="detail-grid">
        <article class="panel highlight">
          <span class="eyebrow">{{ shop.category }}</span>
          <div class="title-row">
            <div>
              <h1>{{ shop.name }}</h1>
              <p>{{ shop.description }}</p>
            </div>
            <div class="badge-row">
              <span class="badge">评分 {{ shop.rating }}</span>
              <span class="badge">人均 ￥{{ shop.avgPrice }}</span>
            </div>
          </div>

          <div class="inner-panel">
            <p><strong>所在区域：</strong><span>{{ shop.district }}</span></p>
            <p><strong>详细地址：</strong><span>{{ shop.address }}</span></p>
            <p><strong>招牌菜：</strong><span>{{ shop.signatureDish }}</span></p>
            <div class="tags">
              <span v-for="tag in shop.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </div>
        </article>

        <aside class="panel">
          <h2>同城推荐</h2>
          <p>看完这家，也可以顺手看看这些高分商户。</p>
          <div class="recommend-list">
            <RouterLink
              v-for="item in recommendations"
              :key="item.id"
              class="recommend-card"
              :to="`/shops/${item.id}`"
            >
              <div class="badge-row">
                <span class="badge">{{ item.category }}</span>
                <span class="badge">{{ item.district }}</span>
              </div>
              <h3>{{ item.name }}</h3>
              <p class="meta">{{ item.description }}</p>
            </RouterLink>
          </div>
        </aside>
      </section>

      <section class="detail-grid" id="reviews">
        <article class="panel">
          <div class="section-head compact">
            <div>
              <h2>用户评价</h2>
              <p>当前共有 {{ reviews.length }} 条点评</p>
            </div>
          </div>

          <div v-if="submitSuccess" class="alert success">{{ submitSuccess }}</div>
          <div v-if="submitError" class="alert error">{{ submitError }}</div>

          <div v-if="reviews.length" class="review-list">
            <article v-for="review in reviews" :key="review.id" class="review-card">
              <div class="review-top">
                <strong>{{ review.nickname }}</strong>
                <span class="badge">评分 {{ review.score }}</span>
              </div>
              <p>{{ review.content }}</p>
              <p class="meta">{{ review.createdAt }}</p>
            </article>
          </div>

          <div v-else class="empty">
            这家店还没有用户评价，来写第一条吧。
          </div>
        </article>

        <aside class="panel">
          <h2>写点评</h2>
          <p>提交后会写入 Spring Boot 的内存数据，适合做 demo 联调。</p>

          <form class="review-form" @submit.prevent="submitReview">
            <input v-model.trim="form.nickname" type="text" placeholder="你的昵称" required />

            <select v-model.number="form.score" required>
              <option :value="0">请选择评分</option>
              <option :value="5">5 分</option>
              <option :value="4">4 分</option>
              <option :value="3">3 分</option>
              <option :value="2">2 分</option>
              <option :value="1">1 分</option>
            </select>

            <textarea
              v-model.trim="form.content"
              placeholder="写点真实感受，比如服务、环境、菜品口味..."
              required
            />

            <button class="button" type="submit" :disabled="submitting">
              {{ submitting ? "发布中..." : "发布点评" }}
            </button>
          </form>
        </aside>
      </section>
    </template>
  </main>
</template>

<script setup>
import { reactive, ref, watch } from "vue";
import { RouterLink } from "vue-router";
import { createReview, fetchRecommendations, fetchReviews, fetchShop } from "../api";

const props = defineProps({
  id: {
    type: [String, Number],
    required: true
  }
});

const shop = ref(null);
const reviews = ref([]);
const recommendations = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const submitError = ref("");
const submitSuccess = ref("");
const submitting = ref(false);

const form = reactive({
  nickname: "",
  score: 0,
  content: ""
});

async function loadDetail(shopId) {
  loading.value = true;
  errorMessage.value = "";
  submitError.value = "";
  submitSuccess.value = "";

  try {
    const [shopData, reviewData, recommendationData] = await Promise.all([
      fetchShop(shopId),
      fetchReviews(shopId),
      fetchRecommendations(shopId)
    ]);

    shop.value = shopData;
    reviews.value = reviewData;
    recommendations.value = recommendationData;
  } catch (error) {
    errorMessage.value = error.message || "加载店铺详情失败，请稍后再试。";
  } finally {
    loading.value = false;
  }
}

async function submitReview() {
  submitError.value = "";
  submitSuccess.value = "";

  if (!form.nickname || !form.content || !form.score) {
    submitError.value = "请填写完整的评价信息。";
    return;
  }

  submitting.value = true;

  try {
    await createReview(props.id, form);
    reviews.value = await fetchReviews(props.id);
    submitSuccess.value = "评价发布成功，已经展示在列表顶部。";
    form.nickname = "";
    form.score = 0;
    form.content = "";
  } catch (error) {
    submitError.value = error.message || "评价提交失败，请稍后重试。";
  } finally {
    submitting.value = false;
  }
}

watch(
  () => props.id,
  (shopId) => {
    loadDetail(shopId);
  },
  { immediate: true }
);
</script>
