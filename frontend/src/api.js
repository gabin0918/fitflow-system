import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api', // Uderzamy w port Gatewaya
});

// Automatyczne dodawanie tokena JWT do każdego zapytania
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Membership API calls
export const membershipAPI = {
    // Pobierz dostępne plany
    getPlans: () => api.get('http://localhost:8083/api/memberships/plans'),

    // Pobierz członkostwa użytkownika
    getUserMemberships: (userId) => api.get(`http://localhost:8083/api/memberships/user/${userId}`),

    // Kup nowe członkostwo
    purchaseMembership: (data) => api.post('http://localhost:8083/api/memberships/purchase', data),

    // Zawieś członkostwo
    suspendMembership: (membershipId) => api.put(`http://localhost:8083/api/memberships/${membershipId}/suspend`),

    // Wznów członkostwo
    resumeMembership: (membershipId) => api.put(`http://localhost:8083/api/memberships/${membershipId}/resume`),

    // Anuluj członkostwo
    cancelMembership: (membershipId) => api.put(`http://localhost:8083/api/memberships/${membershipId}/cancel`),
};

export default api;