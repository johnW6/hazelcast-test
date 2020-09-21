package com.example.ehcache;

import com.example.ehcache.entities.Book;
import com.example.ehcache.repositories.BookRepository;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@SpringBootApplication
@EnableCaching
public class Application implements CommandLineRunner {

    static Logger LOG = LoggerFactory.getLogger(Application.class);

    @Autowired
    BookRepository bookRepository;

    Random random = new Random();

    public static void main(String[] args) {


        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        ConfigurableApplicationContext ctx = springApplication.run(args);
        LOG.info("DONE");
        ctx.close();
    }

    @Override
    public void run(String... args) throws Exception {
        addToDb();
        LOG.info("adding completed");
        printAll();
        getBooks();
    }

    @Transactional
    public void addToDb() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            Book book = new Book();
            book.setName("Book:" + i);
            LOG.info("Savings" + i);
            saveBook(book);
        }
    }

    public void printAll() throws InterruptedException {
        for (int i = 1; i < 6; i++) {
            Optional<Book> book = bookRepository.findById(i);
//            LOG.info(book.get());
        }
    }

    public void getBooks() throws InterruptedException {

        LOG.info("Lets get Book 1:");
        LOG.info("Lets get Book 1 first query:");
        Book book = getBook(1);
        LOG.info("Lets get Book 1 second query:");
        Book book1 = getBook(1);

        LOG.info("Book 1");
        LOG.info(book1.getName());

        LOG.info("Lets edit Book 1:");
        book1.setName(book1.getName()+random.nextInt(10000));
        saveBook(book1);
        Thread.sleep(2000);

        LOG.info("Lets get edited Book 1");
        Book bookEdited = getBook(1);
        LOG.info(bookEdited.toString());
    }


    @Transactional
    public Book getBook(Integer id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Transactional
    public void saveBook(Book book) {
        bookRepository.save(book);
    }
}
