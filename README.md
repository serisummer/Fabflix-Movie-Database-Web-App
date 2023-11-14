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
