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
  },
  mounted () {
    store.dispatch('news').then(res => {
      for (let i in res.data) {
        if (res.data[i].read === false) {
          Push.create(res.data[i].title, {
            link: 'sys/log',
            body: res.data[i].content,
            icon: '/favicon.ico',
            requireInteraction: true,
            data: '3213123',
            onClick: function () {
              // 使网站获取焦点
              window.focus();
              store.dispatch('readNotice', res.data[i])
              //关闭通知
              this.close();
            },
            onClose: function () {
            }
          });
        }
      }
    })
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
