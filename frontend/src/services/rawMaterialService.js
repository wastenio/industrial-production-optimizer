import api from './api'

export function findAllRawMaterials() {
  return api.get('/raw-materials')
}

export function findRawMaterialById(id) {
  return api.get(`/raw-materials/${id}`)
}

export function createRawMaterial(data) {
  return api.post('/raw-materials', data)
}

export function updateRawMaterial(id, data) {
  return api.put(`/raw-materials/${id}`, data)
}

export function deleteRawMaterial(id) {
  return api.delete(`/raw-materials/${id}`)
}