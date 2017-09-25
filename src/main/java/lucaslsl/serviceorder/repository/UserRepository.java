/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.repository;

import lucaslsl.serviceorder.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author lucaslsl
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Page<User> findAllByOrderByIdDesc(Pageable pageable);
    
    Page<User> findByDeletedOrderByIdDesc(Pageable pageable, Boolean deleted);
    
    Page<User> findByUsernameOrderByIdDesc(Pageable pageable, String username);
    
    User findOneByUsername(String username);
}
