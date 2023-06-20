package br.com.abner.springbootstart.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.abner.springbootstart.controllers.PersonController;
import br.com.abner.springbootstart.data.vo.v1.PersonVO;
import br.com.abner.springbootstart.exceptions.RequiredObjectIsNullException;
import br.com.abner.springbootstart.exceptions.ResourceNotFoundException;
import br.com.abner.springbootstart.mapper.DozerMapper;
import br.com.abner.springbootstart.model.Person;
import br.com.abner.springbootstart.repository.PersonRepository;
import jakarta.transaction.Transactional;

@Service
public class PersonServices {

    private Logger logger = Logger.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    PagedResourcesAssembler<PersonVO> assembler;

    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
        logger.info("Finding all people!");
        Page<Person> personPage = repository.findAll(pageable);
        
        Page<PersonVO> personVoPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
        personVoPage.map(person -> person.add(
                linkTo(methodOn(PersonController.class).findById(person.getId())).withSelfRel()));
        
        Link link = linkTo(methodOn(PersonController.class).findAll(
                pageable.getPageNumber(), 
                pageable.getPageSize(), 
                "asc")).withSelfRel();
                
        return assembler.toModel(personVoPage, link);
    }

    public PagedModel<EntityModel<PersonVO>> findPersonsByName(String firstName, Pageable pageable) {
        logger.info("Finding person by name!");
        Page<Person> personPage = repository.findPersonsByName(firstName, pageable);

        Page<PersonVO> personVoPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
        personVoPage.map(person -> person.add(
                linkTo(methodOn(PersonController.class).findById(person.getId())).withSelfRel()));
        
        Link link = linkTo(methodOn(PersonController.class).findAll(
                pageable.getPageNumber(), 
                pageable.getPageSize(), 
                "asc")).withSelfRel();

        return assembler.toModel(personVoPage, link);
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one person!");

        Person entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public PersonVO create(PersonVO person) {

        if(person == null) throw new RequiredObjectIsNullException();
        
        logger.info("Creating one person!");
        //Recebe ValueObject e converte pra Person (Entity)
        Person entity = DozerMapper.parseObject(person, Person.class);
        //Salva o Entity e converte pra ValueObject
        PersonVO vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withSelfRel());
        return vo;
    }

    public PersonVO update(PersonVO person) {

        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one person!");

        //Recebe ValueObject, busca e recebe Person (Entity)
        Person entity = repository.findById(person.getId())
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setGender(person.getGender());
        entity.setAddress(person.getAddress());
        //Salva o Entity e converte para ValueObject
        PersonVO vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withSelfRel());
        return vo;
    }

    @Transactional
    public PersonVO disablePerson(Long id) {
        logger.info("Finding one person!");

        repository.disablePerson(id);

        Person entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    @Transactional
    public PersonVO enablePerson(Long id) {
        logger.info("Finding one person!");

        repository.enablePerson(id);

        Person entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
        vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public void delete(Long id) {
        logger.info("Deleting one person!");

        Person entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        
        repository.delete(entity);
    }

}
