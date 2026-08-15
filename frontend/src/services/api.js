import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
})

export const getRutinas = () => api.get('/rutinas')
export const getRutinaById = (id) => api.get(`/rutinas/${id}`)
export const crearRutina = (rutinaData) => api.post('/rutinas', rutinaData)
export const actualizarRutina = (id, rutinaData) => api.put(`/rutinas/${id}`, rutinaData)
export const eliminarRutina = (id) => api.delete(`/rutinas/${id}`)

export const getSolicitudes = () => api.get('/solicitudes')
export const crearSolicitud = (solicitudData) => api.post('/solicitudes', solicitudData)
export const responderSolicitud = (id, estado) => api.put(`/solicitudes/${id}/responder`, { estado })

export default api