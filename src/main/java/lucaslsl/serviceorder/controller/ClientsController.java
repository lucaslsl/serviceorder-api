/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucaslsl.serviceorder.controller;

import java.util.List;
import lucaslsl.serviceorder.model.Client;
import lucaslsl.serviceorder.repository.ClientRepository;
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

/**
 *
 * @author lucaslsl
 */
@RestController
@CrossOrigin
public class ClientsController {
    
    @Autowired
    ClientRepository clientRepository;
    
    @GetMapping("api/v1/clients/{clientId}")
    public ResponseEntity<Client> retrieve(@PathVariable("clientId") Long id) {
        Client client = clientRepository.findOne(id);
        if(client==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Client>(client, HttpStatus.OK);
    }
    
    @GetMapping("api/v1/clients")
    public ResponseEntity<List<Client>> list(
            Pageable p,
            @RequestParam(value = "deleted", required = false) Boolean deleted,
            @RequestParam(value = "juridical", required = false) Boolean juridical,
            @RequestParam(value = "national_registration_number", required = false) String nationalRegistrationNumber) {
        
        Page<Client> resultPage;
        if(deleted != null && juridical != null){
            resultPage = clientRepository.findByDeletedAndJuridicalOrderByIdDesc(p, deleted, juridical);
        }else if(deleted == null && juridical != null){
            resultPage = clientRepository.findByJuridicalOrderByIdDesc(p, juridical);
        }else if(deleted != null && juridical == null){
            resultPage = clientRepository.findByDeletedOrderByIdDesc(p, deleted);
        }else if(nationalRegistrationNumber != null){
            resultPage = clientRepository.findByNationalRegistrationNumber(p, nationalRegistrationNumber);
        }else{
            resultPage = clientRepository.findAllByOrderByIdDesc(p);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Total-Count", String.valueOf(resultPage.getTotalElements()));
        return new ResponseEntity<List<Client>>(resultPage.getContent(), headers, HttpStatus.OK);
    }
    
    @PostMapping("api/v1/clients")
    public ResponseEntity<Client> create(@RequestBody Client client, UriComponentsBuilder builder) {
        
        Client clientFound = clientRepository.findOneByNationalRegistrationNumber(client.getNationalRegistrationNumber());
        if(clientFound != null){
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        
        client.setDeleted(Boolean.FALSE);
        Client clientCreated = clientRepository.save(client);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/api/v1/client/{clientId}").buildAndExpand(clientCreated.getId()).toUri());
        return new ResponseEntity<Client>(clientCreated, headers, HttpStatus.CREATED);
            
    }
    
    @PutMapping("api/v1/clients/{clientId}")
    public ResponseEntity<Client> update(@PathVariable("clientId") Long id, @RequestBody Client client) {
        Client clientFound = clientRepository.findOne(id);
        if(clientFound==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        client.setId(clientFound.getId());
        Client clientSaved = clientRepository.save(client);
        return new ResponseEntity<Client>(clientSaved, HttpStatus.OK);
    }
    
    @DeleteMapping("api/v1/clients/{clientId}")
    public ResponseEntity<Void> delete(@PathVariable("clientId") Long id) {
        Client client = clientRepository.findOne(id);
        if(client==null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        client.setDeleted(Boolean.TRUE);
        clientRepository.save(client);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
}
