package br.com.abner.springbootstart.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.abner.springbootstart.data.vo.v1.PersonVO;
import br.com.abner.springbootstart.exceptions.RequiredObjectIsNullException;
import br.com.abner.springbootstart.model.Person;
import br.com.abner.springbootstart.repository.PersonRepository;
import br.com.abner.springbootstart.services.PersonServices;
import br.com.abner.springbootstart.unittests.mapper.mocks.MockPerson;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class PersonServicesTest {

    MockPerson input;

    @InjectMocks
    private PersonServices service;

    @Mock
    PersonRepository repository;

    @BeforeEach
    void setUpMocks() throws Exception {
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
    }

    // @Test
    // void testFindAll() {
    //     List<Person> personList = input.mockEntityList();

    //     when(repository.findAll()).thenReturn(personList);

    //     List<PersonVO> result = service.findAll();
    //     assertNotNull(result);
    //     assertEquals(14, personList.size());

    //     PersonVO personOne = result.get(1);
        
    //     assertNotNull(personOne);
    //     assertNotNull(personOne.getId());
    //     assertNotNull(personOne.getLinks());
    //     assertTrue(personOne.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
    //     assertEquals("First Name Test1", personOne.getFirstName());
    //     assertEquals("Last Name Test1", personOne.getLastName());
    //     assertEquals("Addres Test1", personOne.getAddress());
    //     assertEquals("Female", personOne.getGender());

    //     PersonVO personFour = result.get(4);
        
    //     assertNotNull(personFour);
    //     assertNotNull(personFour.getId());
    //     assertNotNull(personFour.getLinks());
    //     assertTrue(personFour.toString().contains("links: [</api/person/v1/4>;rel=\"self\"]"));
    //     assertEquals("First Name Test4", personFour.getFirstName());
    //     assertEquals("Last Name Test4", personFour.getLastName());
    //     assertEquals("Addres Test4", personFour.getAddress());
    //     assertEquals("Male", personFour.getGender());

    //     PersonVO personSeven = result.get(7);
        
    //     assertNotNull(personSeven);
    //     assertNotNull(personSeven.getId());
    //     assertNotNull(personSeven.getLinks());
    //     assertTrue(personSeven.toString().contains("links: [</api/person/v1/7>;rel=\"self\"]"));
    //     assertEquals("First Name Test7", personSeven.getFirstName());
    //     assertEquals("Last Name Test7", personSeven.getLastName());
    //     assertEquals("Addres Test7", personSeven.getAddress());
    //     assertEquals("Female", personSeven.getGender());
    // }

    @Test
    void testFindById() {
        Person person = input.mockEntity(1);
        person.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(person));

        PersonVO result = service.findById(1L);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Addres Test1", result.getAddress());
        assertEquals("Female", result.getGender());       
    }

    @Test
    void testCreate() {
        Person entity = input.mockEntity(1);
        
        Person persisted = entity;
        persisted.setId(1L);

        PersonVO vo = input.mockVO(1);
        vo.setId(1L);
        when(repository.save(entity)).thenReturn(persisted);
        
        PersonVO result = service.create(vo);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Addres Test1", result.getAddress());
        assertEquals("Female", result.getGender());
    }

    @Test
    void testCreateWithNullPerson() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.create(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    @Test
    void testUpdate() {
        Person entity = input.mockEntity(1);
        
        Person persisted = entity;
        persisted.setId(1L);

        PersonVO vo = input.mockVO(1);
        vo.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(persisted);
        
        PersonVO result = service.update(vo);
        
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
        assertEquals("Addres Test1", result.getAddress());
        assertEquals("Female", result.getGender());
    }

    @Test
    void testUpdateWithNullPerson() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.update(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDelete() {
        Person person = input.mockEntity(1);
        person.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(person));

        service.delete(1L);
    }
}
