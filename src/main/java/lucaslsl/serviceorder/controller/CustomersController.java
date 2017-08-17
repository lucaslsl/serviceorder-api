/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.controller;

import java.util.List;
import lucaslsl.serviceorder.model.AdditionalInformation;
import lucaslsl.serviceorder.model.Customer;
import lucaslsl.serviceorder.model.CustomerAddress;
import lucaslsl.serviceorder.repository.AdditionalInformationRepository;
import lucaslsl.serviceorder.repository.CustomerAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import lucaslsl.serviceorder.repository.CustomerRepository;

/**
 *
 * @author lucaslsl
 */
@RestController
@CrossOrigin
public class CustomersController {
    
    @Autowired
    CustomerRepository customerRepository;
    
    @Autowired
    CustomerAddressRepository customerAddressRepository;
    
    @Autowired
    AdditionalInformationRepository additionalInformationRepository;
    
    @GetMapping("api/v1/customers/{customerId}")
    public ResponseEntity<Customer> retrieve(@PathVariable("customerId") Long id) {
        Customer customer = customerRepository.findOne(id);
        if(customer==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }
    
    @GetMapping("api/v1/customers")
    public ResponseEntity<List<Customer>> list(
            Pageable p,
            @RequestParam(value = "deleted", required = false) Boolean deleted,
            @RequestParam(value = "juridical", required = false) Boolean juridical,
            @RequestParam(value = "national_registration_number", required = false) String nationalRegistrationNumber) {
        
        Page<Customer> resultPage;
        if(deleted != null && juridical != null && nationalRegistrationNumber == null){
            resultPage = customerRepository.findByDeletedAndJuridicalOrderByIdDesc(p, deleted, juridical);
        }else if(deleted == null && juridical != null && nationalRegistrationNumber == null){
            resultPage = customerRepository.findByJuridicalOrderByIdDesc(p, juridical);
        }else if(deleted != null && juridical == null && nationalRegistrationNumber == null){
            resultPage = customerRepository.findByDeletedOrderByIdDesc(p, deleted);
        }else if(nationalRegistrationNumber != null && deleted != null){
            resultPage = customerRepository.findByNationalRegistrationNumberAndDeletedOrderByIdDesc(p, nationalRegistrationNumber, deleted);
        }else{
            resultPage = customerRepository.findAllByOrderByIdDesc(p);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Total-Count", String.valueOf(resultPage.getTotalElements()));
        return new ResponseEntity<List<Customer>>(resultPage.getContent(), headers, HttpStatus.OK);
    }
    
    @PostMapping("api/v1/customers")
    public ResponseEntity<Customer> create(@RequestBody Customer customer, UriComponentsBuilder builder) {
        
        Customer customerFound = customerRepository.findOneByNationalRegistrationNumber(customer.getNationalRegistrationNumber());
        if(customerFound != null){
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
        customer.setDeleted(Boolean.FALSE);
        Customer customerCreated = customerRepository.save(customer);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/api/v1/customers/{customerId}").buildAndExpand(customerCreated.getId()).toUri());
        return new ResponseEntity<Customer>(customerCreated, headers, HttpStatus.CREATED);
            
    }
    
    @PutMapping("api/v1/customers/{customerId}")
    public ResponseEntity<Customer> update(@PathVariable("customerId") Long id, @RequestBody Customer customer) {
        Customer customerFound = customerRepository.findOne(id);
        if(customerFound==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        customer.setId(customerFound.getId());
        Customer customerSaved = customerRepository.save(customer);
        return new ResponseEntity<Customer>(customerSaved, HttpStatus.OK);
    }
    
    @DeleteMapping("api/v1/customers/{customerId}")
    public ResponseEntity<Void> delete(@PathVariable("customerId") Long id) {
        Customer customer = customerRepository.findOne(id);
        if(customer==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        customer.setDeleted(Boolean.TRUE);
        customerRepository.save(customer);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("api/v1/customers/{customerId}/addresses/{addressId}")
    public ResponseEntity<CustomerAddress> retrieveAddress(@PathVariable("customerId") Long customerId, @PathVariable("addressId") Long addressId) {
        CustomerAddress customerAddress = customerAddressRepository.findOneByIdAndCustomerId(addressId, customerId);
        if(customerAddress==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<CustomerAddress>(customerAddress, HttpStatus.OK);
    }
    
    @GetMapping("api/v1/customers/{customerId}/addresses")
    public ResponseEntity<List<CustomerAddress>> listAddresses(Pageable p, @PathVariable("customerId") Long customerId){
        
        Page<CustomerAddress> resultPage = customerAddressRepository.findByCustomerIdOrderByIdDesc(p, customerId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Total-Count", String.valueOf(resultPage.getTotalElements()));
        return new ResponseEntity<List<CustomerAddress>>(resultPage.getContent(), headers, HttpStatus.OK);
    }
    
    @PostMapping("api/v1/customers/{customerId}/addresses")
    public ResponseEntity<CustomerAddress> createAddress(@PathVariable("customerId") Long customerId, @RequestBody CustomerAddress address, UriComponentsBuilder builder) {
        
        Customer customer = customerRepository.findOne(customerId);
        if(customer==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        address.setCustomerId(customer.getId());
        CustomerAddress addressCreated = customerAddressRepository.save(address);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/api/v1/customers/{customerId}/addresses/{addressId}").buildAndExpand(customerId, addressCreated.getId()).toUri());
        return new ResponseEntity<CustomerAddress>(addressCreated, headers, HttpStatus.CREATED);
            
    }
    
    @PutMapping("api/v1/customers/{customerId}/addresses/{addressId}")
    public ResponseEntity<CustomerAddress> updateAddress(@PathVariable("customerId") Long customerId, @PathVariable("addressId") Long addressId, @RequestBody CustomerAddress address) {
        CustomerAddress addressFound = customerAddressRepository.findOneByIdAndCustomerId(addressId, customerId);
        if(addressFound==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        address.setId(addressFound.getId());
        address.setCustomerId(addressFound.getCustomerId());
        CustomerAddress addressSaved = customerAddressRepository.save(address);
        return new ResponseEntity<CustomerAddress>(addressSaved, HttpStatus.OK);
    }
    
    @DeleteMapping("api/v1/customers/{customerId}/addresses/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable("customerId") Long customerId, @PathVariable("addressId") Long addressId) {
        CustomerAddress addressFound = customerAddressRepository.findOneByIdAndCustomerId(addressId, customerId);
        if(addressFound==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        customerAddressRepository.delete(addressFound);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("api/v1/customers/{customerId}/infos/{infoId}")
    public ResponseEntity<AdditionalInformation> retrieveInfo(@PathVariable("customerId") Long customerId, @PathVariable("infoId") Long infoId) {
        AdditionalInformation info = additionalInformationRepository.findOneByIdAndParentTypeAndParentId(infoId, Customer.class.getSimpleName(), customerId);
        if(info==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<AdditionalInformation>(info, HttpStatus.OK);
    }
    
    @GetMapping("api/v1/customers/{customerId}/infos")
    public ResponseEntity<List<AdditionalInformation>> listInfos(Pageable p, @PathVariable("customerId") Long customerId){
        
        Page<AdditionalInformation> resultPage = additionalInformationRepository.findByParentTypeAndParentId(p, Customer.class.getSimpleName(), customerId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Total-Count", String.valueOf(resultPage.getTotalElements()));
        return new ResponseEntity<List<AdditionalInformation>>(resultPage.getContent(), headers, HttpStatus.OK);
    }
    
    @PostMapping("api/v1/customers/{customerId}/infos")
    public ResponseEntity<AdditionalInformation> createInfo(@PathVariable("customerId") Long customerId, @RequestBody AdditionalInformation info, UriComponentsBuilder builder) {
        
        Customer customer = customerRepository.findOne(customerId);
        if(customer==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        info.setParentType(Customer.class.getSimpleName());
        info.setParentId(customer.getId());
        
        AdditionalInformation infoCreated = additionalInformationRepository.save(info);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/api/v1/customers/{customerId}/infos/{infoId}").buildAndExpand(customerId, infoCreated.getId()).toUri());
        return new ResponseEntity<AdditionalInformation>(infoCreated, headers, HttpStatus.CREATED);
    }
    
    @PutMapping("api/v1/customers/{customerId}/infos/{infoId}")
    public ResponseEntity<AdditionalInformation> updateInfo(@PathVariable("customerId") Long customerId, @PathVariable("infoId") Long infoId, @RequestBody AdditionalInformation info) {
        AdditionalInformation infoFound = additionalInformationRepository.findOneByIdAndParentTypeAndParentId(infoId, Customer.class.getSimpleName(), customerId);
        if(infoFound==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        info.setId(infoFound.getId());
        info.setParentType(infoFound.getParentType());
        info.setParentId(infoFound.getParentId());
        
        AdditionalInformation infoSaved = additionalInformationRepository.save(info);
        return new ResponseEntity<AdditionalInformation>(infoSaved, HttpStatus.OK);
    }
    
    @DeleteMapping("api/v1/customers/{customerId}/infos/{infoId}")
    public ResponseEntity<Void> deleteInfo(@PathVariable("customerId") Long customerId, @PathVariable("infoId") Long infoId) {
        AdditionalInformation infoFound = additionalInformationRepository.findOneByIdAndParentTypeAndParentId(infoId, Customer.class.getSimpleName(), customerId);
        if(infoFound==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        additionalInformationRepository.delete(infoFound);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
}
