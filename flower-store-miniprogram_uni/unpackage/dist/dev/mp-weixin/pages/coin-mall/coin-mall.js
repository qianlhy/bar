(global["webpackJsonp"] = global["webpackJsonp"] || []).push([["pages/coin-mall/coin-mall"],{

/***/ 189:
/*!*******************************************************************************************************************************************************************!*\
  !*** C:/Users/Administrator/Desktop/2026年1月7日/卡歌/酒吧小程序/flower-store-backend-sb_uni/flower-store-miniprogram_uni/main.js?{"page":"pages%2Fcoin-mall%2Fcoin-mall"} ***!
  \*******************************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(wx, createPage) {

var _interopRequireDefault = __webpack_require__(/*! @babel/runtime/helpers/interopRequireDefault */ 4);
__webpack_require__(/*! uni-pages */ 26);
var _vue = _interopRequireDefault(__webpack_require__(/*! vue */ 25));
var _coinMall = _interopRequireDefault(__webpack_require__(/*! ./pages/coin-mall/coin-mall.vue */ 190));
// @ts-ignore
wx.__webpack_require_UNI_MP_PLUGIN__ = __webpack_require__;
createPage(_coinMall.default);
/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/wx.js */ 1)["default"], __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/index.js */ 2)["createPage"]))

/***/ }),

/***/ 190:
/*!************************************************************************************************************************************************!*\
  !*** C:/Users/Administrator/Desktop/2026年1月7日/卡歌/酒吧小程序/flower-store-backend-sb_uni/flower-store-miniprogram_uni/pages/coin-mall/coin-mall.vue ***!
  \************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _coin_mall_vue_vue_type_template_id_1a26ce30___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./coin-mall.vue?vue&type=template&id=1a26ce30& */ 191);
/* harmony import */ var _coin_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./coin-mall.vue?vue&type=script&lang=js& */ 193);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _coin_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _coin_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__[key]; }) }(__WEBPACK_IMPORT_KEY__));
/* harmony import */ var _coin_mall_vue_vue_type_style_index_0_lang_css___WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./coin-mall.vue?vue&type=style&index=0&lang=css& */ 196);
/* harmony import */ var _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/runtime/componentNormalizer.js */ 32);

var renderjs





/* normalize component */

var component = Object(_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_runtime_componentNormalizer_js__WEBPACK_IMPORTED_MODULE_3__["default"])(
  _coin_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_1__["default"],
  _coin_mall_vue_vue_type_template_id_1a26ce30___WEBPACK_IMPORTED_MODULE_0__["render"],
  _coin_mall_vue_vue_type_template_id_1a26ce30___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"],
  false,
  null,
  null,
  null,
  false,
  _coin_mall_vue_vue_type_template_id_1a26ce30___WEBPACK_IMPORTED_MODULE_0__["components"],
  renderjs
)

component.options.__file = "pages/coin-mall/coin-mall.vue"
/* harmony default export */ __webpack_exports__["default"] = (component.exports);

/***/ }),

/***/ 191:
/*!*******************************************************************************************************************************************************************************!*\
  !*** C:/Users/Administrator/Desktop/2026年1月7日/卡歌/酒吧小程序/flower-store-backend-sb_uni/flower-store-miniprogram_uni/pages/coin-mall/coin-mall.vue?vue&type=template&id=1a26ce30& ***!
  \*******************************************************************************************************************************************************************************/
/*! exports provided: render, staticRenderFns, recyclableRender, components */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_template_id_1a26ce30___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--17-0!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/template.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-uni-app-loader/page-meta.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./coin-mall.vue?vue&type=template&id=1a26ce30& */ 192);
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "render", function() { return _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_template_id_1a26ce30___WEBPACK_IMPORTED_MODULE_0__["render"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_template_id_1a26ce30___WEBPACK_IMPORTED_MODULE_0__["staticRenderFns"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "recyclableRender", function() { return _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_template_id_1a26ce30___WEBPACK_IMPORTED_MODULE_0__["recyclableRender"]; });

/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "components", function() { return _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_templateLoader_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_17_0_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_template_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_uni_app_loader_page_meta_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_template_id_1a26ce30___WEBPACK_IMPORTED_MODULE_0__["components"]; });



/***/ }),

