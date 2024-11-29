CREATE TABLE Authors(
    author_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
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

DELETE FROM Books;
DELETE FROM Members;
DELETE FROM Authors;

INSERT INTO Authors (name) VALUES
('J.K. Rowling'), ('George R.R. Martin'), ('J.R.R. Tolkien'), 
('Agatha Christie'), ('Dan Brown'), ('Stephen King'), 
('Haruki Murakami'), ('Isaac Asimov'), ('Arthur C. Clarke'), 
('Jane Austen'), ('Charles Dickens'), ('Mark Twain'), 
('Ernest Hemingway'), ('Leo Tolstoy'), ('F. Scott Fitzgerald');

-- Insert books with valid author_id
INSERT INTO Books (title, author_id, ISBN, publication_date, available_copies) VALUES
('Harry Potter and the Sorcerers Stone', 1, '9780747532743', '1997-06-26', 10),
('A Game of Thrones', 2, '9780553103540', '1996-08-06', 5),
('The Hobbit', 3, '9780007458424', '1937-09-21', 8),
('Murder on the Orient Express', 4, '9780062693662', '1934-01-01', 6),
('The Da Vinci Code', 5, '9780385504201', '2003-03-18', 12),
('The Shining', 6, '9780307743657', '1977-01-28', 4),
('Norwegian Wood', 7, '9780375704024', '1987-09-04', 9),
('Foundation', 8, '9780553293357', '1951-06-01', 7),
('2001: A Space Odyssey', 9, '9780451457998', '1968-06-01', 5),
('Pride and Prejudice', 10, '9780141040349', '1813-01-28', 15),
('Oliver Twist', 11, '9780199536195', '1837-02-01', 7),
('The Adventures of Huckleberry Finn', 12, '9780486280615', '1884-12-10', 6),
('The Old Man and the Sea', 13, '9780684801223', '1952-09-01', 4),
('War and Peace', 14, '9780307266934', '1869-03-01', 3),
('The Great Gatsby', 15, '9780743273565', '1925-04-10', 10);

INSERT INTO members (member_name) VALUES
('John Doe'),
('Jane Smith'),
('Mike Johnson'),
('Emily Davis'),
('Chris Brown'),
('Laura Wilson'),
('David Moore'),
('Sarah Taylor'),
('Daniel Harris'),
('Jessica Lee'),
('Matthew Clark'),
('Samantha Lewis'),
('William Young'),
('Olivia Walker'),
('James Hall'),
('Mark Diaz'),
('Zio Magugat'),
('Paul Dacalan'),
('Jules Agustin');
