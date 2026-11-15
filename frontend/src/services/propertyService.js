import { apiClient } from './api';

export const propertyService = {
  async getProperties(params = {}) {
    const cleanParams = {};
    Object.keys(params).forEach((key) => {
      const val = params[key];
      if (val !== undefined && val !== null && val !== '') {
        cleanParams[key] = val;
      }
    });

    const response = await apiClient.get('/api/properties', { params: cleanParams });
    return response.data;
  },

  async getPropertyById(id) {
    const response = await apiClient.get(`/api/properties/${id}`);
    return response.data;
  },

  async createProperty(data) {
    const response = await apiClient.post('/api/properties', data);
    return response.data;
  },

  async updateProperty(id, data) {
    const response = await apiClient.put(`/api/properties/${id}`, data);
    return response.data;
  },

  async updatePropertyStatus(id, status) {
    const response = await apiClient.patch(`/api/properties/${id}/status`, { status });
    return response.data;
  },

  async deleteProperty(id) {
    const response = await apiClient.delete(`/api/properties/${id}`);
    return response.data;
  },

  async getMyProperties(page = 0, size = 10) {
    const response = await apiClient.get('/api/properties/my', {
      params: { page, size },
    });
    return response.data;
  },

  async uploadPropertyImage(propertyId, file, isPrimary = false) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('isPrimary', isPrimary);

    const response = await apiClient.post(`/api/properties/${propertyId}/images`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  async deletePropertyImage(propertyId, imageId) {
    const response = await apiClient.delete(`/api/properties/${propertyId}/images/${imageId}`);
    return response.data;
  },
};
