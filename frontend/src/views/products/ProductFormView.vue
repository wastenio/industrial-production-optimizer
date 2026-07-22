<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  createProduct,
  findProductById,
  updateProduct,
} from '@/services/productService'
import { findAllRawMaterials } from '@/services/rawMaterialService'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const errorMessage = ref('')
const rawMaterials = ref([])

const form = reactive({
  code: '',
  name: '',
  salePrice: '',
  compositions: [],
})

const productId = computed(() => route.params.id)
const isEditMode = computed(() => !!productId.value)

async function loadRawMaterials() {
  try {
    const response = await findAllRawMaterials()
    rawMaterials.value = response.data
  } catch (error) {
    errorMessage.value = 'Não foi possível carregar as matérias-primas.'
  }
}

async function loadProduct() {
  if (!isEditMode.value) {
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const response = await findProductById(productId.value)

    form.code = response.data.code
    form.name = response.data.name
    form.salePrice = response.data.salePrice
    form.compositions = response.data.compositions.map((composition) => ({
      rawMaterialId: composition.rawMaterialId,
      requiredQuantity: composition.requiredQuantity,
    }))
  } catch (error) {
    const apiError = error.response?.data

    if (apiError?.message) {
      errorMessage.value = apiError.message
    } else {
      errorMessage.value = 'Não foi possível carregar o produto.'
    }
  } finally {
    loading.value = false
  }
}

function addComposition() {
  form.compositions.push({
    rawMaterialId: '',
    requiredQuantity: '',
  })
}

function removeComposition(index) {
  form.compositions.splice(index, 1)
}

function buildPayload() {
  return {
    code: form.code,
    name: form.name,
    salePrice: Number(form.salePrice),
    compositions: form.compositions.map((composition) => ({
      rawMaterialId: Number(composition.rawMaterialId),
      requiredQuantity: Number(composition.requiredQuantity),
    })),
  }
}

function resolveErrorMessage(error) {
  const apiError = error.response?.data

  if (apiError?.fieldErrors?.length > 0) {
    return apiError.fieldErrors
      .map((fieldError) => `${fieldError.field}: ${fieldError.message}`)
      .join(' | ')
  }

  if (apiError?.message) {
    return apiError.message
  }

  return isEditMode.value
    ? 'Não foi possível atualizar o produto.'
    : 'Não foi possível cadastrar o produto.'
}

async function saveProduct() {
  loading.value = true
  errorMessage.value = ''

  try {
    const payload = buildPayload()

    if (isEditMode.value) {
      await updateProduct(productId.value, payload)
    } else {
      await createProduct(payload)
    }

    router.push('/products')
  } catch (error) {
    errorMessage.value = resolveErrorMessage(error)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadRawMaterials()
  await loadProduct()

  if (!isEditMode.value && form.compositions.length === 0) {
    addComposition()
  }
})
</script>

<template>
  <main class="page">
    <h1>{{ isEditMode ? 'Editar produto' : 'Novo produto' }}</h1>

    <p v-if="errorMessage" class="error">
      {{ errorMessage }}
    </p>

    <form @submit.prevent="saveProduct" class="form">
      <section class="section">
        <h2>Dados do produto</h2>

        <div class="form-group">
          <label for="code">Código</label>
          <input id="code" v-model="form.code" type="text" placeholder="Ex: PRD-001" />
        </div>

        <div class="form-group">
          <label for="name">Nome</label>
          <input id="name" v-model="form.name" type="text" placeholder="Ex: Mesa Industrial" />
        </div>

        <div class="form-group">
          <label for="salePrice">Valor de venda</label>
          <input
            id="salePrice"
            v-model="form.salePrice"
            type="number"
            step="0.01"
            min="0"
            placeholder="Ex: 850.00"
          />
        </div>
      </section>

      <section class="section">
        <div class="section-header">
          <div>
            <h2>Composição</h2>
            <p>Informe as matérias-primas necessárias para produzir 1 unidade deste produto.</p>
          </div>

          <button type="button" @click="addComposition">
            Adicionar item
          </button>
        </div>

        <div
          v-for="(composition, index) in form.compositions"
          :key="index"
          class="composition-item"
        >
          <div class="form-group">
            <label>Matéria-prima</label>
            <select v-model="composition.rawMaterialId">
              <option value="">Selecione</option>

              <option
                v-for="rawMaterial in rawMaterials"
                :key="rawMaterial.id"
                :value="rawMaterial.id"
              >
                {{ rawMaterial.code }} - {{ rawMaterial.name }} ({{ rawMaterial.unit }})
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Quantidade necessária</label>
            <input
              v-model="composition.requiredQuantity"
              type="number"
              step="0.001"
              min="0"
              placeholder="Ex: 15"
            />
          </div>

          <button
            type="button"
            class="danger"
            @click="removeComposition(index)"
          >
            Remover
          </button>
        </div>

        <p v-if="form.compositions.length === 0" class="empty-message">
          Nenhuma matéria-prima adicionada à composição.
        </p>
      </section>

      <div class="actions">
        <button type="button" @click="router.push('/products')">
          Cancelar
        </button>

        <button type="submit" :disabled="loading">
          {{ loading ? 'Salvando...' : 'Salvar' }}
        </button>
      </div>
    </form>
  </main>
</template>

<style scoped>
.page {
  padding: 24px;
  max-width: 900px;
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

.section {
  margin-bottom: 32px;
}

.section h2 {
  margin-top: 0;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.section-header p {
  margin: 0;
  color: #555;
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

input,
select {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.composition-item {
  display: grid;
  grid-template-columns: 1fr 220px auto;
  gap: 12px;
  align-items: end;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  margin-bottom: 12px;
  background: #f9fafb;
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

button[type='submit'],
.section-header button {
  background: #2563eb;
  color: white;
  border-color: #2563eb;
}

.danger {
  background: #dc2626;
  color: white;
  border-color: #dc2626;
}

button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error {
  color: #b00020;
  margin-bottom: 16px;
}

.empty-message {
  color: #666;
}
</style>