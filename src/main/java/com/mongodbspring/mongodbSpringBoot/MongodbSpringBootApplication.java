package com.mongodbspring.mongodbSpringBoot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

@SpringBootApplication
public class MongodbSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongodbSpringBootApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(StudentRepository repository, MongoTemplate mongoTemplate) {
        return args -> {
            Address address = new Address("China", "10000", "Shenyang");
            String email = "Testing for default";
            Student student = new Student(
                    "asdad",
                    "dan",
                    email,
                    Gender.MALE,
                    address,
                    List.of("Computer Science", "math", "food"),
                    BigDecimal.TEN,
                    LocalDateTime.now()
            );

            //custom queries
//            usingMongoTempLateAndQuery(repository, mongoTemplate, email, student);

            // Using optional interface implementation in StudentRepository
            repository.findStudentByEmail(email).ifPresentOrElse(s -> {
                System.out.println(student + " already exists");
//                repository.deleteStudentByEmail(email);
//                System.out.println("deleted");
            }, () -> {
                System.out.println("Inserting student " + student);
                repository.insert(student);
            });

        };
    }

    private void usingMongoTempLateAndQuery(StudentRepository repository, MongoTemplate mongoTemplate, String email, Student student) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));

        List<Student> students = mongoTemplate.find(query, Student.class);

        if (students.size() > 1) {
            throw new IllegalStateException("Found many student with email " + email);
        }

        if (students.isEmpty()) {
            System.out.println("Inserting student " + student);
            repository.insert(student);
        } else {
            System.out.println(student + " already exists");
        }
    }
}