/***/ 192:
/*!*******************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--17-0!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/template.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-uni-app-loader/page-meta.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!C:/Users/Administrator/Desktop/2026年1月7日/卡歌/酒吧小程序/flower-store-backend-sb_uni/flower-store-miniprogram_uni/pages/coin-mall/coin-mall.vue?vue&type=template&id=1a26ce30& ***!
  \*******************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! exports provided: render, staticRenderFns, recyclableRender, components */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "render", function() { return render; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "staticRenderFns", function() { return staticRenderFns; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "recyclableRender", function() { return recyclableRender; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "components", function() { return components; });
var components
var render = function () {
  var _vm = this
  var _h = _vm.$createElement
  var _c = _vm._self._c || _h
  var g0 = !_vm.isLoading && _vm.goods.length === 0
  _vm.$mp.data = Object.assign(
    {},
    {
      $root: {
        g0: g0,
      },
    }
  )
}
var recyclableRender = false
var staticRenderFns = []
render._withStripped = true



/***/ }),

/***/ 193:
/*!*************************************************************************************************************************************************************************!*\
  !*** C:/Users/Administrator/Desktop/2026年1月7日/卡歌/酒吧小程序/flower-store-backend-sb_uni/flower-store-miniprogram_uni/pages/coin-mall/coin-mall.vue?vue&type=script&lang=js& ***!
  \*************************************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!./node_modules/babel-loader/lib!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--13-1!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/script.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./coin-mall.vue?vue&type=script&lang=js& */ 194);
/* harmony import */ var _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0__[key]; }) }(__WEBPACK_IMPORT_KEY__));
 /* harmony default export */ __webpack_exports__["default"] = (_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_babel_loader_lib_index_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_13_1_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_script_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_script_lang_js___WEBPACK_IMPORTED_MODULE_0___default.a); 

/***/ }),

/***/ 194:
/*!********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/babel-loader/lib!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--13-1!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/script.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!C:/Users/Administrator/Desktop/2026年1月7日/卡歌/酒吧小程序/flower-store-backend-sb_uni/flower-store-miniprogram_uni/pages/coin-mall/coin-mall.vue?vue&type=script&lang=js& ***!
  \********************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(uni) {

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

var coinApi = __webpack_require__(/*! ../../api/coin */ 195);
var userApi = __webpack_require__(/*! ../../api/user */ 51);
var _default = {
  data: function data() {
    return {
      coins: 0,
      goods: [],
      isLoading: true
    };
  },
  onShow: function onShow() {
    this.loadUser();
    this.loadGoods();
  },
  methods: {
    loadUser: function loadUser() {
      var _this = this;
      var token = uni.getStorageSync('token');
      if (!token) {
        this.coins = 0;
        return;
      }
      userApi.getUserInfo().then(function (data) {
        _this.coins = data.coins || 0;
      }).catch(function () {});
    },
    loadGoods: function loadGoods() {
      var _this2 = this;
      this.isLoading = true;
      coinApi.getCoinProducts().then(function (list) {
        _this2.goods = list || [];
        _this2.isLoading = false;
      }).catch(function () {
        _this2.isLoading = false;
      });
    },
    goRecords: function goRecords() {
      uni.navigateTo({
        url: '/pages/coin-records/coin-records'
      });
    },
    onExchange: function onExchange(e) {
      var _this3 = this;
      var token = uni.getStorageSync('token');
      if (!token) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        setTimeout(function () {
          return uni.navigateTo({
            url: '/pages/login/login'
          });
        }, 1500);
        return;
      }
      var item = this.goods[e.currentTarget.dataset.index];
      if (!item) return;
      if (item.stock <= 0) {
        uni.showToast({
          title: '库存不足',
          icon: 'none'
        });
        return;
      }
      if (this.coins < item.coinPrice) {
        uni.showToast({
          title: 'All In 币不足',
          icon: 'none'
        });
        return;
      }
      uni.showModal({
        title: '确认兑换',
        content: "\u786E\u5B9A\u82B1\u8D39 ".concat(item.coinPrice, " \u4E2A All In \u5E01\u5151\u6362\u300C").concat(item.name, "\u300D\u5417\uFF1F"),
        success: function success(res) {
          if (res.confirm) _this3.doExchange(item.id);
        }
      });
    },
    doExchange: function doExchange(productId) {
      var _this4 = this;
      coinApi.exchange(productId).then(function () {
        uni.showToast({
          title: '兑换成功',
          icon: 'success'
        });
        _this4.loadUser();
        _this4.loadGoods();
      }).catch(function () {});
    }
  }
};
exports.default = _default;
/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./node_modules/@dcloudio/uni-mp-weixin/dist/index.js */ 2)["default"]))

