/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.repository;

import java.util.List;
import lucaslsl.serviceorder.model.BudgetItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author lucaslsl
 */
public interface BudgetItemRepository extends PagingAndSortingRepository<BudgetItem, Long> {
    
    Page<BudgetItem> findAllByOrderByIdDesc(Pageable pageable);
    
    List<BudgetItem> findByBudgetIdOrderByIdDesc(Long budgetId);
        
    List<BudgetItem> findByBudgetIdAndApprovedOrderByIdDesc(Long budgetId, Boolean approved);
    
    Page<BudgetItem> findByBudgetIdOrderByIdDesc(Pageable pageable, Long budgetId);
    
    Page<BudgetItem> findByIdAndBudgetIdOrderByIdDesc(Pageable pageable, Long id, Long budgetId);
    
    BudgetItem findOneByIdAndBudgetId(Long id, Long budgetId);
    
    
}
