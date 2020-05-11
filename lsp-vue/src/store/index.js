import Vue from 'vue'
import Vuex from 'vuex'

import user from './module/user'
import app from './module/app'
import menu from './sys/menu'
import role from './sys/role'

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
    role
  }
})
