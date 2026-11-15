import { apiClient } from './api';

export const inquiryService = {
  async submitInquiry(propertyId, data) {
    const response = await apiClient.post(`/api/properties/${propertyId}/inquiries`, data);
    return response.data;
  },

  async getMyInquiries(page = 0, size = 10) {
    const response = await apiClient.get('/api/inquiries/my', {
      params: { page, size },
    });
    return response.data;
  },

  async getPropertyInquiries(propertyId) {
    const response = await apiClient.get(`/api/properties/${propertyId}/inquiries`);
    return response.data;
  },

  async updateInquiryStatus(inquiryId, status) {
    const response = await apiClient.patch(`/api/inquiries/${inquiryId}/status`, { status });
    return response.data;
  },

  async deleteInquiry(inquiryId) {
    const response = await apiClient.delete(`/api/inquiries/${inquiryId}`);
    return response.data;
  },
};
