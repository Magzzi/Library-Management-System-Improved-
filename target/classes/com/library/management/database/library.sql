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
