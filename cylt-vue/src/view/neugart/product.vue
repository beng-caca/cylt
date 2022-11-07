<template>
  <div data-menu="menu.customer.product" class="split-pane-page-wrapper">
    <split-pane v-model="offset">
      <Card  slot="left" class="pane"  style="height:100%;display:block;overflow-y:auto;" dis-hover>
        <Spin size="large" fix v-if="isLoading"></Spin>
        <div slot="title" style="height: 32px;">
          <i-col :span="10" style="font-size: 27px;">
          {{ $t('menu.customer.product') + $t('system.tree') }}
          </i-col>
        </div>
        <Tree :data="$store.state.product.productList" :render="renderContent"></Tree>
      </Card>
      <Card slot="right" class="pane" style="height:100%;" dis-hover>
        <p slot="title"  style="font-size: 20px;height: 32px;">
          {{ $t('menu.customer.product') + $t('system.info') + ' - ' + $store.state.product.info.value }}
        </p>
        <Form ref="formValidate" :model="$store.state.product.info" :rules="ruleValidate" :label-width="120">
          <FormItem  :label-width="120" prop="title">
            <span slot='label'>{{ $t('neugart.title') }}</span>
            <Input v-model="$store.state.product.info.title"/>
          </FormItem>
          <FormItem prop="value"  :label-width="120">
            <span slot='label'>{{ $t('neugart.value') }}</span>
            <Input v-model="$store.state.product.info.value"/>
          </FormItem>
          <FormItem :label-width="120" prop="icon">
            <span slot='label'>{{ $t('neugart.icon') }}</span>
            <i-col :span="14">
              <Upload :on-format-error="handleFormatError" :format="['jpg','jpeg','png']" ref="upload" :before-upload="handleUpload" action="">
                <Button icon="ios-cloud-upload-outline">{{ $t('update') }}</Button>
              </Upload>
            </i-col>
            <i-col :span="10">
              <img style="width: 100%;border: 2px solid #dcdee2;" v-if="$store.state.product.info.icon" :src="$store.state.product.info.icon"/>
            </i-col>
           <!-- <Input v-model="$store.state.product.info.icon"/>-->
          </FormItem>
          <FormItem :label="$t('neugart.valuationType')"  prop="valuationType">
              <Select v-model="$store.state.product.info.valuationType" style="width: 100%" >
                <Option v-for="item in $dictList('VALUATION_TYPE')" :value="item.dictValue" :key="item.id">{{ $t(item.title) }}</Option>
              </Select>
          </FormItem>
          <FormItem v-if="$store.state.product.info.valuationType !== '0'" prop="valuation"  :label-width="120">
            <span slot='label'>{{ $t('neugart.valuation') }}</span>
            <Input v-model="$store.state.product.info.valuation"/>
          </FormItem>
          <FormItem prop="orderBy"  :label-width="120">
            <span slot='label'>{{ $t('neugart.order') }}</span>
            <Input v-model="$store.state.product.info.orderBy"/>
          </FormItem>
          <FormItem :label-width="120" prop="defaultData">
            <span slot='label'>{{ $t('neugart.defaultData') }}</span>
            <i-switch v-model="$store.state.product.info.defaultData" size="large">
            </i-switch>
          </FormItem>
          <h2>满减折扣</h2>
          <Table border :columns="discount.title" :data="$store.state.product.info.discountList">
            <template slot-scope="{ row, index }" slot="number">
              <Input-number :min="1" v-model="$store.state.product.info.discountList[index].number"></Input-number>
            </template>
            <template slot-scope="{ row, index }" slot="discount">
              <Slider v-model="$store.state.product.info.discountList[index].discount" :tip-format="hideFormat" show-input></Slider>
            </template>
            <template slot-scope="{ row, index }" slot="action">
              <Button type="error" v-jurisdiction="'del'" size="small" @click="delDiscount(index)">{{ $t('system.del') }}</Button>
            </template>
          </Table>
          <i-col :span="24">
            <div class="add-btn" @click="addDiscount">+</div>
          </i-col>
          <FormItem :label-width="120">
            <Button type="primary" v-jurisdiction="'edit'" @click="save()">{{ $t('system.save') }}</Button>
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
    this.isLoading = true
    store.dispatch('getNeProductList', this.$store.state.product.query).then(() => {
      this.isLoading = false
    })
    // 统计要筛选的客户分类
    let customerTypeList = []
    for (let i in this.$dictList('CUSTOMER_TYPE')) {
      let data = { label: this.$t(this.$dict('CUSTOMER_TYPE', this.$dictList('CUSTOMER_TYPE')[i].dictValue)), value: this.$dictList('CUSTOMER_TYPE')[i].dictValue }
      customerTypeList.push(data)
    }
    // 初始化筛选记忆
    let _this = this
    _this.$store.state.product.query.custType = null
    return {
      offset: 0.4,
      delConfirm: false,
      offsetVertical: '10px',
      isLoading: false,
      custType: null,
      ruleValidate: {
        value: [
          { required: true, message: this.$t('neugart.value') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ],
        valuationType: [
          { required: true, message: this.$t('neugart.valuationType') + this.$t('system.validate.notNull'), trigger: 'blur' }
        ]
      },
      discount: {
        title: [
          { title: this.$t('neugart.discount.number'), width: 150, slot: 'number' },
          { title: this.$t('neugart.discount.discount'), slot: 'discount' },
          { title: this.$t('customer.customerType'), key: 'custType',
            render: (h, params) => {
              return h('div', [
                h('strong', {}, this.$t(this.$dict('CUSTOMER_TYPE', params.row.custType)))
              ])
            },
            filters: customerTypeList,
            filterMultiple: false,
            filterMethod (value, row) {
              _this.$store.state.product.query.custType = value
              return value === row.custType;
            }
          },
          { title: this.$t('system.operation'), slot: 'action', width: 70, align: 'center' }
        ],
        data: [{
          number: 6,
          discount: 50
        },
        {
          number: 6,
          discount: 80
        }]
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
          width: '100%',
          backgroundColor: '#F0F0F0'
        }
      }, [
        h('span', [
          h('span', {
            style: {
              fontSize: '20px'
            }
          }, this.$t(data.title))
        ]),
        h('span', {
          style: {
            display: 'inline-block',
            float: 'right',
            marginRight: '32px',
            fontSize: '20px'
          }
        }, [
          this.$jurisdiction('menu.customer.product', 'edit') ? h('Button', {
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
          }) : h('span'),
          this.$jurisdiction('menu.customer.product', 'edit') ? h('Button', {
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
          }) : h('span'),
          this.$jurisdiction('menu.customer.product', 'del') ? h('Button', {
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
          }) : h('span')
        ])
      ])
    },
    remove () {
      let { root, node, data } = this.del
      this.delConfirm = false
      store.dispatch('delNeProduct', data.id).then(res => {
        const parentKey = root.find(el => el === node).parent
        const parent = root.find(el => el.nodeKey === parentKey).node
        const index = parent.children.indexOf(data)
        parent.children.splice(index, 1)
        this.$Message.success(this.$t('system.success'))
      })
    },
    insertInit (data) {
      store.dispatch('insertProduct', data.id)
    },
    updateInit (root, node, data) {
      store.dispatch('getNeProduct', data)
    },
    save () {
      this.$refs['formValidate'].validate((valid) => {
        if (valid) {
          store.dispatch('saveSysproduct').then(res => {
            this.$Message.success(this.$t('system.success'))
            this.query()
          })
        }
      })
    },
    handleUpload (file) {
      const reader = new FileReader()
      // 将文件读取为 DataURL 以data:开头的字符串
      reader.readAsDataURL(file)
      reader.onload = e => {
        // 读取到的图片base64 数据编码 将此编码字符串传给后台即可
        const code = e.target.result
        this.$store.state.product.info.icon = code
        this.$forceUpdate()
        console.log(code)
      }
      return false
    },
    handleFormatError (file) {
      this.$Notice.warning({
        title: 'The file format is incorrect',
        desc: 'File format of ' + file.name + ' is incorrect, please select jpg or png.'
      })
    },
    upload () {
      return false
    },
    query () {
      this.isLoading = true
      store.dispatch('getNeProductList', this.$store.state.product.query).then(() => {
        this.isLoading = false
      })
    },
    hideFormat () {
      return null
    },
    addDiscount () {
      store.dispatch('addDiscount').then(res => {
        this.$forceUpdate()
      })
    },
    delDiscount (index) {
      store.dispatch('delDiscount', index).then(res => {
        this.$forceUpdate()
      })
    }
  }
}
</script>
<style lang="less">
  .split-pane-page-wrapper{
    height: 100%;
  }
  .add-btn {
    text-align: center;
    font-size: 20px;
    background-color: #578826;
    color: #fff;
    cursor: copy;
    width: 60px;
  }
</style>
