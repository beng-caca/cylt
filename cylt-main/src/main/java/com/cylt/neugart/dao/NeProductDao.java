package com.cylt.neugart.dao;

import com.cylt.pojo.neugart.NeProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 产品dao
 */
@Repository("neProductDao")
public interface NeProductDao extends JpaRepository<NeProduct, String> {
}
