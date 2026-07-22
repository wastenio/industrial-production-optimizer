import { createRouter, createWebHistory } from 'vue-router'
import RawMaterialListView from '@/views/raw-materials/RawMaterialListView.vue'
import RawMaterialFormView from '@/views/raw-materials/RawMaterialFormView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/raw-materials',
    },
    {
      path: '/raw-materials',
      name: 'raw-materials',
      component: RawMaterialListView,
    },
    {
      path: '/raw-materials/new',
      name: 'raw-materials-new',
      component: RawMaterialFormView,
    },
  ],
})

export default router