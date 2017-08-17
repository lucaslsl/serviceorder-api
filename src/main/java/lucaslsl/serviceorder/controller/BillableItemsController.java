/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.controller;

import java.util.List;
import lucaslsl.serviceorder.model.BillableItem;
import lucaslsl.serviceorder.repository.BillableItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class BillableItemsController {
    
    @Autowired
    BillableItemRepository billableItemRepository;
    
    @GetMapping("api/v1/billable_items/{billableItemId}")
    public ResponseEntity<BillableItem> retrieve(@PathVariable("billableItemId") Long id) {
        BillableItem billableItem = billableItemRepository.findOne(id);
        if(billableItem==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<BillableItem>(billableItem, HttpStatus.OK);
    }
    
    @GetMapping("api/v1/billable_items")
    public ResponseEntity<List<BillableItem>> list(
            Pageable p, 
            @RequestParam(value = "deleted", required = false) Boolean deleted) {
        Page<BillableItem> resultPage;
        if(deleted != null){
            resultPage = billableItemRepository.findByDeleted(p, deleted);
        }else{
            resultPage = billableItemRepository.findAllByOrderByIdDesc(p);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Total-Count", String.valueOf(resultPage.getTotalElements()));
        return new ResponseEntity<List<BillableItem>>(resultPage.getContent(), headers, HttpStatus.OK);
    }
    
    @PostMapping("api/v1/billable_items")
    public ResponseEntity<BillableItem> create(@RequestBody BillableItem billableItem, UriComponentsBuilder builder) {
        billableItem.setDeleted(Boolean.FALSE);
        BillableItem billableItemCreated = billableItemRepository.save(billableItem);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/api/v1/billable_items/{billableItemId}").buildAndExpand(billableItemCreated.getId()).toUri());
        return new ResponseEntity<BillableItem>(billableItemCreated, headers, HttpStatus.CREATED);
            
    }
    
    @PutMapping("api/v1/billable_items/{billableItemId}")
    public ResponseEntity<BillableItem> update(@PathVariable("billableItemId") Long id, @RequestBody BillableItem billableItem) {
        BillableItem billableItemFound = billableItemRepository.findOne(id);
        if(billableItemFound==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        billableItem.setId(billableItemFound.getId());
        BillableItem billableItemSaved =  billableItemRepository.save(billableItem);
        return new ResponseEntity<BillableItem>(billableItemSaved, HttpStatus.OK);
    }
    
    @DeleteMapping("api/v1/billable_items/{billableItemId}")
    public ResponseEntity<Void> delete(@PathVariable("billableItemId") Long id) {
        BillableItem billableItem = billableItemRepository.findOne(id);
        if(billableItem==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        billableItem.setDeleted(Boolean.TRUE);
        billableItemRepository.save(billableItem);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
}
