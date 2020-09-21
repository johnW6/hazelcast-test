package com.example.ehcache;

import com.example.ehcache.entities.Book;
import com.example.ehcache.repositories.BookRepository;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@EnableCaching
public class Application implements CommandLineRunner {

    @Autowired
    BookRepository bookRepository;

    public static final String BOOKS = "books";
    private final HazelcastInstance hazelcastInstance
            = Hazelcast.newHazelcastInstance();

    public static void main(String[] args) {


        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        addToDb();
        System.out.println("adding completed");
        printAll();
        getBooks();
    }

    public void addToDb() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            Book book = new Book();
            book.setName("Book:" + i);
            bookRepository.save(book);
        }
    }

    public void printAll() throws InterruptedException {
        for (int i = 1; i < 6; i++) {
            Optional<Book> book = bookRepository.findById(i);
//            System.out.println(book.get());
        }
    }

    public void getBooks() {

        hazelcastInstance.getMap(BOOKS);


        System.out.println("Lets get Book 1:");
        System.out.println("Lets get Book 1 first query:");
        Optional<Book> book = bookRepository.findById(1);
        System.out.println("Lets get Book 1 second query:");
        Optional<Book> book1 = bookRepository.findById(1);

        System.out.println("Book 1");
        System.out.println(book1);

        System.out.println("Lets edit Book 1:");
        book1.get().setName("Edited book name test" );
        bookRepository.save(book1.get());

        System.out.println("Lets get edited Book 1");
        Optional<Book> bookEdited = bookRepository.findById(1);
        System.out.println(bookEdited.toString());


    }
}
