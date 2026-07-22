<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { createRawMaterial } from "@/services/rawMaterialService";

const router = useRouter();

const loading = ref(false);
const errorMessage = ref("");

const form = reactive({
  code: "",
  name: "",
  stockQuantity: "",
  unit: "",
});

async function saveRawMaterial() {
  loading.value = true;
  errorMessage.value = "";

  try {
    await createRawMaterial({
      code: form.code,
      name: form.name,
      stockQuantity: Number(form.stockQuantity),
      unit: form.unit,
    });

    router.push("/raw-materials");
  } catch (error) {
    console.error(error);

    const apiError = error.response?.data;

    if (apiError?.fieldErrors?.length > 0) {
      errorMessage.value = apiError.fieldErrors
        .map((fieldError) => `${fieldError.field}: ${fieldError.message}`)
        .join(" | ");
    } else if (apiError?.message) {
      errorMessage.value = apiError.message;
    } else {
      errorMessage.value = "Não foi possível cadastrar a matéria-prima.";
    }
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <main class="page">
    <h1>Nova matéria-prima</h1>

    <p v-if="errorMessage" class="error">
      {{ errorMessage }}
    </p>

    <form @submit.prevent="saveRawMaterial" class="form">
      <div class="form-group">
        <label for="code">Código</label>
        <input
          id="code"
          v-model="form.code"
          type="text"
          placeholder="Ex: MP-001"
        />
      </div>

      <div class="form-group">
        <label for="name">Nome</label>
        <input
          id="name"
          v-model="form.name"
          type="text"
          placeholder="Ex: Aço"
        />
      </div>

      <div class="form-group">
        <label for="stockQuantity">Quantidade em estoque</label>
        <input
          id="stockQuantity"
          v-model="form.stockQuantity"
          type="number"
          step="0.001"
          min="0"
          placeholder="Ex: 500"
        />
      </div>

      <div class="form-group">
        <label for="unit">Unidade</label>
        <input
          id="unit"
          v-model="form.unit"
          type="text"
          placeholder="Ex: KG, UN, L"
        />
      </div>

      <div class="actions">
        <button type="button" @click="router.push('/raw-materials')">
          Cancelar
        </button>

        <button type="submit" :disabled="loading">
          {{ loading ? "Salvando..." : "Salvar" }}
        </button>
      </div>
    </form>
  </main>
</template>

<style scoped>
.page {
  padding: 24px;
  max-width: 720px;
}

h1 {
  margin-bottom: 24px;
}

.form {
  background: white;
  padding: 24px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
}

label {
  font-weight: 600;
  margin-bottom: 6px;
}

input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

button {
  padding: 10px 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}

button[type="submit"] {
  background: #2563eb;
  color: white;
  border-color: #2563eb;
}

button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error {
  color: #b00020;
  margin-bottom: 16px;
}
</style>
