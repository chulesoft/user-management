export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string | null;
  active: boolean;
  deleted: boolean;
  version: number;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
}

export interface UserRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone: string | null;
  active: boolean;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
