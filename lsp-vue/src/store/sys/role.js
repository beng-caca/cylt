import { getRoleList, save as saveRole, del as delRole } from '@/api/sys/role'

export default {
  state: {
    roleList: [],
    info: {},
    query: {
      pageNumber: 1,
      totalNumber: 0,
      singlePage: 20
    }
  },
  mutations: {
    roleList (state, list) {
      state.roleList = list
    },
    getRole (state, data) {
      state.info = data
    },
    insertMenu (state) {
      state.info = []
      state.info.showMenu = true
    }
  },
  actions: {
    getRoleList ({ commit, rootState }, params) {
      getRoleList(params).then(res => {
        commit('roleList', res.data)
      }).catch(e => {
        console.log(e)
      })
    },
    getSysRole ({ commit, rootState }, params) {
      commit('getRole', params)
    },
    saveSysRole ({ commit, rootState }) {
      return saveRole(rootState.role.info)
    },
    insertRole ({ commit, rootState }) {
      commit('insertMenu')
    },
    delSysRole ({ commit, rootState }, id) {
      return delRole(id)
    }
  }
}
