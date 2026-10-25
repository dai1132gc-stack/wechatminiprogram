import type { RouteRecordRaw } from 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    roles?: string[]
    public?: boolean
    hidden?: boolean
  }
}

/** 布局组件（路由表引用，避免循环导入） */
const Layout = () => import('@/layout/index.vue')

export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true, title: '登录' },
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { public: true, title: '404' },
  },
]

export const asyncRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'Odometer' },
      },
    ],
  },
  {
    path: '/house',
    component: Layout,
    redirect: '/house/list',
    meta: { title: '房源', icon: 'House' },
    children: [
      {
        path: 'list',
        name: 'HouseList',
        component: () => import('@/views/house/list.vue'),
        meta: { title: '房源管理', roles: ['LANDLORD', 'ADMIN'] },
      },
      {
        path: 'edit/:id?',
        name: 'HouseEdit',
        component: () => import('@/views/house/edit.vue'),
        meta: { title: '编辑房源', roles: ['LANDLORD', 'ADMIN'], hidden: true },
      },
      {
        path: 'detail/:id',
        name: 'HouseDetail',
        component: () => import('@/views/house/detail.vue'),
        meta: { title: '房源详情', roles: ['LANDLORD', 'ADMIN'], hidden: true },
      },
      {
        path: 'audit',
        name: 'HouseAudit',
        component: () => import('@/views/house/audit.vue'),
        meta: { title: '房源审核', roles: ['ADMIN'] },
      },
    ],
  },
  {
    path: '/application',
    component: Layout,
    children: [
      {
        path: 'list',
        name: 'ApplicationList',
        component: () => import('@/views/application/list.vue'),
        meta: { title: '租赁申请', roles: ['LANDLORD', 'ADMIN'] },
      },
    ],
  },
  {
    path: '/contract',
    component: Layout,
    children: [
      {
        path: 'list',
        name: 'ContractList',
        component: () => import('@/views/contract/list.vue'),
        meta: { title: '合同管理', roles: ['LANDLORD', 'ADMIN'] },
      },
      {
        path: 'detail/:id',
        name: 'ContractDetail',
        component: () => import('@/views/contract/detail.vue'),
        meta: { title: '合同详情', roles: ['LANDLORD', 'ADMIN'], hidden: true },
      },
    ],
  },
  {
    path: '/device',
    component: Layout,
    children: [
      {
        path: 'list',
        name: 'DeviceList',
        component: () => import('@/views/device/list.vue'),
        meta: { title: '设备管理', roles: ['LANDLORD', 'ADMIN'] },
      },
      {
        path: 'bind/:id',
        name: 'DeviceBind',
        component: () => import('@/views/device/bind.vue'),
        meta: { title: '绑定房源', roles: ['LANDLORD', 'ADMIN'], hidden: true },
      },
    ],
  },
  {
    path: '/env',
    component: Layout,
    meta: { title: '环境监测', icon: 'DataLine' },
    children: [
      {
        path: 'overview',
        name: 'EnvOverview',
        component: () => import('@/views/env/overview.vue'),
        meta: { title: '环境总览', roles: ['ADMIN'] },
      },
      {
        path: 'detail/:houseId',
        name: 'EnvDetail',
        component: () => import('@/views/env/detail.vue'),
        meta: { title: '环境详情', roles: ['LANDLORD', 'ADMIN'], hidden: true },
      },
      {
        path: 'alarm',
        name: 'AlarmList',
        component: () => import('@/views/env/alarm.vue'),
        meta: { title: '告警处置', roles: ['LANDLORD', 'ADMIN'] },
      },
    ],
  },
  {
    path: '/system',
    component: Layout,
    meta: { title: '系统', icon: 'Setting' },
    children: [
      {
        path: 'user',
        name: 'UserManage',
        component: () => import('@/views/system/user.vue'),
        meta: { title: '用户管理', roles: ['ADMIN'] },
      },
      {
        path: 'log',
        name: 'OperationLog',
        component: () => import('@/views/system/log.vue'),
        meta: { title: '操作日志', roles: ['ADMIN'] },
      },
      {
        path: 'dict',
        name: 'DictManage',
        component: () => import('@/views/system/dict.vue'),
        meta: { title: '字典管理', roles: ['ADMIN'] },
      },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/404', meta: { hidden: true } },
]
