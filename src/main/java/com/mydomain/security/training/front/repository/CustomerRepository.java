package com.mydomain.security.training.front.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.mydomain.security.training.front.entity.Customer;

@Repository
@Transactional
public class CustomerRepository {

	@PersistenceContext
	private EntityManager em;
	
	public List<Customer>getCustomerByName(String name){
		
		String query="select c from Customer c where c.name='"+name+"'";
		Query q=em.createQuery(query);
		return q.getResultList();
	}
}
