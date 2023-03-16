package ru.ivashkevich.alishev_project2_boot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivashkevich.alishev_project2_boot.models.Book;
import ru.ivashkevich.alishev_project2_boot.models.Person;
import ru.ivashkevich.alishev_project2_boot.repositories.BooksRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> getAllBooks(){
        return booksRepository.findAll();
    }

    public List<Book> getBooksFromPageNumber(int page, int booksPerPage){
        return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    public List<Book> getAllBooksSortByPublicationYear(){
        return booksRepository.findAll(Sort.by("publicationYear"));
    }

    public List<Book> getBooksFromPageNumberSortByPublicationYear(int page, int booksPerPage){
        return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("publicationYear"))).getContent();
    }

    public List<Book> getBooksWithTitleStartingWith(String startingWith){
        return booksRepository.findByTitleStartingWith(startingWith);
    }

    public Book getBookById(int id){
        return booksRepository.findById(id).orElse(null);
    }

    @Transactional
    public void create(Book book){
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook){
        updatedBook.setId(id);
        booksRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id){
        booksRepository.deleteById(id);
    }

    @Transactional
    public void assignOwner(int bookId, Person person){
        Optional<Book> optionalBook = booksRepository.findById(bookId);
        if (optionalBook.isPresent()){
            Book book = optionalBook.get();
            book.setTakenAt(new Date());
            book.setOwner(person);
        }
    }

    @Transactional
    public void releaseBookFromOwner(int id){
        Optional<Book> optionalBook = booksRepository.findById(id);
        if (optionalBook.isPresent()){
            Book book = optionalBook.get();
            book.setTakenAt(null);
            book.setOwner(null);
        }
    }
}
