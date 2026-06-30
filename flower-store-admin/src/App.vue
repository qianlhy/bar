<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

#app {
  width: 100%;
  height: 100vh;
}

/* 梭哈酒馆品牌主题：以红色为主色，金色为点缀，呼应小程序黑金风格 */
:root {
  --el-color-primary: #c41e3a;
  --el-color-primary-light-3: #d34b60;
  --el-color-primary-light-5: #e08293;
  --el-color-primary-light-7: #edb3bf;
  --el-color-primary-light-8: #f4cdd5;
  --el-color-primary-light-9: #fae6ea;
  --el-color-primary-dark-2: #9d1830;
}

/* 表格表头、卡片标题等点缀金色边 */
.el-card__header {
  border-top: 3px solid #e8c547;
}
</style>

