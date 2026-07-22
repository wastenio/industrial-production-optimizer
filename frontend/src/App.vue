<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="app">
    <aside class="sidebar">
      <h2>Produção</h2>

      <nav>
        <RouterLink to="/raw-materials">
          Matérias-primas
        </RouterLink>

        <RouterLink to="/products">
          Produtos
        </RouterLink>
      </nav>
    </aside>

    <section class="content">
      <RouterView />
    </section>
  </div>
</template>

<style scoped>
.app {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 240px;
  background: #111827;
  color: white;
  padding: 24px;
}

.sidebar h2 {
  margin-top: 0;
  margin-bottom: 24px;
}

nav {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

nav a {
  color: #d1d5db;
  text-decoration: none;
  padding: 10px;
  border-radius: 4px;
}

nav a.router-link-active {
  background: #2563eb;
  color: white;
}

.content {
  flex: 1;
  background: #f4f6f8;
}
</style>