package br.com.abner.springbootstart.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import br.com.abner.springbootstart.model.Person;

@Service
public class PersonServices {
    
    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(PersonServices.class.getName());

    public List<Person> findAll(){
        logger.info("Finding all people!");

        List<Person> persons = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Person person = mockPerson(i);
            persons.add(person);
        }        
        return persons;
    }

    public Person findById(String id){
        logger.info("Finding one person!");

        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Abner");
        person.setLastName("Simas");
        person.setGender("Male");
        person.setAddress("Rua Itororó, 410");
        
        return person;
    }

    private Person mockPerson(int i) {
        Person person = new Person();
        person.setId(Long.parseLong(String.valueOf(i)));
        person.setFirstName("Person name " + i);
        person.setLastName("Last name " + i);
        person.setGender("Male");
        person.setAddress("Brasil");

        return person;
    }
}
