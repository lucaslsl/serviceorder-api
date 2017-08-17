/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.repository;

import lucaslsl.serviceorder.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author lucaslsl
 */
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {
    
    Order findOneByBudgetId(Long budgetId);
    
    Page<Order> findAllByOrderByIdDesc(Pageable pageable);
    
    Page<Order> findByBudgetId(Pageable pageable, Long budgetId);
    
    Page<Order> findByBudgetIdOrderByIdDesc(Pageable pageable, Long budgetId);
    
    Page<Order> findByCustomerIdOrderByIdDesc(Pageable pageable, Long customerId);
    
}
