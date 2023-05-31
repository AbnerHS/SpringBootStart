package br.com.abner.springbootstart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.abner.springbootstart.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    
}
