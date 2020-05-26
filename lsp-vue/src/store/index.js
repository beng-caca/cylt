import Vue from 'vue'
import Vuex from 'vuex'
import createVuexAlong from 'vuex-along'

import user from './module/user'
import app from './module/app'
import menu from './sys/menu'
import role from './sys/role'
import log from './sys/log'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    //
  },
  mutations: {
    //
  },
  actions: {
    //
  },
  modules: {
    user,
    app,
    menu,
    role,
    log
  },
  plugins: [
    createVuexAlong({
      name: 'cylt' // 设置保存的集合名字，避免同站点下的多项目数据冲突
    })
  ]
})
