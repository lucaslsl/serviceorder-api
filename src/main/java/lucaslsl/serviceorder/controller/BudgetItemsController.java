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
import lucaslsl.serviceorder.model.Client;
import lucaslsl.serviceorder.repository.BillableItemRepository;
import lucaslsl.serviceorder.repository.BudgetItemRepository;
import lucaslsl.serviceorder.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author lucaslsl
 */
@RestController
@CrossOrigin
@EnableTransactionManagement
public class BudgetItemsController {
    
    @Autowired
    BudgetItemRepository budgetItemRepository;
    
    @Autowired
    BudgetRepository budgetRepository;
    
    @Autowired
    BillableItemRepository billableItemRepository;
    
    @GetMapping("api/v1/budgets/{budgetId}/items/{itemId}")
    public ResponseEntity<BudgetItem> retrieve(@PathVariable("budgetId") Long budgetId, @PathVariable("itemId") Long itemId) {
        BudgetItem budgetItem = budgetItemRepository.findOneByIdAndBudgetId(itemId, budgetId);
        if(budgetItem==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<BudgetItem>(budgetItem, HttpStatus.OK);
    }
    
    @GetMapping("api/v1/budgets/{budgetId}/items")
    public ResponseEntity<List<BudgetItem>> list(Pageable p, @PathVariable("budgetId") Long budgetId) {
        Page<BudgetItem> resultPage = budgetItemRepository.findByBudgetIdOrderByIdDesc(p, budgetId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Total-Count", String.valueOf(resultPage.getTotalElements()));
        return new ResponseEntity<List<BudgetItem>>(resultPage.getContent(), headers, HttpStatus.OK);
    }
    
    @Transactional
    @PostMapping("api/v1/budgets/{budgetId}/items")
    public ResponseEntity<BudgetItem> create(@RequestBody BudgetItem budgetItem, @PathVariable("budgetId") Long budgetId, UriComponentsBuilder builder) {
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
    public ResponseEntity<BudgetItem> update(@PathVariable("budgetId") Long budgetId, @PathVariable("itemId") Long itemId, @RequestBody BudgetItem budgetItem) {
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
    public ResponseEntity<Void> approve(@PathVariable("budgetId") Long budgetId, @PathVariable("itemId") Long itemId) {
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
    public ResponseEntity<Void> delete(@PathVariable("budgetId") Long budgetId, @PathVariable("itemId") Long itemId) {
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
