<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="bill">
        <h2 class="jh-entity-heading" data-cy="billDetailsHeading">
          <span v-text="$t('billManagerApp.bill.detail.title')">Bill</span> {{ bill.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('billManagerApp.bill.products')">Products</span>
          </dt>
          <dd>
            <table>
                <thead>
                  <tr>
                  <th>Tên sản phẩm</th>
                  <th>Số lượng</th>
                  <th>Thành tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(pro, index) in bill.JProduct" :key="index">
                    <td>{{ pro.name }}</td>
                    <td>{{ pro.qua }}</td>
                    <td>{{ pro.tota }}</td>
                  </tr>
                </tbody>
              </table>
          </dd>
          <dt>
            <span v-text="$t('billManagerApp.bill.customer')">Customer</span>
          </dt>
          <dd>
            <span>{{ bill.customer }}</span>
          </dd>
          <dt>
            <span v-text="$t('billManagerApp.bill.total')">Total</span>
          </dt>
          <dd>
            <span>{{ bill.total }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="bill.id" :to="{ name: 'BillEdit', params: { billId: bill.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./bill-details.component.ts"></script>
