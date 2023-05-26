package br.com.abner.springbootstart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.abner.springbootstart.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

}
