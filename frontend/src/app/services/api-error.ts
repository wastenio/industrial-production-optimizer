import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '../models';

export function apiErrorMessage(error: unknown, fallback: string): string {
  const payload = error instanceof HttpErrorResponse ? (error.error as ApiError | null) : null;
  if (payload?.fieldErrors?.length) {
    return payload.fieldErrors.map(({ field, message }) => `${field}: ${message}`).join(' | ');
  }
  return payload?.message || fallback;
}
