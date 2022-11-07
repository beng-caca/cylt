import axios from '@/libs/api.request'

export const getProductList = (data) => {
  return axios.request({
    url: '/api/product',
    data: data,
    method: 'post'
  })
}

export const login = (loginId) => {
  return axios.request({
    url: '/api/login',
    data: loginId,
    method: 'post'
  })
}

export const sendVerificationCode = (loginId) => {
  return axios.request({
    url: '/api/sendVerificationCode',
    data: loginId,
    method: 'post'
  })
}
