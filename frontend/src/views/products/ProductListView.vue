<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { findAllProducts, deleteProduct } from "@/services/productService";

const router = useRouter();

const products = ref([]);
const loading = ref(false);
const errorMessage = ref("");

async function loadProducts() {
  loading.value = true;
  errorMessage.value = "";

  try {
    const response = await findAllProducts();
    products.value = response.data;
  } catch (error) {
    const apiError = error.response?.data;

    if (apiError?.message) {
      errorMessage.value = apiError.message;
    } else {
      errorMessage.value = "Não foi possível carregar os produtos.";
    }
  } finally {
    loading.value = false;
  }
}

async function removeProduct(product) {
  const confirmed = confirm(
    `Deseja realmente excluir o produto ${product.code} - ${product.name}?`,
  );

  if (!confirmed) {
    return;
  }

  try {
    await deleteProduct(product.id);
    await loadProducts();
  } catch (error) {
    const apiError = error.response?.data;

    if (apiError?.message) {
      errorMessage.value = apiError.message;
    } else {
      errorMessage.value = "Não foi possível excluir o produto.";
    }
  }
}

function formatCurrency(value) {
  return Number(value).toLocaleString("pt-BR", {
    style: "currency",
    currency: "BRL",
  });
}

onMounted(() => {
  loadProducts();
});
</script>

<template>
  <main class="page">
    <header class="page-header">
      <div>
        <h1>Produtos</h1>
        <p>Cadastro de produtos e suas composições de matéria-prima.</p>
      </div>

      <button @click="router.push('/products/new')">Novo produto</button>
    </header>

    <p v-if="loading">Carregando...</p>

    <p v-if="errorMessage" class="error">
      {{ errorMessage }}
    </p>

    <table v-if="!loading && products.length > 0">
      <thead>
        <tr>
          <th>Código</th>
          <th>Nome</th>
          <th>Valor de venda</th>
          <th>Composição</th>
          <th>Ações</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="product in products" :key="product.id">
          <td>{{ product.code }}</td>
          <td>{{ product.name }}</td>
          <td>{{ formatCurrency(product.salePrice) }}</td>
          <td>{{ product.compositions?.length || 0 }} item(ns)</td>
          <td>
            <button @click="router.push(`/products/${product.id}/edit`)">
              Editar
            </button>

            <button class="danger" @click="removeProduct(product)">
              Excluir
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <p v-if="!loading && products.length === 0">Nenhum produto cadastrado.</p>
  </main>
</template>

<style scoped>
.page {
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
}

h1 {
  margin-bottom: 4px;
}

p {
  margin: 0;
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

button {
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 8px;
}

.page-header button {
  background: #2563eb;
  color: white;
  border-color: #2563eb;
}

.danger {
  background: #dc2626;
  color: white;
  border-color: #dc2626;
}

.error {
  color: #b00020;
  margin-bottom: 16px;
}
</style>
