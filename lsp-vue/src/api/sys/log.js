import axios from '@/libs/api.request'

export const getList = (data) => {
  return axios.request({
    url: '/sys/log/list',
    data: data,
    method: 'post'
  })
}
