/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.repository;

import lucaslsl.serviceorder.model.AdditionalInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author lucaslsl
 */
public interface AdditionalInformationRepository extends PagingAndSortingRepository<AdditionalInformation, Long> {
    
    Page<AdditionalInformation> findByParentTypeAndParentId(Pageable pageable, String parentType, Long parentId);
    
    AdditionalInformation findOneByIdAndParentTypeAndParentId(Long id, String parentType, Long parentId);
    
}
