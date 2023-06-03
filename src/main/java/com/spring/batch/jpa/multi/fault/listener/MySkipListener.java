package com.spring.batch.jpa.multi.fault.listener;

import com.spring.batch.jpa.multi.fault.entity.Customer;

public class MySkipListener implements org.springframework.batch.core.SkipListener<Customer, Object>{
	
	@Override
	public void onSkipInProcess(Customer item, Throwable t) {
		System.out.println(item.toString()+"   --->"+t);
		
	}
	@Override
	public void onSkipInRead(Throwable t) {
		System.out.println(t);
		
	}
	@Override
	public void onSkipInWrite(Object item, Throwable t) {
		System.out.println(item.toString()+"  w --->"+t);
		
	}

}
