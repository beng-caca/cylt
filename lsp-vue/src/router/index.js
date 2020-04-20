import Vue from 'vue'
import Router from 'vue-router'
import routes from './routers'
import store from '@/store'
import iView from 'iview'
import { setToken, getToken, setTitle } from '@/libs/util'
import config from '@/config'
const { homeName } = config

Vue.use(Router)
const router = new Router({
  routes,
  mode: 'history'
})
const LOGIN_PAGE_NAME = 'login'

const turnTo = (to, access, next) => {
  if (to.name.indexOf('error_') !== -1) { // 跳过权限检测
    next()
    return
  }

  if (access.some(_ => _ === to.name)) {
    next() // 有权限，可访问
  } else {
    next({ replace: true, name: 'error_401' }) // 无权限，重定向到401页面
  }
}

router.beforeEach((to, from, next) => {
  iView.LoadingBar.start()
  if (store.state.from === to.name) {
    return;
  } else {
    store.state.from = to.name
  }
  const token = getToken()
  if (!token && to.name === LOGIN_PAGE_NAME) {
    // 未登陆且要跳转的页面是登录页
    next() // 跳转
  } else if (token && to.name === LOGIN_PAGE_NAME) {
    // 已登录且要跳转的页面是登录页
    next({
      name: homeName // 跳转到homeName页
    })
  } else {
    if (store.state.user.id) {
      let ss = ['home', 'aasd']
      turnTo(to, ss, next)
    } else {
      store.dispatch('getUserInfo').then(user => {
        let ss = ['home', 'aasd']
        // 拉取用户信息，通过用户权限和跳转的页面的name来判断是否有权限访问;access必须是一个数组，如：['super_admin'] ['super_admin', 'admin']
        turnTo(to, ss, next)
      }).catch(() => {
        setToken('')
        next({
          name: 'login'
        })
      })
    }
  }
})

router.afterEach(to => {
  setTitle(to, router.app)
  iView.LoadingBar.finish()
  window.scrollTo(0, 0)
})

export default router
