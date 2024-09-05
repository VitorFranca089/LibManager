CREATE TABLE loans(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    id_book INT NOT NULL,
    id_user INT NOT NULL,
    loan_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP,
    FOREIGN KEY (id_book) REFERENCES books(id),
    FOREIGN KEY (id_user) REFERENCES users(id)
)