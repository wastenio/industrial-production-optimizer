export interface RawMaterial {
  id: number;
  code: string;
  name: string;
  stockQuantity: number;
  unit: string;
}

export interface RawMaterialPayload extends Omit<RawMaterial, 'id'> {}

export interface ProductComposition {
  rawMaterialId: number;
  rawMaterialCode?: string;
  rawMaterialName?: string;
  requiredQuantity: number;
}

export interface Product {
  id: number;
  code: string;
  name: string;
  salePrice: number;
  compositions: ProductComposition[];
}

export interface ProductPayload extends Omit<Product, 'id'> {}

export interface ProductionPlanItem {
  productId: number;
  productCode: string;
  productName: string;
  quantity: number;
  unitSalePrice: number;
  totalSaleValue: number;
}

export interface RemainingRawMaterial {
  rawMaterialId: number;
  rawMaterialCode: string;
  rawMaterialName: string;
  initialQuantity: number;
  usedQuantity: number;
  remainingQuantity: number;
  unit: string;
}

export interface ProductionPlan {
  items: ProductionPlanItem[];
  totalProductionUnits: number;
  totalSaleValue: number;
  remainingRawMaterials: RemainingRawMaterial[];
}

export interface ApiError {
  message?: string;
  fieldErrors?: Array<{ field: string; message: string }>;
}
