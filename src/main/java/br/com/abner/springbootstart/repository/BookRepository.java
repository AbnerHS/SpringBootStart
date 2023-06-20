package br.com.abner.springbootstart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.abner.springbootstart.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    
    @Query("SELECT b FROM Book b WHERE b.title LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Book> findBookByTitle(@Param("title") String title, Pageable pageable);

}
