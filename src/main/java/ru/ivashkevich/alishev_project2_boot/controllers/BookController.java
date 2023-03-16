package ru.ivashkevich.alishev_project2_boot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.ivashkevich.alishev_project2_boot.models.Book;
import ru.ivashkevich.alishev_project2_boot.models.Person;
import ru.ivashkevich.alishev_project2_boot.services.BooksService;
import ru.ivashkevich.alishev_project2_boot.services.PeopleService;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BooksService booksService;
    private final PeopleService peopleService;

    @Autowired
    public BookController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    @GetMapping
    public String showAllBooks(Model model,
                               @RequestParam(required = false) Integer page,
                               @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                               @RequestParam(value = "sort_by_year", required = false) boolean sortByYear){
        List<Book> books;
        if ((page != null) && (booksPerPage != null)) {
            if (sortByYear)
                books = booksService.getBooksFromPageNumberSortByPublicationYear(page, booksPerPage);
            else
                books = booksService.getBooksFromPageNumber(page, booksPerPage);
        }
        else
            if (sortByYear)
                books = booksService.getAllBooksSortByPublicationYear();
            else
                books = booksService.getAllBooks();

        model.addAttribute("books", books);

        return "books/all-books";
    }

    @GetMapping("/{id}")
    public String showBookById(@ModelAttribute("person") Person person, @PathVariable int id, Model model){
        Book book = booksService.getBookById(id);
        model.addAttribute("book", book);

        if (book.getOwner() != null){
            model.addAttribute("owner", book.getOwner());
        }
        else {
            model.addAttribute("people", peopleService.getAllPeople());
        }

        return "books/book";
    }

    @GetMapping("/new")
    public String newBook(Model model){
        model.addAttribute("book", new Book());
        return "books/new";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id){
        model.addAttribute("book", booksService.getBookById(id));
        return "books/edit";
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam(value = "starts_with", required = false) String startsWith){
        if (startsWith != null){
            model.addAttribute("books", booksService.getBooksWithTitleStartingWith(startsWith));
        }
        return "books/search";
    }

    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "books/new";

        booksService.create(book);
        return "redirect:/books";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult, @PathVariable("id") int id){
        if(bindingResult.hasErrors())
            return "books/edit";
        booksService.update(id, book);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/assign")
    public String setOwner(@ModelAttribute("owner") Person person, @PathVariable("id") int id){
        booksService.assignOwner(id, person);

        return "redirect:/books/{id}";
    }

    @PatchMapping("/{id}/release")
    public String deleteOwner(@PathVariable("id") int id){
        booksService.releaseBookFromOwner(id);
        return "redirect:/books/{id}";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
        booksService.delete(id);
        return "redirect:/books";
    }
}
