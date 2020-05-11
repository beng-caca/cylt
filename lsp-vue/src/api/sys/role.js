import axios from '@/libs/api.request'

export const getRoleList = () => {
  return axios.request({
    url: '/sys/role/list',
    method: 'get'
  })
}

export const save = (data) => {
  return axios.request({
    url: '/sys/role/save',
    data: data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: '/sys/role/delete',
    data: { id: id },
    method: 'post'
  })
}
