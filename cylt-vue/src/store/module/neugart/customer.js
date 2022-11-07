import {
  getList,
  save,
  del
} from '@/api/neugart/customer'

export default {
  state: {
    list: [],
    loading: false,
    query: {
      pageNumber: 1,
      totalNumber: 0,
      singlePage: 20
    },
    info: {

    }
  },
  mutations: {
    setCustomerList (state, page) {
      state.list = page.pageList
      state.query.totalNumber = page.totalNumber
    },
    setLoading (state, isLoading) {
      state.loading = isLoading
    },
    initInfo (state, data) {
      state.info = data
    }
  },
  actions: {
    // 读取客户列表
    getCustomerList ({ state, commit }) {
      commit('setLoading', true)
      return new Promise((resolve, reject) => {
        getList(state.query).then((obj) => {
          commit('setCustomerList', obj.data)
          commit('setLoading', false)
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },
    // 保存客户
    saveCustomer ({ state }) {
      return save(state.info)
    },
    // 保存客户
    delCustomer ({ state }, id) {
      return del(id)
    },
    // 编辑客户
    getCustomerInfo ({ state, commit }, info) {
      if (info) {
        return commit('initInfo', info)
      }
    }
  }
}
