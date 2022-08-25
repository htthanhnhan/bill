package com.irtech.billmanager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BigDecimalFilter;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.irtech.billmanager.domain.Bill} entity. This class is used
 * in {@link com.irtech.billmanager.web.rest.BillResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /bills?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BillCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter products;

    private StringFilter customer;

    private BigDecimalFilter total;

    public BillCriteria() {}

    public BillCriteria(BillCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.products = other.products == null ? null : other.products.copy();
        this.customer = other.customer == null ? null : other.customer.copy();
        this.total = other.total == null ? null : other.total.copy();
    }

    @Override
    public BillCriteria copy() {
        return new BillCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getProducts() {
        return products;
    }

    public StringFilter products() {
        if (products == null) {
            products = new StringFilter();
        }
        return products;
    }

    public void setProducts(StringFilter products) {
        this.products = products;
    }

    public StringFilter getCustomer() {
        return customer;
    }

    public StringFilter customer() {
        if (customer == null) {
            customer = new StringFilter();
        }
        return customer;
    }

    public void setCustomer(StringFilter customer) {
        this.customer = customer;
    }

    public BigDecimalFilter getTotal() {
        return total;
    }

    public BigDecimalFilter total() {
        if (total == null) {
            total = new BigDecimalFilter();
        }
        return total;
    }

    public void setTotal(BigDecimalFilter total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BillCriteria that = (BillCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(products, that.products) &&
            Objects.equals(customer, that.customer) &&
            Objects.equals(total, that.total)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, products, customer, total);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BillCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (products != null ? "products=" + products + ", " : "") +
            (customer != null ? "customer=" + customer + ", " : "") +
            (total != null ? "total=" + total + ", " : "") +
            "}";
    }
}
