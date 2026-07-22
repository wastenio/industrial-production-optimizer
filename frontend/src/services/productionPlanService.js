import api from './api'

export function calculateProductionPlan() {
  return api.post('/production-plans/calculate')
}