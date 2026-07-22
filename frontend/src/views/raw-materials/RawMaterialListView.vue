<script setup>
import { onMounted, ref } from "vue";
import {
  findAllRawMaterials,
  deleteRawMaterial,
} from "@/services/rawMaterialService";
import { useRouter } from "vue-router";

const router = useRouter();

const rawMaterials = ref([]);
const loading = ref(false);
const errorMessage = ref("");

async function loadRawMaterials() {
  loading.value = true;
  errorMessage.value = "";

  try {
    const response = await findAllRawMaterials();
    rawMaterials.value = response.data;
  } catch (error) {
    errorMessage.value = "Não foi possível carregar as matérias-primas.";
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadRawMaterials();
});

async function removeRawMaterial(rawMaterial) {
  const confirmed = confirm(
    `Deseja realmente excluir a matéria-prima ${rawMaterial.code} - ${rawMaterial.name}?`,
  );

  if (!confirmed) {
    return;
  }

  try {
    await deleteRawMaterial(rawMaterial.id);
    await loadRawMaterials();
  } catch (error) {
    const apiError = error.response?.data;

    if (apiError?.message) {
      errorMessage.value = apiError.message;
    } else {
      errorMessage.value = "Não foi possível excluir a matéria-prima.";
    }
  }
}
</script>

<template>
  <main class="page">
    <header class="page-header">
      <div>
        <h1>Matérias-primas</h1>
        <p>Controle de estoque dos insumos disponíveis para produção.</p>
      </div>

      <button @click="router.push('/raw-materials/new')">
        Nova matéria-prima
      </button>
    </header>

    <p v-if="loading">Carregando...</p>

    <p v-if="errorMessage" class="error">
      {{ errorMessage }}
    </p>

    <table v-if="!loading && rawMaterials.length > 0">
      <thead>
        <tr>
          <th>Código</th>
          <th>Nome</th>
          <th>Estoque</th>
          <th>Unidade</th>
          <th>Ações</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="rawMaterial in rawMaterials" :key="rawMaterial.id">
          <td>{{ rawMaterial.code }}</td>
          <td>{{ rawMaterial.name }}</td>
          <td>{{ rawMaterial.stockQuantity }}</td>
          <td>{{ rawMaterial.unit }}</td>
          <td>
            <button
              @click="router.push(`/raw-materials/${rawMaterial.id}/edit`)"
            >
              Editar
            </button>

            <button class="danger" @click="removeRawMaterial(rawMaterial)">
              Excluir
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <p v-if="!loading && rawMaterials.length === 0">
      Nenhuma matéria-prima cadastrada.
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

.error {
  color: #b00020;
  margin-bottom: 16px;
}

button {
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}

td button {
  margin-right: 8px;
}

.danger {
  background: #dc2626;
  color: white;
  border-color: #dc2626;
}
</style>
