package br.com.abner.springbootstart.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.abner.springbootstart.data.vo.v1.PersonVO;
import br.com.abner.springbootstart.data.vo.v2.PersonVOV2;
import br.com.abner.springbootstart.exceptions.ResourceNotFoundException;
import br.com.abner.springbootstart.mapper.DozerMapper;
import br.com.abner.springbootstart.mapper.custom.PersonMapper;
import br.com.abner.springbootstart.model.Person;
import br.com.abner.springbootstart.repository.PersonRepository;

@Service
public class PersonServices {

    private Logger logger = Logger.getLogger(PersonServices.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    PersonMapper personMapper;

    public List<PersonVO> findAll() {
        logger.info("Finding all people!");

        return DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one person!");

        Person entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        return DozerMapper.parseObject(entity, PersonVO.class);
    }

    public PersonVO create(PersonVO person) {
        logger.info("Creating one person!");
        //Recebe ValueObject e converte pra Person (Entity)
        Person entity = DozerMapper.parseObject(person, Person.class);
        //Salva o Entity e converte pra ValueObject
        PersonVO vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        return vo;
    }

    public PersonVOV2 createV2(PersonVOV2 person) {
        logger.info("Creating one person with V2!");
        //Recebe ValueObject e converte pra Person (Entity)
        Person entity = personMapper.convertVoToEntity(person);
        //Salva o Entity e converte pra ValueObject
        PersonVOV2 vo = personMapper.convertEntityToVo(repository.save(entity));
        return vo;
    }

    public PersonVO update(PersonVO person) {
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
        return vo;
    }

    public void delete(Long id) {
        logger.info("Deleting one person!");

        Person entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        
        repository.delete(entity);
    }

}
