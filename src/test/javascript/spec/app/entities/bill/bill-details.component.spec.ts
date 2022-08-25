/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import BillDetailComponent from '@/entities/bill/bill-details.vue';
import BillClass from '@/entities/bill/bill-details.component';
import BillService from '@/entities/bill/bill.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Bill Management Detail Component', () => {
    let wrapper: Wrapper<BillClass>;
    let comp: BillClass;
    let billServiceStub: SinonStubbedInstance<BillService>;

    beforeEach(() => {
      billServiceStub = sinon.createStubInstance<BillService>(BillService);

      wrapper = shallowMount<BillClass>(BillDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { billService: () => billServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundBill = { id: 123 };
        billServiceStub.find.resolves(foundBill);

        // WHEN
        comp.retrieveBill(123);
        await comp.$nextTick();

        // THEN
        expect(comp.bill).toBe(foundBill);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundBill = { id: 123 };
        billServiceStub.find.resolves(foundBill);

        // WHEN
        comp.beforeRouteEnter({ params: { billId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.bill).toBe(foundBill);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
