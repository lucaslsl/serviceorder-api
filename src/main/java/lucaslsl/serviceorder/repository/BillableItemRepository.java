/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.repository;

import lucaslsl.serviceorder.model.BillableItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author lucaslsl
 */
public interface BillableItemRepository extends PagingAndSortingRepository<BillableItem, Long> {
    
    Page<BillableItem> findAllByOrderByIdDesc(Pageable pageable);
    
    Page<BillableItem> findOneByDeleted(Pageable pageable, Boolean deleted);
    
    Page<BillableItem> findByDeleted(Pageable pageable, Boolean deleted);
    
}
