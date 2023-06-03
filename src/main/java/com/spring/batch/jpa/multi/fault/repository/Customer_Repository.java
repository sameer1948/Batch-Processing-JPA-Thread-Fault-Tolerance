package com.spring.batch.jpa.multi.fault.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.batch.jpa.multi.fault.entity.Customer;

public interface Customer_Repository extends JpaRepository<Customer, Integer>{

}
