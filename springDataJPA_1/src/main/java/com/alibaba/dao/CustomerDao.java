package com.alibaba.dao;

import com.alibaba.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerDao extends JpaRepository<Customer,Long>,JpaSpecificationExecutor<Customer> {

    @Query(value = "from Customer")
    public List<Customer> findAllCustomer();

    @Query(value = "from Customer where custName=?1")
    public Customer findCustomer(String custName);

    //使用@modifying 标识该操作为更新操作
    @Query(value = "update Customer set custName=?1 where custId=?2")
    @Modifying
    public void updateCustomer(String custName,Long custId);

    @Query(value = "select * from customer",nativeQuery = true)
    public List<Customer> findSql();

    public Customer findByCustName(String custName);
}
