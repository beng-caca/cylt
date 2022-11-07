import axios from '@/libs/api.request'

export const getProductList = (data) => {
  return axios.request({
    url: '/neugart/product/noPageList',
    data: data,
    method: 'post'
  })
}

export const saveProduct = (data) => {
  return axios.request({
    url: '/neugart/product/save',
    data: data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: '/neugart/product/delete',
    data: { id: id },
    method: 'post'
  })
}
