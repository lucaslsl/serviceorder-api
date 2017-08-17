/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.repository;

import lucaslsl.serviceorder.model.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author lucaslsl
 */
public interface BudgetRepository extends PagingAndSortingRepository<Budget, Long> {
    
    Page<Budget> findAllByOrderByIdDesc(Pageable pageable);
    
    Page<Budget> findOneByDeleted(Pageable pageable, Boolean deleted);
    
    Page<Budget> findByDeletedOrderByIdDesc(Pageable pageable, Boolean deleted);
    
    Page<Budget> findByClosedOrderByIdDesc(Pageable pageable, Boolean closed);
    
    Page<Budget> findByDeletedAndClosedOrderByIdDesc(Pageable pageable, Boolean deleted, Boolean closed);
    
    Page<Budget> findByCustomerIdOrderByIdDesc(Pageable pageable, Long customerId);
    
    Page<Budget> findByCustomerIdAndClosedOrderByIdDesc(Pageable pageable, Long customerId, Boolean closed);
    
    Page<Budget> findByCustomerIdAndDeletedOrderByIdDesc(Pageable pageable, Long customerId, Boolean deleted);
    
    Page<Budget> findByCustomerIdAndDeletedAndClosedOrderByIdDesc(Pageable pageable, Long customerId, Boolean deleted, Boolean closed);
    
}
