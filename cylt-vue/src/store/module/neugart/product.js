import {
  addTreeList
} from '@/libs/util'
import { getProductList, saveProduct, del as delProduct } from '@/api/neugart/product'

export default {
  state: {
    productList: [],
    info: {},
    query: {
      custType: ''
    }
  },
  mutations: {
    productList (state, list) {
      list = addTreeList(list, (data) => {
        return {
          id: data.id,
          pid: data.pid,
          title: data.value,
          expand: true,
          data: data,
          orderBy: data.orderBy,
          children: []
        }
      })
      state.productList = list
    },
    getProduct (state, data) {
      data.discountList = data.discountList.sort(function (a, b) {
        return a.number - b.number
      })
      state.info = data
    },
    insertProduct (state, pid) {
      state.info = []
      state.info.pid = pid
      state.info.custType = state.query.custType
      state.info.value = ''
      state.info.defaultData = false
    },
    addDiscount (state) {
      if (state.info.discountList === undefined) {
        state.info.discountList = []
      }
      if (state.query.custType) {
        state.info.discountList.push({ number: 5, custType: state.query.custType, productId: state.info.id, discount: 100 })
      }
    },
    delDiscount (state, index) {
      state.info.discountList.splice(index, 1)
    }
  },
  actions: {
    getNeProductList ({ commit, rootState }, params) {
      return getProductList(params).then(res => {
        commit('productList', res.data)
      }).catch(e => {
        console.log(e)
      })
    },
    getNeProduct ({ commit, rootState }, params) {
      commit('getProduct', params.data)
    },
    saveSysproduct ({ commit, rootState }) {
      return saveProduct(rootState.product.info).then(res => {
      }).catch(e => {
        console.log(e)
      })
    },
    insertProduct ({ commit, rootState }, pid) {
      commit('insertProduct', pid)
    },
    delNeProduct ({ commit, rootState }, id) {
      return delProduct(id)
    },
    addDiscount ({ commit, rootState }) {
      commit('addDiscount')
    },
    delDiscount ({ commit, rootState }, data) {
      commit('delDiscount', data)
    }
  }
}
