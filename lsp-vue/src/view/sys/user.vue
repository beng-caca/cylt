<template>
  <div class="split-pane-page-wrapper">
    <Table
      :loading="$store.state.user.loading"
      context-menu
      show-context-menu
      :columns="columns1"
      :data="$store.state.user.userList"
      @on-contextmenu="handleContextMenu"
      border
      stripe
    >
      <template slot="contextMenu">
        <DropdownItem @click.native="handleContextMenuEdit">编辑</DropdownItem>
        <DropdownItem @click.native="handleContextMenuDelete" style="color: #ed4014">删除</DropdownItem>
      </template>
    </Table>
    <Page
      @on-change="changePage"
      @on-page-size-change="changeSizePage"
      :page-size="$store.state.user.query.singlePage"
      :total="$store.state.user.query.totalNumber"
      :current="$store.state.user.query.pageNumber"
      :page-size-opts="[20,40,60,80,100]"
      style="text-align: right;margin-top: 5px;"
      show-total show-sizer >
    </Page>
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
export default {
  data () {
    store.dispatch('getUserList')
    return {
      columns1: [
        {
          type: 'index',
          width: 60,
          align: 'center'
        },
        {
          title: this.$t('system.user.username'),
          key: 'username'
        }, {
          title: this.$t('system.user.name'),
          key: 'name'
        },
        {
          title: this.$t('system.user.enterpriseId'),
          key: 'enterpriseId'
        }
      ],
      contextLine: 0,
      delConfirm: false
    }
  },
  methods: {
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
    },
    changePage (pageNumber) {
      this.$store.state.user.query.pageNumber = pageNumber
      store.dispatch('getUserList')
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.user.query.pageNumber = 1
      this.$store.state.user.query.singlePage = pageSizeNumber
      store.dispatch('getUserList')
    },
    handleContextMenu (row) {
      const index = this.data1.findIndex(item => item.name === row.name)
      this.contextLine = index + 1
    },
    handleContextMenuEdit () {
      this.$Message.info('Click edit of line' + this.contextLine)
    },
    handleContextMenuDelete () {
      this.$Message.info('Click delete of line' + this.contextLine)
    }
  }
}
</script>
<style lang="less">
  .split-pane-page-wrapper{
    height: 100%;
  }
</style>
