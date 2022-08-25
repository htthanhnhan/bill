package com.irtech.billmanager.web.rest;

import static com.irtech.billmanager.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.irtech.billmanager.IntegrationTest;
import com.irtech.billmanager.domain.Bill;
import com.irtech.billmanager.repository.BillRepository;
import com.irtech.billmanager.service.criteria.BillCriteria;
import com.irtech.billmanager.service.dto.BillDTO;
import com.irtech.billmanager.service.mapper.BillMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BillResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BillResourceIT {

    private static final String DEFAULT_PRODUCTS = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCTS = "BBBBBBBBBB";

    private static final String DEFAULT_CUSTOMER = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOMER = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_TOTAL = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL = new BigDecimal(1);
    private static final BigDecimal SMALLER_TOTAL = new BigDecimal(0 - 1);

    private static final String ENTITY_API_URL = "/api/bills";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBillMockMvc;

    private Bill bill;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bill createEntity(EntityManager em) {
        Bill bill = new Bill().products(DEFAULT_PRODUCTS).customer(DEFAULT_CUSTOMER).total(DEFAULT_TOTAL);
        return bill;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bill createUpdatedEntity(EntityManager em) {
        Bill bill = new Bill().products(UPDATED_PRODUCTS).customer(UPDATED_CUSTOMER).total(UPDATED_TOTAL);
        return bill;
    }

    @BeforeEach
    public void initTest() {
        bill = createEntity(em);
    }

    @Test
    @Transactional
    void createBill() throws Exception {
        int databaseSizeBeforeCreate = billRepository.findAll().size();
        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);
        restBillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(billDTO)))
            .andExpect(status().isCreated());

        // Validate the Bill in the database
        List<Bill> billList = billRepository.findAll();
        assertThat(billList).hasSize(databaseSizeBeforeCreate + 1);
        Bill testBill = billList.get(billList.size() - 1);
        assertThat(testBill.getProducts()).isEqualTo(DEFAULT_PRODUCTS);
        assertThat(testBill.getCustomer()).isEqualTo(DEFAULT_CUSTOMER);
        assertThat(testBill.getTotal()).isEqualByComparingTo(DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    void createBillWithExistingId() throws Exception {
        // Create the Bill with an existing ID
        bill.setId(1L);
        BillDTO billDTO = billMapper.toDto(bill);

        int databaseSizeBeforeCreate = billRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBillMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(billDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Bill in the database
        List<Bill> billList = billRepository.findAll();
        assertThat(billList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBills() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList
        restBillMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bill.getId().intValue())))
            .andExpect(jsonPath("$.[*].products").value(hasItem(DEFAULT_PRODUCTS)))
            .andExpect(jsonPath("$.[*].customer").value(hasItem(DEFAULT_CUSTOMER)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))));
    }

    @Test
    @Transactional
    void getBill() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get the bill
        restBillMockMvc
            .perform(get(ENTITY_API_URL_ID, bill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bill.getId().intValue()))
            .andExpect(jsonPath("$.products").value(DEFAULT_PRODUCTS))
            .andExpect(jsonPath("$.customer").value(DEFAULT_CUSTOMER))
            .andExpect(jsonPath("$.total").value(sameNumber(DEFAULT_TOTAL)));
    }

    @Test
    @Transactional
    void getBillsByIdFiltering() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        Long id = bill.getId();

        defaultBillShouldBeFound("id.equals=" + id);
        defaultBillShouldNotBeFound("id.notEquals=" + id);

        defaultBillShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBillShouldNotBeFound("id.greaterThan=" + id);

        defaultBillShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBillShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBillsByProductsIsEqualToSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where products equals to DEFAULT_PRODUCTS
        defaultBillShouldBeFound("products.equals=" + DEFAULT_PRODUCTS);

        // Get all the billList where products equals to UPDATED_PRODUCTS
        defaultBillShouldNotBeFound("products.equals=" + UPDATED_PRODUCTS);
    }

    @Test
    @Transactional
    void getAllBillsByProductsIsNotEqualToSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where products not equals to DEFAULT_PRODUCTS
        defaultBillShouldNotBeFound("products.notEquals=" + DEFAULT_PRODUCTS);

        // Get all the billList where products not equals to UPDATED_PRODUCTS
        defaultBillShouldBeFound("products.notEquals=" + UPDATED_PRODUCTS);
    }

    @Test
    @Transactional
    void getAllBillsByProductsIsInShouldWork() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where products in DEFAULT_PRODUCTS or UPDATED_PRODUCTS
        defaultBillShouldBeFound("products.in=" + DEFAULT_PRODUCTS + "," + UPDATED_PRODUCTS);

        // Get all the billList where products equals to UPDATED_PRODUCTS
        defaultBillShouldNotBeFound("products.in=" + UPDATED_PRODUCTS);
    }

    @Test
    @Transactional
    void getAllBillsByProductsIsNullOrNotNull() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where products is not null
        defaultBillShouldBeFound("products.specified=true");

        // Get all the billList where products is null
        defaultBillShouldNotBeFound("products.specified=false");
    }

    @Test
    @Transactional
    void getAllBillsByProductsContainsSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where products contains DEFAULT_PRODUCTS
        defaultBillShouldBeFound("products.contains=" + DEFAULT_PRODUCTS);

        // Get all the billList where products contains UPDATED_PRODUCTS
        defaultBillShouldNotBeFound("products.contains=" + UPDATED_PRODUCTS);
    }

    @Test
    @Transactional
    void getAllBillsByProductsNotContainsSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where products does not contain DEFAULT_PRODUCTS
        defaultBillShouldNotBeFound("products.doesNotContain=" + DEFAULT_PRODUCTS);

        // Get all the billList where products does not contain UPDATED_PRODUCTS
        defaultBillShouldBeFound("products.doesNotContain=" + UPDATED_PRODUCTS);
    }

    @Test
    @Transactional
    void getAllBillsByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where customer equals to DEFAULT_CUSTOMER
        defaultBillShouldBeFound("customer.equals=" + DEFAULT_CUSTOMER);

        // Get all the billList where customer equals to UPDATED_CUSTOMER
        defaultBillShouldNotBeFound("customer.equals=" + UPDATED_CUSTOMER);
    }

    @Test
    @Transactional
    void getAllBillsByCustomerIsNotEqualToSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where customer not equals to DEFAULT_CUSTOMER
        defaultBillShouldNotBeFound("customer.notEquals=" + DEFAULT_CUSTOMER);

        // Get all the billList where customer not equals to UPDATED_CUSTOMER
        defaultBillShouldBeFound("customer.notEquals=" + UPDATED_CUSTOMER);
    }

    @Test
    @Transactional
    void getAllBillsByCustomerIsInShouldWork() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where customer in DEFAULT_CUSTOMER or UPDATED_CUSTOMER
        defaultBillShouldBeFound("customer.in=" + DEFAULT_CUSTOMER + "," + UPDATED_CUSTOMER);

        // Get all the billList where customer equals to UPDATED_CUSTOMER
        defaultBillShouldNotBeFound("customer.in=" + UPDATED_CUSTOMER);
    }

    @Test
    @Transactional
    void getAllBillsByCustomerIsNullOrNotNull() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where customer is not null
        defaultBillShouldBeFound("customer.specified=true");

        // Get all the billList where customer is null
        defaultBillShouldNotBeFound("customer.specified=false");
    }

    @Test
    @Transactional
    void getAllBillsByCustomerContainsSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where customer contains DEFAULT_CUSTOMER
        defaultBillShouldBeFound("customer.contains=" + DEFAULT_CUSTOMER);

        // Get all the billList where customer contains UPDATED_CUSTOMER
        defaultBillShouldNotBeFound("customer.contains=" + UPDATED_CUSTOMER);
    }

    @Test
    @Transactional
    void getAllBillsByCustomerNotContainsSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where customer does not contain DEFAULT_CUSTOMER
        defaultBillShouldNotBeFound("customer.doesNotContain=" + DEFAULT_CUSTOMER);

        // Get all the billList where customer does not contain UPDATED_CUSTOMER
        defaultBillShouldBeFound("customer.doesNotContain=" + UPDATED_CUSTOMER);
    }

    @Test
    @Transactional
    void getAllBillsByTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where total equals to DEFAULT_TOTAL
        defaultBillShouldBeFound("total.equals=" + DEFAULT_TOTAL);

        // Get all the billList where total equals to UPDATED_TOTAL
        defaultBillShouldNotBeFound("total.equals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllBillsByTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where total not equals to DEFAULT_TOTAL
        defaultBillShouldNotBeFound("total.notEquals=" + DEFAULT_TOTAL);

        // Get all the billList where total not equals to UPDATED_TOTAL
        defaultBillShouldBeFound("total.notEquals=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllBillsByTotalIsInShouldWork() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where total in DEFAULT_TOTAL or UPDATED_TOTAL
        defaultBillShouldBeFound("total.in=" + DEFAULT_TOTAL + "," + UPDATED_TOTAL);

        // Get all the billList where total equals to UPDATED_TOTAL
        defaultBillShouldNotBeFound("total.in=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllBillsByTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where total is not null
        defaultBillShouldBeFound("total.specified=true");

        // Get all the billList where total is null
        defaultBillShouldNotBeFound("total.specified=false");
    }

    @Test
    @Transactional
    void getAllBillsByTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where total is greater than or equal to DEFAULT_TOTAL
        defaultBillShouldBeFound("total.greaterThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the billList where total is greater than or equal to UPDATED_TOTAL
        defaultBillShouldNotBeFound("total.greaterThanOrEqual=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllBillsByTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where total is less than or equal to DEFAULT_TOTAL
        defaultBillShouldBeFound("total.lessThanOrEqual=" + DEFAULT_TOTAL);

        // Get all the billList where total is less than or equal to SMALLER_TOTAL
        defaultBillShouldNotBeFound("total.lessThanOrEqual=" + SMALLER_TOTAL);
    }

    @Test
    @Transactional
    void getAllBillsByTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where total is less than DEFAULT_TOTAL
        defaultBillShouldNotBeFound("total.lessThan=" + DEFAULT_TOTAL);

        // Get all the billList where total is less than UPDATED_TOTAL
        defaultBillShouldBeFound("total.lessThan=" + UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void getAllBillsByTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        // Get all the billList where total is greater than DEFAULT_TOTAL
        defaultBillShouldNotBeFound("total.greaterThan=" + DEFAULT_TOTAL);

        // Get all the billList where total is greater than SMALLER_TOTAL
        defaultBillShouldBeFound("total.greaterThan=" + SMALLER_TOTAL);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBillShouldBeFound(String filter) throws Exception {
        restBillMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bill.getId().intValue())))
            .andExpect(jsonPath("$.[*].products").value(hasItem(DEFAULT_PRODUCTS)))
            .andExpect(jsonPath("$.[*].customer").value(hasItem(DEFAULT_CUSTOMER)))
            .andExpect(jsonPath("$.[*].total").value(hasItem(sameNumber(DEFAULT_TOTAL))));

        // Check, that the count call also returns 1
        restBillMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBillShouldNotBeFound(String filter) throws Exception {
        restBillMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBillMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBill() throws Exception {
        // Get the bill
        restBillMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBill() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        int databaseSizeBeforeUpdate = billRepository.findAll().size();

        // Update the bill
        Bill updatedBill = billRepository.findById(bill.getId()).get();
        // Disconnect from session so that the updates on updatedBill are not directly saved in db
        em.detach(updatedBill);
        updatedBill.products(UPDATED_PRODUCTS).customer(UPDATED_CUSTOMER).total(UPDATED_TOTAL);
        BillDTO billDTO = billMapper.toDto(updatedBill);

        restBillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, billDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(billDTO))
            )
            .andExpect(status().isOk());

        // Validate the Bill in the database
        List<Bill> billList = billRepository.findAll();
        assertThat(billList).hasSize(databaseSizeBeforeUpdate);
        Bill testBill = billList.get(billList.size() - 1);
        assertThat(testBill.getProducts()).isEqualTo(UPDATED_PRODUCTS);
        assertThat(testBill.getCustomer()).isEqualTo(UPDATED_CUSTOMER);
        assertThat(testBill.getTotal()).isEqualTo(UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void putNonExistingBill() throws Exception {
        int databaseSizeBeforeUpdate = billRepository.findAll().size();
        bill.setId(count.incrementAndGet());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, billDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(billDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bill in the database
        List<Bill> billList = billRepository.findAll();
        assertThat(billList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBill() throws Exception {
        int databaseSizeBeforeUpdate = billRepository.findAll().size();
        bill.setId(count.incrementAndGet());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBillMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(billDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bill in the database
        List<Bill> billList = billRepository.findAll();
        assertThat(billList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBill() throws Exception {
        int databaseSizeBeforeUpdate = billRepository.findAll().size();
        bill.setId(count.incrementAndGet());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBillMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(billDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bill in the database
        List<Bill> billList = billRepository.findAll();
        assertThat(billList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBillWithPatch() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        int databaseSizeBeforeUpdate = billRepository.findAll().size();

        // Update the bill using partial update
        Bill partialUpdatedBill = new Bill();
        partialUpdatedBill.setId(bill.getId());

        restBillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBill.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBill))
            )
            .andExpect(status().isOk());

        // Validate the Bill in the database
        List<Bill> billList = billRepository.findAll();
        assertThat(billList).hasSize(databaseSizeBeforeUpdate);
        Bill testBill = billList.get(billList.size() - 1);
        assertThat(testBill.getProducts()).isEqualTo(DEFAULT_PRODUCTS);
        assertThat(testBill.getCustomer()).isEqualTo(DEFAULT_CUSTOMER);
        assertThat(testBill.getTotal()).isEqualByComparingTo(DEFAULT_TOTAL);
    }

    @Test
    @Transactional
    void fullUpdateBillWithPatch() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        int databaseSizeBeforeUpdate = billRepository.findAll().size();

        // Update the bill using partial update
        Bill partialUpdatedBill = new Bill();
        partialUpdatedBill.setId(bill.getId());

        partialUpdatedBill.products(UPDATED_PRODUCTS).customer(UPDATED_CUSTOMER).total(UPDATED_TOTAL);

        restBillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBill.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBill))
            )
            .andExpect(status().isOk());

        // Validate the Bill in the database
        List<Bill> billList = billRepository.findAll();
        assertThat(billList).hasSize(databaseSizeBeforeUpdate);
        Bill testBill = billList.get(billList.size() - 1);
        assertThat(testBill.getProducts()).isEqualTo(UPDATED_PRODUCTS);
        assertThat(testBill.getCustomer()).isEqualTo(UPDATED_CUSTOMER);
        assertThat(testBill.getTotal()).isEqualByComparingTo(UPDATED_TOTAL);
    }

    @Test
    @Transactional
    void patchNonExistingBill() throws Exception {
        int databaseSizeBeforeUpdate = billRepository.findAll().size();
        bill.setId(count.incrementAndGet());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, billDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(billDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bill in the database
        List<Bill> billList = billRepository.findAll();
        assertThat(billList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBill() throws Exception {
        int databaseSizeBeforeUpdate = billRepository.findAll().size();
        bill.setId(count.incrementAndGet());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBillMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(billDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bill in the database
        List<Bill> billList = billRepository.findAll();
        assertThat(billList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBill() throws Exception {
        int databaseSizeBeforeUpdate = billRepository.findAll().size();
        bill.setId(count.incrementAndGet());

        // Create the Bill
        BillDTO billDTO = billMapper.toDto(bill);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBillMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(billDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bill in the database
        List<Bill> billList = billRepository.findAll();
        assertThat(billList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBill() throws Exception {
        // Initialize the database
        billRepository.saveAndFlush(bill);

        int databaseSizeBeforeDelete = billRepository.findAll().size();

        // Delete the bill
        restBillMockMvc
            .perform(delete(ENTITY_API_URL_ID, bill.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Bill> billList = billRepository.findAll();
        assertThat(billList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
