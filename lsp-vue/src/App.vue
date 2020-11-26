<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
import store from '@/store'
import Push from 'push.js'
export default {
  name: 'App',
  created () {
    // 注册推送通知
    Push.Permission.request()
    if (!store.getters.Message) {
      // 将全局消息赋值给store
      store.getters.Message = this.$Message
    }

    if (!store.getters.t) {
      // 将国际化赋值给store
      store.getters.t = this.$t
    }
  }
}
</script>

<style lang="less">
.size{
  width: 100%;
  height: 100%;
}
html,body{
  .size;
  overflow: hidden;
  margin: 0;
  padding: 0;
}
#app {
  .size;
}
</style>
