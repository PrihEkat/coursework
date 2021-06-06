package com.coursework.repository;

import com.coursework.model.Counterparty;

import java.util.List;

/**Repository for the "Counterparty" entity.*/
public interface CounterpartyRepository extends CommonRepository<Counterparty> {
    public List<Counterparty> findByName(String name);
}