-- Authors Table
CREATE TABLE IF NOT EXISTS Authors (
    author_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

-- Trigger to reset sequence after deleting an author
CREATE TRIGGER reset_authors_sequence AFTER DELETE ON Authors
BEGIN
    UPDATE sqlite_sequence SET seq = (SELECT MAX(author_id) FROM Authors) WHERE name = 'Authors';
END;

-- Books Table with foreign key constraint for cascading delete
CREATE TABLE IF NOT EXISTS Books (
    book_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    author_id INTEGER,
    ISBN TEXT UNIQUE,
    publication_date TEXT,
    available_copies INTEGER DEFAULT 1,
    FOREIGN KEY (author_id) REFERENCES Authors (author_id) ON DELETE CASCADE
);

-- Trigger to reset sequence after deleting a book
CREATE TRIGGER reset_books_sequence AFTER DELETE ON Books
BEGIN
    UPDATE sqlite_sequence SET seq = (SELECT MAX(book_id) FROM Books) WHERE name = 'Books';
END;

-- Trigger to delete author if no books are left after a book is deleted
CREATE TRIGGER delete_author_if_no_books_after_delete
AFTER DELETE ON Books
FOR EACH ROW
BEGIN
    DELETE FROM Authors
    WHERE author_id = OLD.author_id
    AND NOT EXISTS (SELECT 1 FROM Books WHERE author_id = OLD.author_id);
END;

-- Members Table
CREATE TABLE IF NOT EXISTS members (
    member_id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_name TEXT NOT NULL
);

-- Trigger to reset sequence after deleting a member
CREATE TRIGGER reset_members_sequence AFTER DELETE ON members
BEGIN
    UPDATE sqlite_sequence SET seq = (SELECT MAX(member_id) FROM members) WHERE name = 'members';
END;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    password TEXT NOT NULL
);

-- Trigger to reset sequence after deleting a user
CREATE TRIGGER reset_users_sequence AFTER DELETE ON users
BEGIN
    UPDATE sqlite_sequence SET seq = (SELECT MAX(id) FROM users) WHERE name = 'users';
END;

-- BorrowedBooks Table with foreign key constraints for cascading delete
CREATE TABLE IF NOT EXISTS BorrowedBooks (
    borrow_id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_id INTEGER,
    book_id INTEGER,
    borrow_date TEXT,
    return_date TEXT,
    FOREIGN KEY (member_id) REFERENCES members (member_id),
    FOREIGN KEY (book_id) REFERENCES Books (book_id),
    UNIQUE (member_id, book_id)
);

-- Trigger to reset sequence after deleting a borrowed book record
CREATE TRIGGER reset_borrowed_books_sequence AFTER DELETE ON BorrowedBooks
BEGIN
    UPDATE sqlite_sequence SET seq = (SELECT MAX(borrow_id) FROM BorrowedBooks) WHERE name = 'BorrowedBooks';
END;

-- Sample Data Insertions
-- Insert authors
INSERT INTO Authors (name) VALUES
('J.K. Rowling'), ('George R.R. Martin'), ('J.R.R. Tolkien'), 
('Agatha Christie'), ('Dan Brown'), ('Stephen King'), 
('Haruki Murakami'), ('Isaac Asimov'), ('Arthur C. Clarke'), 
('Jane Austen'), ('Charles Dickens'), ('Mark Twain'), 
('Ernest Hemingway'), ('Leo Tolstoy'), ('F. Scott Fitzgerald');



-- Members Data
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
('James Hall');

-- Users Data
INSERT INTO users (username, password) VALUES
('john_doe', 'password123'),
('jane_smith', 'mypassword'),
('mike_johnson', '123456'),
('emily_davis', 'emily123'),
('chris_brown', 'chris321'),
('laura_wilson', 'laura789'),
('david_moore', 'davem123'),
('sarah_taylor', 'sarah456'),
('daniel_harris', 'daniel789'),
('jessica_lee', 'jessica123'),
('matthew_clark', 'matt321'),
('samantha_lewis', 'sam234'),
('william_young', 'william2024'),
('olivia_walker', 'olivia111'),
('james_hall', 'jamespass');

-- BorrowedBooks Data
INSERT INTO BorrowedBooks (member_id, book_id, borrow_date, return_date) VALUES
(1, 1, '2024-10-01', '2024-10-15'),
(2, 2, '2024-10-05', '2024-10-19'),
(3, 3, '2024-10-10', '2024-10-24'),
(4, 4, '2024-10-12', '2024-10-26'),
(5, 5, '2024-10-15', '2024-10-29'),
(6, 6, '2024-10-17', '2024-10-31'),
(7, 7, '2024-10-20', '2024-11-03'),
(8, 8, '2024-10-22', '2024-11-05'),
(9, 9, '2024-10-25', '2024-11-08'),
(10, 10, '2024-10-28', '2024-11-11'),
(11, 11, '2024-10-30', '2024-11-13'),
(12, 12, '2024-11-02', '2024-11-16'),
(13, 13, '2024-11-05', '2024-11-19'),
(14, 14, '2024-11-07', '2024-11-21'),
(15, 15, '2024-11-10', '2024-11-24');

