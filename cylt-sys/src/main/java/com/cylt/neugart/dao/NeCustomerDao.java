package com.cylt.neugart.dao;

import com.cylt.pojo.neugart.NeCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("neCustomerDao")
public interface NeCustomerDao extends JpaRepository<NeCustomer, String> {
}
