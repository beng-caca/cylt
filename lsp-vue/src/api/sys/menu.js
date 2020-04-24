import axios from '@/libs/api.request'

export const getMenuList = () => {
  return axios.request({
    url: '/sys/menu/list',
    method: 'get'
  })
}

export const saveMenu = (data) => {
  return axios.request({
    url: '/sys/menu/save',
    data: data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: '/sys/menu/delete',
    data: { id: id },
    method: 'post'
  })
}
