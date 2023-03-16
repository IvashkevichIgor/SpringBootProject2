package ru.ivashkevich.alishev_project2_boot.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivashkevich.alishev_project2_boot.models.Book;
import ru.ivashkevich.alishev_project2_boot.models.Person;
import ru.ivashkevich.alishev_project2_boot.repositories.PeopleRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> getAllPeople(){
        return peopleRepository.findAll();
    }

    public Person getPersonById(int id){
        return peopleRepository.findById(id).orElse(null);
    }

    public List<Book> getBooksByPersonId(int id){
        Optional<Person> optional = peopleRepository.findById(id);
        if (optional.isPresent()){
            Hibernate.initialize(optional.get().getBooks());
            return optional.get().getBooks();
        }
        else
            return Collections.emptyList();
    }

    public boolean hasPersonWithName(String name){
        return peopleRepository.existsByName(name);
    }

    @Transactional
    public void create(Person person){
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson){
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    @Transactional
    public void delete(int id){
        peopleRepository.deleteById(id);
    }
}
