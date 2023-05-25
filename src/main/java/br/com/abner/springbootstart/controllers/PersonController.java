package br.com.abner.springbootstart.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.abner.springbootstart.model.Person;
import br.com.abner.springbootstart.services.PersonServices;

@RestController
@RequestMapping("/person")
public class PersonController {
    
    @Autowired
    private PersonServices service;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> findAll() throws Exception {
        return service.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Person findById(@PathVariable(value = "id") String id) throws Exception {
        return service.findById(id);
    }

}
