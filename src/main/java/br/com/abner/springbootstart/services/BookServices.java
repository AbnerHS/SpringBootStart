package br.com.abner.springbootstart.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
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
    
    @Autowired
    PagedResourcesAssembler<BookVO> assembler;

    public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable){
        Page<Book> bookPage = repository.findAll(pageable);
        
        Page<BookVO> bookVoPage = bookPage.map(b -> DozerMapper.parseObject(b, BookVO.class));
        bookVoPage.map(book -> book.add(
            linkTo(methodOn(BookController.class).findById(book.getId())).withSelfRel()));
            
        Link link = linkTo(methodOn(BookController.class).findAll(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            "asc")).withSelfRel();

        return assembler.toModel(bookVoPage, link);
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
