/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.repository;

import lucaslsl.serviceorder.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author lucaslsl
 */
public interface ClientRepository extends PagingAndSortingRepository<Client, Long> {
    
    Page<Client> findAllByOrderByIdDesc(Pageable pageable);
    
    Client findOneByNationalRegistrationNumber(String nationalRegistrationNumber);
    
    Page<Client> findByNationalRegistrationNumber(Pageable pageable, String nationalRegistrationNumber);
    
    Page<Client> findByDeletedOrderByIdDesc(Pageable pageable, Boolean deleted);
    
    Page<Client> findByJuridicalOrderByIdDesc(Pageable pageable, Boolean juridical);
    
    Page<Client> findByDeletedAndJuridicalOrderByIdDesc(Pageable pageable, Boolean deleted, Boolean juridical);
    
}
