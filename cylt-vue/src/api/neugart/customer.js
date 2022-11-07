import axios from '@/libs/api.request'

export const getList = (data) => {
  return axios.request({
    url: '/neugart/customer/list',
    data: data,
    method: 'post'
  })
}

export const save = (data) => {
  return axios.request({
    url: '/neugart/customer/save',
    data: data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: '/neugart/customer/delete',
    data: { id: id },
    method: 'post'
  })
}