/***/ }),

/***/ 196:
/*!*********************************************************************************************************************************************************************************!*\
  !*** C:/Users/Administrator/Desktop/2026年1月7日/卡歌/酒吧小程序/flower-store-backend-sb_uni/flower-store-miniprogram_uni/pages/coin-mall/coin-mall.vue?vue&type=style&index=0&lang=css& ***!
  \*********************************************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_6_oneOf_1_0_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_6_oneOf_1_1_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_6_oneOf_1_2_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_6_oneOf_1_3_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_style_index_0_lang_css___WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! -!./node_modules/mini-css-extract-plugin/dist/loader.js??ref--6-oneOf-1-0!./node_modules/css-loader/dist/cjs.js??ref--6-oneOf-1-1!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/stylePostLoader.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--6-oneOf-1-2!./node_modules/postcss-loader/src??ref--6-oneOf-1-3!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!./coin-mall.vue?vue&type=style&index=0&lang=css& */ 197);
/* harmony import */ var _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_6_oneOf_1_0_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_6_oneOf_1_1_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_6_oneOf_1_2_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_6_oneOf_1_3_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_style_index_0_lang_css___WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_6_oneOf_1_0_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_6_oneOf_1_1_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_6_oneOf_1_2_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_6_oneOf_1_3_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_style_index_0_lang_css___WEBPACK_IMPORTED_MODULE_0__);
/* harmony reexport (unknown) */ for(var __WEBPACK_IMPORT_KEY__ in _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_6_oneOf_1_0_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_6_oneOf_1_1_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_6_oneOf_1_2_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_6_oneOf_1_3_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_style_index_0_lang_css___WEBPACK_IMPORTED_MODULE_0__) if(["default"].indexOf(__WEBPACK_IMPORT_KEY__) < 0) (function(key) { __webpack_require__.d(__webpack_exports__, key, function() { return _E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_6_oneOf_1_0_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_6_oneOf_1_1_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_6_oneOf_1_2_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_6_oneOf_1_3_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_style_index_0_lang_css___WEBPACK_IMPORTED_MODULE_0__[key]; }) }(__WEBPACK_IMPORT_KEY__));
 /* harmony default export */ __webpack_exports__["default"] = (_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_mini_css_extract_plugin_dist_loader_js_ref_6_oneOf_1_0_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_css_loader_dist_cjs_js_ref_6_oneOf_1_1_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_loaders_stylePostLoader_js_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_webpack_preprocess_loader_index_js_ref_6_oneOf_1_2_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_postcss_loader_src_index_js_ref_6_oneOf_1_3_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_vue_cli_plugin_uni_packages_vue_loader_lib_index_js_vue_loader_options_E_A_new_start_HBuilderX_4_87_2025121004_HBuilderX_plugins_uniapp_cli_node_modules_dcloudio_webpack_uni_mp_loader_lib_style_js_coin_mall_vue_vue_type_style_index_0_lang_css___WEBPACK_IMPORTED_MODULE_0___default.a); 

/***/ }),

/***/ 197:
/*!*************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************!*\
  !*** ./node_modules/mini-css-extract-plugin/dist/loader.js??ref--6-oneOf-1-0!./node_modules/css-loader/dist/cjs.js??ref--6-oneOf-1-1!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib/loaders/stylePostLoader.js!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/webpack-preprocess-loader??ref--6-oneOf-1-2!./node_modules/postcss-loader/src??ref--6-oneOf-1-3!./node_modules/@dcloudio/vue-cli-plugin-uni/packages/vue-loader/lib??vue-loader-options!./node_modules/@dcloudio/webpack-uni-mp-loader/lib/style.js!C:/Users/Administrator/Desktop/2026年1月7日/卡歌/酒吧小程序/flower-store-backend-sb_uni/flower-store-miniprogram_uni/pages/coin-mall/coin-mall.vue?vue&type=style&index=0&lang=css& ***!
  \*************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

// extracted by mini-css-extract-plugin
    if(false) { var cssReload; }
  

/***/ })

},[[189,"common/runtime","common/vendor"]]]);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/coin-mall/coin-mall.js.map