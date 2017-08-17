/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.controller;

import java.util.List;
import lucaslsl.serviceorder.model.Budget;
import lucaslsl.serviceorder.model.Order;
import lucaslsl.serviceorder.repository.BudgetRepository;
import lucaslsl.serviceorder.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author lucaslsl
 */
@RestController
@CrossOrigin
@EnableTransactionManagement
public class OrdersController {
    
    @Autowired
    OrderRepository orderRepository;
    
    @Autowired
    BudgetRepository budgetRepository;
    
    @GetMapping("api/v1/orders/{orderId}")
    public ResponseEntity<Order> retrieve(@PathVariable("orderId") Long id) {
        Order order = orderRepository.findOne(id);
        if(order==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Order>(order, HttpStatus.OK);
    }
    
    @GetMapping("api/v1/orders")
    public ResponseEntity<List<Order>> list(
            Pageable p,
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestParam(value = "budgetId", required = false) Long budgetId) {
        
        Page<Order> resultPage;
        
        if(clientId != null){
            resultPage = orderRepository.findByClientIdOrderByIdDesc(p, clientId);
        }else{
            resultPage = orderRepository.findAllByOrderByIdDesc(p);
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Total-Count", String.valueOf(resultPage.getTotalElements()));
        return new ResponseEntity<List<Order>>(resultPage.getContent(), headers, HttpStatus.OK);
    }
    
    @PostMapping("api/v1/orders")
    public ResponseEntity<Order> create(@RequestBody Order order, UriComponentsBuilder builder) {
        
        
        Budget budget = budgetRepository.findOne(order.getBudgetId());
        if(budget==null){
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
        Order orderFound = orderRepository.findOneByBudgetId(budget.getId());
        if(orderFound != null){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        
        order.setClientId(budget.getClientId());
        order.setSubtotal(budget.getTotal());
                
        Order orderCreated = orderRepository.save(order);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/api/v1/orders/{orderId}").buildAndExpand(orderCreated.getId()).toUri());
        return new ResponseEntity<Order>(orderCreated, headers, HttpStatus.CREATED);
            
    }
    
}
