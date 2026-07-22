<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { findAllRawMaterials } from '@/services/rawMaterialService'
import { findAllProducts } from '@/services/productService'

const router = useRouter()

const loading = ref(false)
const rawMaterialsCount = ref(0)
const productsCount = ref(0)
const errorMessage = ref('')

async function loadDashboardData() {
  loading.value = true
  errorMessage.value = ''

  try {
    const [rawMaterialsResponse, productsResponse] = await Promise.all([
      findAllRawMaterials(),
      findAllProducts(),
    ])

    rawMaterialsCount.value = rawMaterialsResponse.data.length
    productsCount.value = productsResponse.data.length
  } catch (error) {
    errorMessage.value = 'Não foi possível carregar os dados do dashboard.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboardData()
})
</script>

<template>
  <main class="page">
    <header class="page-header">
      <div>
        <h1>Industrial Production Optimizer</h1>
        <p>
          Sistema para controle de matérias-primas, cadastro de produtos e cálculo da melhor
          combinação de produção com base no estoque disponível.
        </p>
      </div>
    </header>

    <p v-if="loading">Carregando...</p>

    <p v-if="errorMessage" class="error">
      {{ errorMessage }}
    </p>

    <section class="summary">
      <div class="card">
        <span>Matérias-primas cadastradas</span>
        <strong>{{ rawMaterialsCount }}</strong>
      </div>

      <div class="card">
        <span>Produtos cadastrados</span>
        <strong>{{ productsCount }}</strong>
      </div>

      <div class="card">
        <span>Status do sistema</span>
        <strong>Online</strong>
      </div>
    </section>

    <section class="actions">
      <div class="action-card">
        <h2>Matérias-primas</h2>
        <p>Cadastre e gerencie os insumos disponíveis em estoque.</p>

        <button @click="router.push('/raw-materials')">
          Acessar matérias-primas
        </button>
      </div>

      <div class="action-card">
        <h2>Produtos</h2>
        <p>Cadastre produtos e informe a composição necessária para produção.</p>

        <button @click="router.push('/products')">
          Acessar produtos
        </button>
      </div>

      <div class="action-card">
        <h2>Plano de produção</h2>
        <p>Calcule a melhor combinação de produtos para maximizar o valor total de venda.</p>

        <button @click="router.push('/production-plan')">
          Calcular plano
        </button>
      </div>
    </section>
  </main>
</template>

<style scoped>
.page {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin-bottom: 8px;
}

.page-header p {
  max-width: 900px;
  margin: 0;
  color: #4b5563;
}

.summary {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.card {
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
}

.card span {
  display: block;
  color: #555;
  margin-bottom: 8px;
}

.card strong {
  font-size: 28px;
  color: #111827;
}

.actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 18px;
}

.action-card {
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 24px;
}

.action-card h2 {
  margin-top: 0;
  margin-bottom: 8px;
}

.action-card p {
  color: #4b5563;
  min-height: 48px;
}

button {
  margin-top: 16px;
  padding: 10px 16px;
  border: 1px solid #2563eb;
  border-radius: 4px;
  background: #2563eb;
  color: white;
  cursor: pointer;
}

.error {
  color: #b00020;
  margin-bottom: 16px;
}
</style>