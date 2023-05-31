package br.com.abner.springbootstart.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.abner.springbootstart.controllers.BookController;
import br.com.abner.springbootstart.data.vo.v1.BookVO;
import br.com.abner.springbootstart.exceptions.RequiredObjectIsNullException;
import br.com.abner.springbootstart.mapper.DozerMapper;
import br.com.abner.springbootstart.model.Book;
import br.com.abner.springbootstart.repository.BookRepository;

@Service
public class BookServices {

    @Autowired
    BookRepository repository;

    public List<BookVO> findAll(){
        List<BookVO> books = DozerMapper.parseListObjects(repository.findAll(), BookVO.class);
        books
            .stream()
            .forEach(book -> book.add(linkTo(methodOn(BookController.class).findById(book.getId())).withSelfRel()));
        return books;
    }

    public BookVO findById(Long id){
        Book entity = repository.findById(id)
            .orElseThrow(() -> new RequiredObjectIsNullException("No records found for this ID!"));

        BookVO vo = DozerMapper.parseObject(entity, BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
        return vo;
    }
    
    public BookVO create(BookVO book){
        if(book == null) throw new RequiredObjectIsNullException();

        Book entity = DozerMapper.parseObject(book, Book.class);
        BookVO vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getId())).withSelfRel());
        return vo;
    }
    
    public BookVO update(BookVO book){
        if(book == null) throw new RequiredObjectIsNullException();

        Book entity = repository.findById(book.getId())
            .orElseThrow(() -> new RequiredObjectIsNullException("No records found for this ID!"));            

        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());
        
        BookVO vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
        vo.add(linkTo(methodOn(BookController.class).findById(vo.getId())).withSelfRel());
        return vo;
    }

    public void delete(Long id){
        Book entity = repository.findById(id)
            .orElseThrow(() -> new RequiredObjectIsNullException("No records found for this ID!"));            

        repository.delete(entity);
    }
    
}
