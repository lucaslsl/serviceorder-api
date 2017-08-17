/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.repository;

import lucaslsl.serviceorder.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author lucaslsl
 */
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {
    
    Page<Customer> findAllByOrderByIdDesc(Pageable pageable);
    
    Customer findOneByNationalRegistrationNumber(String nationalRegistrationNumber);
    
    Page<Customer> findByNationalRegistrationNumber(Pageable pageable, String nationalRegistrationNumber);
    
    Page<Customer> findByDeletedOrderByIdDesc(Pageable pageable, Boolean deleted);
    
    Page<Customer> findByJuridicalOrderByIdDesc(Pageable pageable, Boolean juridical);
    
    Page<Customer> findByDeletedAndJuridicalOrderByIdDesc(Pageable pageable, Boolean deleted, Boolean juridical);
    
}
