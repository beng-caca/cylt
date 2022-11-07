<template>
  <div data-menu="menu.customer.user" class="split-pane-page-wrapper">
    <Form inline  style="padding: 10px;margin-bottom: 20px;border: 2px solid #e8eaec;">
      <FormItem prop="username">
        <Input type="text" v-model="$store.state.customer.query.loginId" :placeholder="$t('customer.loginId')">
        </Input>
      </FormItem>
      <FormItem prop="name">
        <Select v-model="$store.state.customer.query.customerType" style="width: 120px;margin: 1px 11px 1px 1px;" clearable>
          <Option v-for="item in $dictList('CUSTOMER_TYPE')" :value="item.dictValue" :key="item.id">{{ $t(item.title) }}</Option>
        </Select>
      </FormItem>
      <FormItem>
        <Button type="primary" @click="query()">{{ $t('system.query') }}</Button>
      </FormItem>

      <Divider dashed class="divider"/>
      <div class="operation">
        <Button type="primary" v-jurisdiction="'edit'" @click="add()">{{ $t('system.add') }}</Button>
      </div>
    </Form>
    <Table
      :loading="$store.state.customer.loading"
      context-menu
      show-context-menu
      :columns="columns1"
      :data="$store.state.customer.list"
      border
      stripe
    >
      <template slot-scope="{ row, index }" slot="action">
        <Button type="primary" size="small" style="margin-right: 5px" @click="info(row, index)">{{ $t('system.info') }}</Button>
        <Button type="error" v-jurisdiction="'del'" size="small" @click="delInit(row)">{{ $t('system.del') }}</Button>
      </template>
    </Table>
    <Page
      @on-change="changePage"
      @on-page-size-change="changeSizePage"
      :page-size="$store.state.customer.query.singlePage"
      :total="$store.state.customer.query.totalNumber"
      :current="$store.state.customer.query.pageNumber"
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
      :title="$t('menu.sys.user') + $t('system.info')"
      v-model="isInfo"
      width="30%"
    >
      <Form ref="formValidate" :model="$store.state.user.info" :rules="validate">
        <Row :gutter="32">
          <Col span="24">
            <FormItem :label="$t('customer.loginId')"  prop="loginId">
              <Input v-model="$store.state.customer.info.loginId" :placeholder="$t('system.pleaseEnter') + $t('customer.loginId')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('customer.customerName')"  prop="customerName">
              <Input v-model="$store.state.customer.info.customerName" :placeholder="$t('system.pleaseEnter') + $t('customer.customerName')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('customer.customerType')"  prop="customerType">
              <Select v-model="$store.state.customer.info.customerType" style="width: 100%" >
                <Option v-for="item in $dictList('CUSTOMER_TYPE')" :value="item.dictValue" :key="item.id">{{ $t(item.title) }}</Option>
              </Select>
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('customer.remakes')"  prop="remakes">
              <Input v-model="$store.state.customer.info.remakes" :placeholder="$t('system.pleaseEnter') + $t('customer.remakes')" />
            </FormItem>
          </Col>
        </Row>
      </Form>
      <div v-jurisdiction="'edit'" class="demo-drawer-footer" style="padding-top:20px;">
        <Button size="large" long type="primary" @click="save()">{{ $t('system.save') }}</Button>
      </div>
    </Drawer>
  </div>
</template>
<script>
import store from '@/store'
export default {
  data () {
    store.dispatch('getCustomerList')
    return {
      columns1: [
        { type: 'index', width: 60, align: 'center' },
        { title: this.$t('customer.loginId'), key: 'loginId' },
        { title: this.$t('customer.customerName'), key: 'customerName' },
        { title: this.$t('customer.customerType'), key: 'customerType',
          render: (h, params) => {
            return h('div', [
              h('strong', {}, this.$t(this.$dict('CUSTOMER_TYPE', params.row.customerType)))
            ])
          } },
        { title: this.$t('customer.remakes'), key: 'remakes' },
        { title: this.$t('system.operation'), slot: 'action', width: 150, align: 'center' }
      ],
      contextLine: 0,
      delConfirm: false,
      isInfo: false,
      validate: {
        loginId: [
          { required: true, message: this.$t('customer.loginId') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        customerType: [
          { required: true, message: this.$t('customer.customerType') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    del () {
      let data = this.deldata
      this.delConfirm = false
      store.dispatch('delCustomer', data.id).then(res => {
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
          store.dispatch('saveCustomer').then(res => {
            if (res.data.code === 200) {
              this.query()
              this.$Message.success(this.$t('system.success'))
              this.isInfo = false
            } else if (res.data.message === '用户名已存在！') {
              this.$Message.error(this.$t('system.user.username') + this.$t('system.validate.noRepeat'))
            } else {
              this.$Message.error(this.$t('system.fail'))
            }
          })
        }
      })
    },
    add () {
      store.dispatch('getCustomerInfo', {})
      this.isInfo = true
    },
    query () {
      store.dispatch('getCustomerList')
    },
    info (row) {
      store.dispatch('getCustomerInfo', row)
      this.isInfo = true
    },
    changePage (pageNumber) {
      this.$store.state.customer.query.pageNumber = pageNumber
      this.query()
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.customer.query.pageNumber = 1
      this.$store.state.customer.query.singlePage = pageSizeNumber
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
