<template>
  <div class="split-pane-page-wrapper">
    <Form inline  style="padding: 10px;margin-bottom: 20px;border: 2px solid #e8eaec;">
      <FormItem prop="name">
        <Input type="text" v-model="$store.state.role.query.roleName" :placeholder="$t('system.user.name')">
        </Input>
      </FormItem>
      <FormItem>
        <Button type="primary" @click="query()">{{ $t('system.query') }}</Button>
      </FormItem>

      <Divider dashed class="divider"/>
      <div class="operation">
        <Button type="primary" @click="add()">{{ $t('system.add') }}</Button>
      </div>
    </Form>
    <Table
      :loading="$store.state.role.loading"
      context-menu
      show-context-menu
      :columns="columns1"
      :data="$store.state.role.roleList"
      border
      stripe
    >
      <template slot-scope="{ row, index }" slot="action">
        <Button type="primary" size="small" style="margin-right: 5px" @click="info(row, index)">{{ $t('system.info') }}</Button>
        <Button type="error" size="small" @click="delInit(row)">{{ $t('system.del') }}</Button>
      </template>
    </Table>
    <Page
      @on-change="changePage"
      @on-page-size-change="changeSizePage"
      :page-size="$store.state.role.query.singlePage"
      :total="$store.state.role.query.totalNumber"
      :current="$store.state.role.query.pageNumber"
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
        <Button type="error" size="large" long @click="del()">{{ $t('system.del') }}</Button>
      </div>
    </Modal>
    <Drawer
      :title="$t('menu.sys.role') + $t('system.info') + ' - ' + $t($store.state.role.info.roleName)"
      v-model="isInfo"
      width="30%"
    >
      <Form ref="formValidate" :model="$store.state.role.info" :rules="validate">
        <Row :gutter="32">
          <Col span="24">
            <FormItem :label="$t('system.role.roleName')"  prop="roleName">
              <Input v-model="$store.state.role.info.roleName" :placeholder="$t('system.pleaseEnter') + $t('system.role.roleName')" />
            </FormItem>
          </Col>
        </Row>
      </Form>
      <div class="demo-drawer-footer">
        <Button size="large" long type="primary" @click="save()">{{ $t('system.save') }}</Button>
      </div>
    </Drawer>
  </div>
</template>
<script>
import store from '@/store'
export default {
  data () {
    store.dispatch('getRoleList')
    return {
      columns1: [
        { type: 'index', width: 60, align: 'center' },
        { title: this.$t('system.role.roleName'), key: 'roleName',
          render: (h, params) => {
            return h('div', [
              h('Icon', {
                props: {
                  type: 'person'
                }
              }),
              h('strong', this.$t(params.row.roleName))
            ]);
          }},
        { title: this.$t('system.operation'), slot: 'action', width: 150, align: 'center' }
      ],
      contextLine: 0,
      delConfirm: false,
      isInfo: false,
      validate: {
        roleName: [
          { required: true, message: this.$t('system.role.roleName') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    del () {
      let data = this.deldata
      this.delConfirm = false
      store.dispatch('delSysRole', data.id).then(res => {
        if (res.data.code === 200) {
          this.query()
          this.$Message.success(this.$t('system.success'))
        } else {
          this.$Message.error(this.$t('system.fail'))
        }
      })
    },
    delInit (data) {
      this.deldata = data
      this.delConfirm = true
    },
    save () {
      this.$refs['formValidate'].validate((valid) => {
        if (valid) {
          store.dispatch('saveSysRole').then(res => {
            if (res.data.code === 200) {
              this.query()
              this.$Message.success(this.$t('system.success'))
              this.isInfo = false
            }
          })
        }
      })
    },
    add () {
      store.dispatch('insertRole', {})
      this.isInfo = true
    },
    query () {
      store.dispatch('getRoleList')
    },
    info (row) {
      store.dispatch('getSysRole', row)
      this.isInfo = true
    },
    changePage (pageNumber) {
      this.$store.state.menu.query.pageNumber = pageNumber
      this.query()
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.menu.query.pageNumber = 1
      this.$store.state.menu.query.singlePage = pageSizeNumber
      this.query()
    }
  }
}
</script>
<style lang="less">
  .split-pane-page-wrapper{
    height: 100%;
  }
  /*.ivu-form-item{*/
    /*margin-bottom: 4px;*/
  /*}*/
  .divider{
    margin: 2px 0px
  }
  .operation{
    text-align: right;
  }
</style>
