<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="billManagerApp.customer.home.createOrEditLabel"
          data-cy="CustomerCreateUpdateHeading"
          v-text="$t('billManagerApp.customer.home.createOrEditLabel')"
        >
          Create or edit a Customer
        </h2>
        <div>
          <div class="form-group" v-if="customer.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="customer.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('billManagerApp.customer.fullName')" for="customer-fullName">Full Name</label>
            <input
              type="text"
              class="form-control"
              name="fullName"
              id="customer-fullName"
              data-cy="fullName"
              :class="{ valid: !$v.customer.fullName.$invalid, invalid: $v.customer.fullName.$invalid }"
              v-model="$v.customer.fullName.$model"
              required
            />
            <div v-if="$v.customer.fullName.$anyDirty && $v.customer.fullName.$invalid">
              <small class="form-text text-danger" v-if="!$v.customer.fullName.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.customer.fullName.minLength"
                v-text="$t('entity.validation.minlength', { min: 0 })"
              >
                This field is required to be at least 0 characters.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.customer.fullName.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 100 })"
              >
                This field cannot be longer than 100 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('billManagerApp.customer.email')" for="customer-email">Email</label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="customer-email"
              data-cy="email"
              :class="{ valid: !$v.customer.email.$invalid, invalid: $v.customer.email.$invalid }"
              v-model="$v.customer.email.$model"
              required
            />
            <div v-if="$v.customer.email.$anyDirty && $v.customer.email.$invalid">
              <small class="form-text text-danger" v-if="!$v.customer.email.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.customer.email.minLength"
                v-text="$t('entity.validation.minlength', { min: 0 })"
              >
                This field is required to be at least 0 characters.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.customer.email.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 50 })"
              >
                This field cannot be longer than 50 characters.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.customer.email.pattern"
                v-text="$t('entity.validation.pattern', { pattern: 'Email' })"
              >
                This field should follow pattern for "Email".
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('billManagerApp.customer.address')" for="customer-address">Address</label>
            <input
              type="text"
              class="form-control"
              name="address"
              id="customer-address"
              data-cy="address"
              :class="{ valid: !$v.customer.address.$invalid, invalid: $v.customer.address.$invalid }"
              v-model="$v.customer.address.$model"
            />
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
            :disabled="$v.customer.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./customer-update.component.ts"></script>
