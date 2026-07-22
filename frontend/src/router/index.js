import { createRouter, createWebHistory } from "vue-router";
import RawMaterialListView from "@/views/raw-materials/RawMaterialListView.vue";
import RawMaterialFormView from "@/views/raw-materials/RawMaterialFormView.vue";
import ProductListView from "@/views/products/ProductListView.vue";
import ProductFormView from "@/views/products/ProductFormView.vue";
import ProductionPlanView from "@/views/production-plans/ProductionPlanView.vue";
import HomeView from "@/views/HomeView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView,
    },
    {
      path: "/raw-materials",
      name: "raw-materials",
      component: RawMaterialListView,
    },
    {
      path: "/raw-materials/new",
      name: "raw-materials-new",
      component: RawMaterialFormView,
    },
    {
      path: "/raw-materials/:id/edit",
      name: "raw-materials-edit",
      component: RawMaterialFormView,
    },
    {
      path: "/products",
      name: "products",
      component: ProductListView,
    },
    {
      path: "/products/new",
      name: "products-new",
      component: ProductFormView,
    },
    {
      path: "/products/:id/edit",
      name: "products-edit",
      component: ProductFormView,
    },
    {
      path: "/production-plan",
      name: "production-plan",
      component: ProductionPlanView,
    },
  ],
});

export default router;
