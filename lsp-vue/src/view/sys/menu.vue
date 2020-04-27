<template>
  <div class="split-pane-page-wrapper">
    <split-pane v-model="offset">
      <Card  slot="left" class="pane"  style="height:100%;display:block;overflow-y:auto;" dis-hover>
        <p slot="title">{{ $t('system.menu.menu') + $t('system.tree') }}</p>
        <Tree :data="$store.state.menu.menuList" :render="renderContent"></Tree>
      </Card>
      <Card slot="right" class="pane" style="height:100%;" dis-hover>
        <p slot="title">{{ $t('system.menu.menu') + $t('system.info') + ' - ' + $t($store.state.menu.info.name) }}</p>
        <Form ref="formValidate" :model="$store.state.menu.info" :rules="ruleValidate" :label-width="120">
          <FormItem  :label-width="120" prop="name">
            <span slot='label'>{{ $t('system.menu.name') }}</span>
            <Input v-model="$store.state.menu.info.name"/>
          </FormItem>
          <FormItem :label-width="120" prop="icon">
            <span slot='label'>{{ $t('system.menu.icon') }}</span>
            <Input v-model="$store.state.menu.info.icon"/>
          </FormItem>
          <FormItem prop="component"  :label-width="120">
            <span slot='label'>{{ $t('system.menu.component') }}</span>
            <Input v-model="$store.state.menu.info.component"/>
          </FormItem>
          <FormItem :label-width="120" prop="showMenu">
            <span slot='label'>{{ $t('system.menu.showMenu') }}</span>
              <i-switch v-model="$store.state.menu.info.showMenu" size="large">
              </i-switch>
          </FormItem>
          <FormItem :label-width="120">
            <Button type="primary" @click="save()">{{ $t('system.save') }}</Button>
          </FormItem>
        </Form>
      </Card>
    </split-pane>
    <Modal v-model="delConfirm" width="360">
      <p slot="header" style="color:#f60;text-align:center">
        <Icon type="ios-information-circle"></Icon>
        <span>{{ $t('system.warning') }}</span>
      </p>
      <div style="text-align:center">
        <p>{{ $t('system.confirm.del') }}</p>
      </div>
      <div slot="footer">
        <Button type="error" size="large" long @click="remove">{{ $t('system.del') }}</Button>
      </div>
    </Modal>
  </div>
</template>
<script>
import store from '@/store'
import SplitPane from '_c/split-pane'
export default {
  data () {
    store.dispatch('getSysMenuList')
    return {
      offset: 0.4,
      delConfirm: false,
      offsetVertical: '10px',
      ruleValidate: {
        name: [
          { required: true, message: this.$t('system.menu.name') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        component: [
          { required: true, message: this.$t('system.menu.component') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ]
      }
    }
  },
  components: {
    SplitPane
  },
  methods: {
    renderContent (h, { root, node, data }) {
      return h('span', {
        style: {
          display: 'inline-block',
          width: '100%'
        }
      }, [
        h('span', [
          h('Icon', {
            props: {
              type: data.icon
            },
            style: {
              marginRight: '8px'
            }
          }),
          h('span', this.$t(data.title))
        ]),
        h('span', {
          style: {
            display: 'inline-block',
            float: 'right',
            marginRight: '32px'
          }
        }, [
          h('Button', {
            props: Object.assign({}, this.buttonProps, {
              icon: 'ios-add'
            }),
            style: {
              marginRight: '8px',
              padding: '2px 8px 4px 11px',
              backgroundColor: '#458B00',
              color: '#fff'
            },
            on: {
              click: () => {
                this.insertInit(data)
              }
            }
          }),
          h('Button', {
            props: Object.assign({}, this.buttonProps, {
              icon: 'md-create'
            }),
            style: {
              marginRight: '8px',
              padding: '2px 8px 4px 11px',
              backgroundColor: '#EEEE00',
              color: '#fff'
            },
            on: {
              click: () => {
                this.updateInit(root, node, data)
              }
            }
          }),
          h('Button', {
            props: Object.assign({}, this.buttonProps, {
              icon: 'ios-close'
            }),
            style: {
              padding: '2px 8px 4px 11px',
              backgroundColor: '#EE3B3B',
              color: '#fff'
            },
            on: {
              click: () => {
                this.delConfirm = true
                this.del = { root, node, data }
              }
            }
          })
        ])
      ])
    },
    remove () {
      let { root, node, data } = this.del
      this.delConfirm = false
      store.dispatch('delSysMenu', data.id).then(res => {
        const parentKey = root.find(el => el === node).parent
        const parent = root.find(el => el.nodeKey === parentKey).node
        const index = parent.children.indexOf(data)
        parent.children.splice(index, 1)
        this.$Message.success(this.$t('system.success'))
      })
    },
    insertInit (data) {
      store.dispatch('insertMenu', data.id)
    },
    updateInit (root, node, data) {
      store.dispatch('getSysMenu', data)
    },
    save () {
      this.$refs['formValidate'].validate((valid) => {
        if (valid) {
          store.dispatch('saveSysMenu').then(res => {
            this.$Message.success(this.$t('system.success'))
          })
        }
      })
    }
  }
}
</script>
<style lang="less">
  .split-pane-page-wrapper{
    height: 100%;
  }
</style>
