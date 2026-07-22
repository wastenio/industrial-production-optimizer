<script setup>
import { ref } from 'vue'
import { calculateProductionPlan } from '@/services/productionPlanService'

const loading = ref(false)
const errorMessage = ref('')
const productionPlan = ref(null)

async function calculatePlan() {
  loading.value = true
  errorMessage.value = ''
  productionPlan.value = null

  try {
    const response = await calculateProductionPlan()
    productionPlan.value = response.data
  } catch (error) {
    const apiError = error.response?.data

    if (apiError?.message) {
      errorMessage.value = apiError.message
    } else {
      errorMessage.value = 'Não foi possível calcular o plano de produção.'
    }
  } finally {
    loading.value = false
  }
}

function formatCurrency(value) {
  return Number(value).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  })
}

function formatQuantity(value) {
  return Number(value).toLocaleString('pt-BR', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 3,
  })
}
</script>

<template>
  <main class="page">
    <header class="page-header">
      <div>
        <h1>Plano de produção</h1>
        <p>
          Calcule a melhor combinação de produtos para maximizar o valor total de venda,
          respeitando o estoque disponível de matérias-primas.
        </p>
      </div>

      <button @click="calculatePlan" :disabled="loading">
        {{ loading ? 'Calculando...' : 'Calcular plano' }}
      </button>
    </header>

    <p v-if="errorMessage" class="error">
      {{ errorMessage }}
    </p>

    <section v-if="productionPlan" class="summary">
      <div class="card">
        <span>Total de unidades</span>
        <strong>{{ productionPlan.totalProductionUnits }}</strong>
      </div>

      <div class="card">
        <span>Valor total de venda</span>
        <strong>{{ formatCurrency(productionPlan.totalSaleValue) }}</strong>
      </div>

      <div class="card">
        <span>Produtos no plano</span>
        <strong>{{ productionPlan.items.length }}</strong>
      </div>
    </section>

    <section v-if="productionPlan" class="section">
      <h2>Produtos recomendados</h2>

      <table v-if="productionPlan.items.length > 0">
        <thead>
          <tr>
            <th>Código</th>
            <th>Produto</th>
            <th>Quantidade</th>
            <th>Valor unitário</th>
            <th>Valor total</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="item in productionPlan.items" :key="item.productId">
            <td>{{ item.productCode }}</td>
            <td>{{ item.productName }}</td>
            <td>{{ item.quantity }}</td>
            <td>{{ formatCurrency(item.unitSalePrice) }}</td>
            <td>{{ formatCurrency(item.totalSaleValue) }}</td>
          </tr>
        </tbody>
      </table>

      <p v-else>
        Nenhum produto pode ser produzido com o estoque atual.
      </p>
    </section>

    <section v-if="productionPlan" class="section">
      <h2>Sobras de matérias-primas</h2>

      <table v-if="productionPlan.remainingRawMaterials.length > 0">
        <thead>
          <tr>
            <th>Código</th>
            <th>Matéria-prima</th>
            <th>Estoque inicial</th>
            <th>Quantidade usada</th>
            <th>Quantidade restante</th>
            <th>Unidade</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="rawMaterial in productionPlan.remainingRawMaterials"
            :key="rawMaterial.rawMaterialId"
          >
            <td>{{ rawMaterial.rawMaterialCode }}</td>
            <td>{{ rawMaterial.rawMaterialName }}</td>
            <td>{{ formatQuantity(rawMaterial.initialQuantity) }}</td>
            <td>{{ formatQuantity(rawMaterial.usedQuantity) }}</td>
            <td>{{ formatQuantity(rawMaterial.remainingQuantity) }}</td>
            <td>{{ rawMaterial.unit }}</td>
          </tr>
        </tbody>
      </table>
    </section>

    <p v-if="!productionPlan && !loading" class="empty-message">
      Clique em <strong>Calcular plano</strong> para gerar a melhor combinação de produção.
    </p>
  </main>
</template>

<style scoped>
.page {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
}

.page-header h1 {
  margin-bottom: 4px;
}

.page-header p {
  margin: 0;
  max-width: 780px;
}

.page-header button {
  background: #2563eb;
  color: white;
  border-color: #2563eb;
  min-width: 160px;
  height: 48px;
}

button {
  padding: 10px 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
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

.section {
  margin-bottom: 32px;
}

.section h2 {
  margin-bottom: 16px;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

th,
td {
  padding: 12px;
  border: 1px solid #ddd;
  text-align: left;
}

th {
  background: #f5f5f5;
}

.error {
  color: #b00020;
  margin-bottom: 16px;
}

.empty-message {
  background: white;
  padding: 24px;
  border: 1px solid #ddd;
  border-radius: 8px;
}
</style>