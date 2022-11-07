
import {
  addTreeList
} from '@/libs/util'
import { getProductList, login, sendVerificationCode } from '@/api/neugart/api'

export default {
  state: {
    custUser: {},
    data: [],
    verificationDate: ''
  },
  getters: {
  },
  mutations: {
    customerLogin (state, user) {
      state.custUser = user
    },
    sendVerificationCode (state, date) {
      state.verificationDate = date
    },
    logout (state) {
      state.custUser = {}
    },
    setData (state, list) {
      state.data = []
      list = addTreeList(list, (data) => {
        return data
      })
      for (let i in list) {
        state.data.push(list[i])
      }
    }
  },
  actions: {
    // 请求验证码
    sendVerificationCode ({ commit, rootState }, loginId) {
      return sendVerificationCode(loginId)
    },
    // 登录
    custLogin ({ commit, rootState }, user) {
      return new Promise((resolve, reject) => {
        login(user).then(res => {
          commit('customerLogin', res.data)
          resolve(res)
        }).catch(e => {
          console.log(e)
          reject(err)
        })
      })
    },
    getCustData ({ commit, rootState }, params) {
      return getProductList(params).then(res => {
        commit('setData', res.data)
      }).catch(e => {
        console.log(e)
      })
    }
  }
}
