Project 1:

Demo Video: https://youtu.be/VKuoECgadOM

Contributions:
- Weiguang Wu: project setup, implemented movie list page and jump functionality, recorded demo
- Saul Hernandez: project setup, implemented single star page, single movie page, and jump functionality between them

Project 2:

Demo Video: https://youtu.be/gAUZ40QdwNY

Contributions:
- Weiguang Wu: login, shopping cart, payment, comfirmation
- Saul Hernandez: searching, browsing, sorting, pagination

Substring Matching Design:

LIKE predicate usage

PaymentServlet.java
- used to ensure direct match with credit card info
- select * from creditcards where firstName like ? and 
- lastName like ? and id like ? and expiration like ?

LoginServlet.java
- used to retrieve all customers with direct matching email
- select * from customers where email like ?"

MovieListServlet.java
- used to search for matching titles/director/star name
 such that the pattern is contained anywhere
- AND title LIKE '%" + title + "%'";
- AND director LIKE '%" + director + "%'";
- WHERE st.name LIKE '%" + star + "%'

MoviesByTitleServlet.java
- used to find all movies that start with given character
(assuming its not the * character)
- where m.title like ?%

Project 3:

Demo Video: https://www.youtube.com/watch?v=HtJHjQkU5wQ

Contributions:
- Weiguang Wu: reCaptcha, xmlDomParser
- Saul Hernandez: https, preparedStatements, encrypted password, dashboard

Inconsistencies:
- 5276 stars are inserted
- 95 genres are inserted
- 7419 movies are inserted
- 8385 genres_in_movies are inserted
- 29307 stars_in_movies are inserted
- 4696 movies are inconsistent (no genre or no star)
- 1563 stars are inconsistent (not in both casts and actors)

Project 4:

Demo Video: https://youtu.be/smDRbiCmumo

Contributions:
- Weiguang Wu: Android App for Fabflix
- Saul Hernandez: Full-text Search and Auto-complete

Project 5:
- # General
    - #### Team#: Team Nop
    
    - #### Names: Weiguang Wu, Saul Hernandez
    
    - #### Project 5 Video Demo Link:

    - #### Instruction of deployment:

    - #### Collaborations and Work Distribution: Weiguang Wu: JDBC connection pooling, readme; Saul Hernandez: MySQL Matser-Slave replication, Scaling/Load Balancer


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    
    - #### Explain how Connection Pooling works with two backend SQL.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

    - #### How read/write requests were routed to Master/Slave SQL?
    

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.


- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | ??                         | ??                                  | ??                        | ??           |

