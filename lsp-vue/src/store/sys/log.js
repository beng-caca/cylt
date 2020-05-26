import { getList as getLogList } from '@/api/sys/log'

export default {
  state: {
    list: [],
    info: {},
    query: {
      pageNumber: 1,
      totalNumber: 0,
      singlePage: 20
    }
  },
  mutations: {
    logList (state, page) {
      if (page !== undefined) {
        state.list = page.pageList
        state.query.totalNumber = page.totalNumber
      }
    },
    getLog (state, data) {
      state.info = data
    }
  },
  actions: {
    getSysLogList ({ commit, rootState }, params) {
      getLogList(params).then(res => {
        commit('logList', res.data)
      }).catch(e => {
        console.log(e)
      })
    },
    getSysLog ({ commit, rootState }, params) {
      commit('getLog', params)
    }
  }
}
