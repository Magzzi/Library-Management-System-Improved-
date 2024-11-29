CREATE TABLE Authors(
    author_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE Books(
    book_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    author_id INTEGER,
    ISBN TEXT UNIQUE,
    publication_date TEXT,
    available_copies INTEGER DEFAULT 1,
    FOREIGN KEY (author_id) REFERENCES Authors (author_id)
);

CREATE TABLE members (
    member_id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_name TEXT NOT NULL
);

CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    password TEXT NOT NULL
);

CREATE TABLE BorrowedBooks (
    borrow_id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER,
    book_id INTEGER,
    borrow_date TEXT,
    return_date TEXT,
    FOREIGN KEY (member_id) REFERENCES members (member_id),
    FOREIGN KEY (book_id) REFERENCES Books (book_id),
    UNIQUE (member_id, book_id)
);


SELECT * FROM Books;
SELECT * FROM Members;
SELECT * FROM Authors;

DELETE FROM BorrowedBooks;




INSERT INTO Books (title, author_id, ISBN, publication_date, available_copies) 
VALUES 
('Harry Potter and the Philosopher''s Stone', 1, '9780747532699', '1997-06-26', 5),
('A Game of Thrones', 2, '9780553103540', '1996-08-06', 3),
('The Hobbit', 3, '9780618968635', '1937-09-21', 4),  -- Changed ISBN
('Murder on the Orient Express', 4, '9780007119321', '1934-01-01', 2),  -- Changed ISBN
('The Shining', 5, '9780385121677', '1977-01-28', 6),  -- Changed ISBN
('Foundation', 6, '9780553293359', '1951-06-01', 1),  -- Changed ISBN
('Sherlock Holmes: The Complete Novels and Stories', 7, '9780553212421', '1887-11-14', 7),  -- Changed ISBN
('The Adventures of Tom Sawyer', 8, '9780486400780', '1876-01-01', 8),  -- Changed ISBN
('Pride and Prejudice', 9, '9780141040352', '1813-01-28', 9),  -- Changed ISBN
('Great Expectations', 10, '9780141439572', '1861-01-01', 10);  -- Changed ISBN

INSERT INTO Authors (name) 
VALUES
('J.K. Rowling'),
('George R.R. Martin'),
('J.R.R. Tolkien'),
('Agatha Christie'),
('Stephen King'),
('Isaac Asimov'),
('Sir Arthur Conan Doyle'),
('Mark Twain'),
('Jane Austen'),
('Charles Dickens');
