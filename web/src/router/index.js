import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import LoginCallbackView from "../views/LoginCallbackView.vue";
import ShopDetailView from "../views/ShopDetailView.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView
    },
    {
      path: "/shops/:id",
      name: "shop-detail",
      component: ShopDetailView,
      props: true
    },
    {
      path: "/auth/callback",
      name: "auth-callback",
      component: LoginCallbackView
    }
  ]
});

export default router;
