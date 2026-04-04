import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
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
    }
  ]
});

export default router;
