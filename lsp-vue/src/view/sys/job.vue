<template>
  <div data-menu="menu.sys.job" class="split-pane-page-wrapper">
    <Form inline  style="padding: 10px;margin-bottom: 20px;border: 2px solid #e8eaec;">
      <FormItem prop="code">
        <Input type="text" v-model="$store.state.job.query.jobName" :placeholder="$t('system.job.jobName')">
        </Input>
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
      :loading="$store.state.job.loading"
      context-menu
      show-context-menu
      :columns="columns1"
      :data="$store.state.job.list"
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
      :page-size="$store.state.job.query.singlePage"
      :total="$store.state.job.query.totalNumber"
      :current="$store.state.job.query.pageNumber"
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
      :title="$t('menu.sys.job') + $t('system.info')"
      v-model="isInfo"
      width="30%"
    >
      <Form ref="formValidate" :model="$store.state.job.info" :rules="validate">
        <Row :gutter="32">
          <Col span="24">
            <FormItem :label="$t('system.job.jobName')"  prop="jobName">
              <Input v-model="$store.state.job.info.jobName" :placeholder="$t('system.pleaseEnter') + $t('system.job.jobName')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('system.job.beanName')"  prop="beanName">
              <Input v-model="$store.state.job.info.beanName" :placeholder="$t('system.pleaseEnter') + $t('system.job.beanName')" />
            </FormItem>
          </Col>
          <Col span="24">
            <FormItem :label="$t('system.job.methodName')"  prop="methodName">
              <Input v-model="$store.state.job.info.methodName" :placeholder="$t('system.pleaseEnter') + $t('system.job.methodName')" />
            </FormItem>
          </Col>
          <Col span="12">
          <FormItem :label="$t('system.job.cron')"  prop="cron">
            <Input v-model="$store.state.job.info.cronExpression" :placeholder="$t('system.pleaseEnter') + $t('system.job.cron')" />
          </FormItem>
          </Col>
          <Col span="12">
          <FormItem :label="$t('system.job.state')"  prop="state">
            <Select v-model="$store.state.job.info.status" style="width: 100%" >
              <Option v-for="item in $dictList('JOB_STATE')" :value="item.dictValue" :key="item.id">{{ $t(item.title) }}</Option>
            </Select>
          </FormItem>
          </Col>
        </Row>
      </Form>
      <div class="demo-drawer-footer">
        <Button size="large" long v-jurisdiction="'edit'" type="primary" @click="save()">{{ $t('system.save') }}</Button>
      </div>
    </Drawer>
  </div>
</template>
<script>
import store from '@/store'
export default {
  data () {
    store.dispatch('getJobList', this.$store.state.job.query)
    return {
      columns1: [
        { type: 'index', width: 60, align: 'center' },
        { title: this.$t('system.job.jobName'), key: 'jobName' },
        { title: this.$t('system.job.beanName'), key: 'beanName' },
        { title: this.$t('system.job.methodName'), key: 'methodName' },
        { title: this.$t('system.job.cron'), key: 'cronExpression' },
        { title: this.$t('system.job.state'),
          key: 'status',
          render: (h, params) => {
            let color = '#000'
            switch (params.row.status) {
              case '0':
                color = 'green'
                break
              case '1':
                color = 'grey'
                break
            }
            return h('div', [
              h('strong', {
                style: {
                  color: color
                }
              }, this.$t(this.$dict('JOB_STATE', params.row.status)))
            ])
          }
        },
        { title: this.$t('system.operation'), slot: 'action', width: 150, align: 'center' }
      ],
      contextLine: 0,
      delConfirm: false,
      isInfo: false,
      validate: {
        code: [
          { required: true, message: this.$t('system.job.jobName') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        title: [
          { required: true, message: this.$t('system.job.beanName') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        content: [
          { required: true, message: this.$t('system.job.methodName') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        callbackUrl: [
          { required: true, message: this.$t('system.job.cron') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    del () {
      let data = this.deldata
      this.delConfirm = false
      store.dispatch('delJob', data.id).then(res => {
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
          store.dispatch('saveJob').then(res => {
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
      store.dispatch('insertJob', {})
      this.isInfo = true
    },
    query () {
      store.dispatch('getJobList', this.$store.state.job.query)
    },
    info (row) {
      store.dispatch('getJob', row)
      this.isInfo = true
    },
    changePage (pageNumber) {
      this.$store.state.job.query.pageNumber = pageNumber
      this.query()
    },
    changeSizePage (pageSizeNumber) {
      this.$store.state.job.query.pageNumber = 1
      this.$store.state.job.query.singlePage = pageSizeNumber
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
