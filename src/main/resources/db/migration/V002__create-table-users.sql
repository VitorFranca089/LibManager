CREATE TABLE users(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    username VARCHAR(150) UNIQUE,
    password VARCHAR(150) NOT NULL,
    address VARCHAR(200) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    ROLE ENUM('common', 'librarian') DEFAULT 'common' NOT NULL
)