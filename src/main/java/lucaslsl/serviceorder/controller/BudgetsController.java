/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.controller;

import java.math.BigDecimal;
import java.util.List;
import javax.transaction.Transactional;
import lucaslsl.serviceorder.model.BillableItem;
import lucaslsl.serviceorder.model.Budget;
import lucaslsl.serviceorder.model.BudgetItem;
import lucaslsl.serviceorder.model.Customer;
import lucaslsl.serviceorder.repository.BillableItemRepository;
import lucaslsl.serviceorder.repository.BudgetItemRepository;
import lucaslsl.serviceorder.repository.BudgetRepository;
import lucaslsl.serviceorder.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
public class BudgetsController {
    
    @Autowired
    BudgetRepository budgetRepository;
    
    @Autowired
    BudgetItemRepository budgetItemRepository;
    
    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    BillableItemRepository billableItemRepository;
    
    @Transactional
    @GetMapping("api/v1/budgets/{budgetId}")
    public ResponseEntity<Budget> retrieve(@PathVariable("budgetId") Long id) {
        Budget budget = budgetRepository.findOne(id);
        if(budget==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Budget>(budget, HttpStatus.OK);
    }
    
    @GetMapping("api/v1/budgets")
    public ResponseEntity<List<Budget>> list(
            Pageable p, 
            @RequestParam(value = "deleted", required = false) Boolean deleted,
            @RequestParam(value = "closed", required = false) Boolean closed,
            @RequestParam(value = "customer_id", required = false) Long customerId) {
        Page<Budget> resultPage;
        if(deleted != null && closed != null && customerId != null){
            resultPage = budgetRepository.findByCustomerIdAndDeletedAndClosedOrderByIdDesc(p, customerId, deleted, closed);
        }else if(deleted != null && closed != null && customerId == null){
            resultPage = budgetRepository.findByDeletedAndClosedOrderByIdDesc(p, deleted, closed);
        }else if(deleted == null && closed != null && customerId == null){
            resultPage = budgetRepository.findByClosedOrderByIdDesc(p, closed);
        }else if(deleted != null && closed == null && customerId == null){
            resultPage = budgetRepository.findByDeletedOrderByIdDesc(p, deleted);
        }else if(deleted != null && closed == null && customerId != null){
            resultPage = budgetRepository.findByCustomerIdAndDeletedOrderByIdDesc(p, customerId, deleted);
        }else if(deleted == null && closed != null && customerId != null){
            resultPage = budgetRepository.findByCustomerIdAndClosedOrderByIdDesc(p, customerId, closed);
        }else{
            resultPage = budgetRepository.findAllByOrderByIdDesc(p);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Total-Count", String.valueOf(resultPage.getTotalElements()));
        return new ResponseEntity<List<Budget>>(resultPage.getContent(), headers, HttpStatus.OK);
    }
    
    @PostMapping("api/v1/budgets")
    public ResponseEntity<Budget> create(@RequestBody Budget budget, UriComponentsBuilder builder) {
        Customer customer = customerRepository.findOne(budget.getCustomerId());
        if(customer==null){
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
        budget.setDeleted(Boolean.FALSE);
        budget.setClosed(Boolean.FALSE);
        budget.setTotal(BigDecimal.ZERO);
        Budget budgetCreated = budgetRepository.save(budget);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/api/v1/budgets/{budgetId}").buildAndExpand(budgetCreated.getId()).toUri());
        return new ResponseEntity<Budget>(budgetCreated, headers, HttpStatus.CREATED);
            
    }
    
    @Transactional
    @PutMapping("api/v1/budgets/{budgetId}/close")
    public ResponseEntity<Void> update(@PathVariable("budgetId") Long id) {
        Budget budget = budgetRepository.findOne(id);
        if(budget==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        if(budget.getClosed()){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        
        BigDecimal total = BigDecimal.ZERO;
        
        List<BudgetItem> items = budgetItemRepository.findByBudgetIdAndApprovedOrderByIdDesc(id, true);
        for(BudgetItem item: items){
            BigDecimal itemSubtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            itemSubtotal = itemSubtotal.multiply(BigDecimal.valueOf(1).subtract(item.getDiscount()));
            total = total.add(itemSubtotal);
        }
        
        budget.setTotal(total);
        budget.setClosed(Boolean.TRUE);
        budgetRepository.save(budget);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
    @DeleteMapping("api/v1/budgets/{budgetId}")
    public ResponseEntity<Void> delete(@PathVariable("budgetId") Long id) {
        Budget budget = budgetRepository.findOne(id);
        if(budget==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        budget.setDeleted(Boolean.TRUE);
        budgetRepository.save(budget);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("api/v1/budgets/{budgetId}/items/{itemId}")
    public ResponseEntity<BudgetItem> retrieveItem(@PathVariable("budgetId") Long budgetId, @PathVariable("itemId") Long itemId) {
        BudgetItem budgetItem = budgetItemRepository.findOneByIdAndBudgetId(itemId, budgetId);
        if(budgetItem==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<BudgetItem>(budgetItem, HttpStatus.OK);
    }
    
    @GetMapping("api/v1/budgets/{budgetId}/items")
    public ResponseEntity<List<BudgetItem>> listItems(Pageable p, @PathVariable("budgetId") Long budgetId) {
        Page<BudgetItem> resultPage = budgetItemRepository.findByBudgetIdOrderByIdDesc(p, budgetId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Total-Count", String.valueOf(resultPage.getTotalElements()));
        return new ResponseEntity<List<BudgetItem>>(resultPage.getContent(), headers, HttpStatus.OK);
    }
    
    @Transactional
    @PostMapping("api/v1/budgets/{budgetId}/items")
    public ResponseEntity<BudgetItem> createItem(@RequestBody BudgetItem budgetItem, @PathVariable("budgetId") Long budgetId, UriComponentsBuilder builder) {
        Budget budget = budgetRepository.findOne(budgetId);
        if(budget==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        if(budget.getClosed()){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        
        BillableItem billableItem = billableItemRepository.findOne(budgetItem.getBillableItemId());
        if(billableItem==null){
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
        budgetItem.setApproved(Boolean.FALSE);
        budgetItem.setBudgetId(budget.getId());
        budgetItem.setPrice(billableItem.getPrice());
        
        BudgetItem budgetItemCreated = budgetItemRepository.save(budgetItem);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("api/v1/budgets/{budgetId}/items/{itemId}").buildAndExpand(budget.getId(), budgetItemCreated.getId()).toUri());
        return new ResponseEntity<BudgetItem>(budgetItemCreated, headers, HttpStatus.CREATED);
            
    }
    
    @Transactional
    @PutMapping("api/v1/budgets/{budgetId}/items/{itemId}")
    public ResponseEntity<BudgetItem> updateItem(@PathVariable("budgetId") Long budgetId, @PathVariable("itemId") Long itemId, @RequestBody BudgetItem budgetItem) {
        Budget budget = budgetRepository.findOne(budgetId);
        if(budget==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        BudgetItem budgetItemFound = budgetItemRepository.findOne(itemId);
        if(budgetItemFound==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        if(budget.getClosed()){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        
        budgetItemFound.setQuantity(budgetItem.getQuantity());
        budgetItemFound.setDiscount(budgetItem.getDiscount());
        
        BudgetItem budgetItemSaved = budgetItemRepository.save(budgetItemFound);
        return new ResponseEntity<BudgetItem>(budgetItemSaved, HttpStatus.OK);
    }
    
    @Transactional
    @PutMapping("api/v1/budgets/{budgetId}/items/{itemId}/approve")
    public ResponseEntity<Void> approveItem(@PathVariable("budgetId") Long budgetId, @PathVariable("itemId") Long itemId) {
        Budget budget = budgetRepository.findOne(budgetId);
        if(budget==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        BudgetItem budgetItem = budgetItemRepository.findOne(itemId);
        if(budgetItem==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        if(budget.getClosed()){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        
        budgetItem.setApproved(Boolean.TRUE);
        budgetItemRepository.save(budgetItem);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
    @Transactional
    @DeleteMapping("api/v1/budgets/{budgetId}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable("budgetId") Long budgetId, @PathVariable("itemId") Long itemId) {
        Budget budget = budgetRepository.findOne(budgetId);
        if(budget==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        BudgetItem budgetItem = budgetItemRepository.findOne(itemId);
        if(budgetItem==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        if(budget.getClosed()){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        
        budgetItemRepository.delete(budgetItem);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
}
