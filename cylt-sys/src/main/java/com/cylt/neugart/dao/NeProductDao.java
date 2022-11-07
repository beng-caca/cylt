package com.cylt.neugart.dao;

import com.cylt.pojo.neugart.NeProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("neProductDao")
public interface NeProductDao extends JpaRepository<NeProduct, String> {
}
