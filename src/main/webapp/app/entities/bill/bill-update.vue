<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="billManagerApp.bill.home.createOrEditLabel"
          data-cy="BillCreateUpdateHeading"
          v-text="$t('billManagerApp.bill.home.createOrEditLabel')"
        >
          Create or edit a Bill
        </h2>
        <div>
          <div class="form-group" v-if="bill.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="bill.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('billManagerApp.bill.customer')" for="bill-customer">Customer</label>
            <multiselect
              v-model="customerSelected"
              :options="customers"
              :required="true"
              :searchable="true"
              :close-on-select="true"
              placeholder="Select customer"
              label="fullName"
              track-by="fullName"
              :multiple="false"
            >
            </multiselect>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('billManagerApp.bill.products')" for="bill-products">Products</label>
            <multiselect
              v-model="productSelected"
              :options="products"
              :required="true"
              :searchable="true"
              :close-on-select="true"
              placeholder="Select product"
              label="name"
              track-by="name"
              :multiple="true"
            ></multiselect>
          </div>

          <div class="table-responsive" v-if="productSelected && productSelected.length > 0">
            <table class="table table-striped" aria-describedby="productSelected">
              <thead>
                <tr>
                  <th scope="row">
                    <span>Tên sản phẩm</span>
                  </th>
                  <th scope="row">
                    <span>Đơn giá</span>
                  </th>
                  <th scope="row">
                    <span>Số lượng</span>
                  </th>
                  <th scope="row">
                    <span>Thành tiền</span>
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="pro in productSelected" :key="pro.id" data-cy="entityTable">
                  <td>{{ pro.name }}</td>
                  <td>{{ pro.price }}</td>
                  <td><input v-model.number="pro.qua" @keyup="getTotal(productSelected)" class="input" placeholder="Số lượng" /></td>
                  <td>{{ pro.qua * pro.price ? pro.qua * pro.price : 0 }}</td>
                </tr>
                <tr>
                  <td></td>
                  <td></td>
                  <td>Tổng cộng:</td>
                  <td>{{ total }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.bill.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./bill-update.component.ts"></script>
<style src="vue-multiselect/dist/vue-multiselect.min.css"></style>