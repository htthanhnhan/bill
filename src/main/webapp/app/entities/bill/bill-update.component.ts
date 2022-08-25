import { Component, Vue, Inject } from 'vue-property-decorator';

import { decimal, minValue } from 'vuelidate/lib/validators';

import { IBill, Bill } from '@/shared/model/bill.model';
import BillService from './bill.service';
import Multiselect from 'vue-multiselect';
import { ICustomer } from '@/shared/model/customer.model';
import { IProduct } from '@/shared/model/product.model';
import CustomerService from '../customer/customer.service';
import ProductService from '../product/product.service';

const validations: any = {
  bill: {
    products: {},
    customer: {},
    total: {
      decimal,
      min: minValue(0),
    },
  },
};
Vue.component('multiselect', Multiselect);

@Component({
  validations,
})
export default class BillUpdate extends Vue {

  public customerSelected: any = null;
  public productSelected: any[] = null;

  public total: number = 0;

  public customers: ICustomer[] = [];
  public products: IProduct[] = [];

  @Inject('customerService') private customerService: () => CustomerService;
  @Inject('productService') private productService: () => ProductService;
  @Inject('billService') private billService: () => BillService;
  public bill: IBill = new Bill();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.billId) {
        vm.retrieveBill(to.params.billId);
      }
    });
  }

  public mounted(): void {
    this.getCustomers();
    this.getProducts();
    
  }

  getTotal(pro:any[]): void {
    this.total = 0;
    if(pro && pro.length > 0) {
      for(let p in pro) {
        if(pro[p].qua * pro[p].price && pro[p].name.length > 0){
          this.total = this.total + pro[p].qua * pro[p].price;
        }
      }
    }
    return;
  }


  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.bill.id) {
      this.billService()
        .update(this.bill)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('billManagerApp.bill.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      let result: any[] = [];
      for (let i in this.productSelected) {
        result.push(
          {
            "name": this.productSelected[i].name,
            "qua": this.productSelected[i].qua,
            "tota": this.productSelected[i].qua * this.productSelected[i].price
          }
        );
        
      }
      this.bill.products = JSON.stringify(result);
      this.bill.customer = this.customerSelected.fullName;
      this.bill.total = this.total;
      this.billService()
        .create(this.bill)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('billManagerApp.bill.created', { param: param.id });
          this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    }
  }

  public retrieveBill(billId): void {
    this.billService()
      .find(billId)
      .then(res => {
        this.bill = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}

  public getCustomers(): any {
    this.customerService()
      .retrieve()
      .then(res => {
        this.customers = res.data;
      });
  }

  public getProducts(): any {
    this.productService()
      .retrieve()
      .then(res => {
        this.products = res.data;
      });
  }

}
