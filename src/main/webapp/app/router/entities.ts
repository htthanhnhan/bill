import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore

// prettier-ignore
const Customer = () => import('@/entities/customer/customer.vue');
// prettier-ignore
const CustomerUpdate = () => import('@/entities/customer/customer-update.vue');
// prettier-ignore
const CustomerDetails = () => import('@/entities/customer/customer-details.vue');
// prettier-ignore
const Product = () => import('@/entities/product/product.vue');
// prettier-ignore
const ProductUpdate = () => import('@/entities/product/product-update.vue');
// prettier-ignore
const ProductDetails = () => import('@/entities/product/product-details.vue');
// prettier-ignore
const Bill = () => import('@/entities/bill/bill.vue');
// prettier-ignore
const BillUpdate = () => import('@/entities/bill/bill-update.vue');
// prettier-ignore
const BillDetails = () => import('@/entities/bill/bill-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default [
  {
    path: '/customer',
    name: 'Customer',
    component: Customer,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/customer/new',
    name: 'CustomerCreate',
    component: CustomerUpdate,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/customer/:customerId/edit',
    name: 'CustomerEdit',
    component: CustomerUpdate,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/customer/:customerId/view',
    name: 'CustomerView',
    component: CustomerDetails,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/product',
    name: 'Product',
    component: Product,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/product/new',
    name: 'ProductCreate',
    component: ProductUpdate,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/product/:productId/edit',
    name: 'ProductEdit',
    component: ProductUpdate,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/product/:productId/view',
    name: 'ProductView',
    component: ProductDetails,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/bill',
    name: 'Bill',
    component: Bill,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/bill/new',
    name: 'BillCreate',
    component: BillUpdate,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/bill/:billId/edit',
    name: 'BillEdit',
    component: BillUpdate,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/bill/:billId/view',
    name: 'BillView',
    component: BillDetails,
    meta: { authorities: [Authority.ADMIN] },
  },
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
