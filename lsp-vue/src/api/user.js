import axios from '@/libs/api.request'
import { getToken } from '@/libs/util'

export const login = ({ username, password }) => {
  const data = {
    username,
    password
  }
  return axios.request({
    url: 'login',
    data,
    method: 'post'
  })
}

export const getUserInfo = (token) => {
  return axios.request({
    url: 'sys/user/get',
    params: {
      id: getToken()
    },
    method: 'get'
  })
}

export const logout = () => {
  return axios.request({
    url: '/logout',
    method: 'post'
  })
}

export const getUserList = (data) => {
  return axios.request({
    url: '/sys/user/list',
    data,
    method: 'post'
  })
}

export const getThisUser = () => {
  return axios.request({
    url: 'sys/user/get',
    undefined,
    method: 'get'
  })
}

export const save = (data) => {
  return axios.request({
    url: 'sys/user/save',
    data,
    method: 'post'
  })
}

export const del = (id) => {
  return axios.request({
    url: 'sys/user/delete',
    data: { id: id },
    method: 'post'
  })
}
