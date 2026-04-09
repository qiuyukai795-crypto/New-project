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
          <p>现在点评会写入数据库；发点评前，需要先登录。</p>

          <div v-if="authState.user" class="signed-in-card">
            <span class="badge">已登录账号</span>
            <strong>{{ authState.user.displayName }}</strong>
            <p class="meta">{{ authState.user.email || authState.user.username }}</p>
          </div>

          <div v-else-if="authState.providers.length" class="login-panel">
            <p>你还没有登录，先登录再发布这家店的评价。</p>
            <div class="provider-actions">
              <button
                v-for="provider in authState.providers"
                :key="provider.registrationId"
                class="button"
                type="button"
                @click="login(provider)"
              >
                {{ provider.clientName }} 登录
              </button>
            </div>
          </div>

          <div v-else class="login-panel">
            <p>暂时还没有拿到可用的登录方式。</p>
            <button
              class="button secondary"
              type="button"
              :disabled="providerRefreshing"
              @click="reloadProviders"
            >
              {{ providerRefreshing ? "重新获取中..." : "重新获取登录方式" }}
            </button>
            <p class="meta">如果你刚重启后端，点一次这里或者刷新页面。</p>
          </div>

          <form class="review-form" @submit.prevent="submitReview" v-if="authState.user">

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
import { onMounted, reactive, ref, watch } from "vue";
import { RouterLink, useRoute, useRouter } from "vue-router";
import {
  authState,
  isPasswordProvider,
  refreshAuthProviders,
  rememberLoginRedirectPath,
  startOAuthLogin
} from "../auth";
import { createReview, fetchRecommendations, fetchReviews, fetchShop } from "../api";

const props = defineProps({
  id: {
    type: [String, Number],
    required: true
  }
});
const route = useRoute();
const router = useRouter();

const shop = ref(null);
const reviews = ref([]);
const recommendations = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const submitError = ref("");
const submitSuccess = ref("");
const submitting = ref(false);
const providerRefreshing = ref(false);

const form = reactive({
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

  if (!authState.user) {
    submitError.value = "请先登录后再发布点评。";
    return;
  }

  if (!form.content || !form.score) {
    submitError.value = "请填写完整的评价信息。";
    return;
  }

  submitting.value = true;

  try {
    await createReview(props.id, form);
    reviews.value = await fetchReviews(props.id);
    submitSuccess.value = "评价发布成功，已经展示在列表顶部。";
    form.score = 0;
    form.content = "";
  } catch (error) {
    submitError.value = error.message || "评价提交失败，请稍后重试。";
  } finally {
    submitting.value = false;
  }
}

function login(provider) {
  if (isPasswordProvider(provider)) {
    rememberLoginRedirectPath(route.fullPath);
    router.push("/login");
    return;
  }

  startOAuthLogin(provider, route.fullPath);
}

async function reloadProviders() {
  providerRefreshing.value = true;
  try {
    await refreshAuthProviders();
  } finally {
    providerRefreshing.value = false;
  }
}

onMounted(() => {
  if (!authState.user && !authState.providers.length) {
    reloadProviders();
  }
});

watch(
  () => props.id,
  (shopId) => {
    loadDetail(shopId);
  },
  { immediate: true }
);
</script>
