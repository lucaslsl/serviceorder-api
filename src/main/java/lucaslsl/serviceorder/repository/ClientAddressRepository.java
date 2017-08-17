/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.repository;

import lucaslsl.serviceorder.model.ClientAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author lucaslsl
 */
public interface ClientAddressRepository extends PagingAndSortingRepository<ClientAddress, Long>{
    
    Page<ClientAddress> findByClientIdOrderByIdDesc(Pageable pageable, Long clientId);
    
}
