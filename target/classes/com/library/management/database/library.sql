------------------------------------------------------------------
---CREATE TABLE----------------------------------------------
------------------------------------------------------------------

---Authors Table--------------------------------------------------
CREATE TABLE Authors(
    author_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

---Books Table-----------------------------------------------------
CREATE TABLE Books(
    book_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    author_id INTEGER,
    ISBN TEXT UNIQUE,
    publication_date TEXT,
    available_copies INTEGER DEFAULT 1,
    FOREIGN KEY (author_id) REFERENCES Authors (author_id)
);

---members Table--------------------------------------------------
CREATE TABLE members (
    member_id INTEGER PRIMARY KEY AUTOINCREMENT,
    member_name TEXT NOT NULL
);

---users Table----------------------------------------------------
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    password TEXT NOT NULL
);

---BorrowedBooks Table--------------------------------------------
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

------------------------------------------------------------------
---SEQUENCE TRIGGER-----------------------------------------------
------------------------------------------------------------------

---Author Sequence Trigger----------------------------------------
CREATE TRIGGER reset_authors_sequence 
AFTER DELETE ON Authors
BEGIN
    UPDATE 
        sqlite_sequence 
    SET 
        seq = (SELECT MAX(author_id) FROM Authors) 
    WHERE 
        name = 'Authors';
END;

---Book Sequence Trigger------------------------------------------
CREATE TRIGGER reset_books_sequence 
AFTER DELETE ON Books
BEGIN
    UPDATE 
        sqlite_sequence 
    SET 
        seq = (SELECT MAX(book_id) FROM Books) 
    WHERE 
        name = 'Books';
END;

---Author(No Books) Sequence Trigger-------------------------------
CREATE TRIGGER delete_author_if_no_books_after_delete
AFTER DELETE ON Books
FOR EACH ROW
BEGIN
    DELETE FROM 
        Authors
    WHERE 
        author_id = OLD.author_id
    AND NOT EXISTS 
        (SELECT 1 FROM Books WHERE author_id = OLD.author_id);
END;

---Member Sequence Trigger-----------------------------------------
CREATE TRIGGER reset_members_sequence 
AFTER DELETE ON members
BEGIN
    UPDATE 
        sqlite_sequence 
    SET 
        seq = (SELECT MAX(member_id) FROM members) 
    WHERE name = 'members';
END;

---User Sequence Trigger-------------------------------------------
CREATE TRIGGER reset_users_sequence AFTER DELETE ON users
BEGIN
    UPDATE sqlite_sequence SET seq = (SELECT MAX(id) FROM users) WHERE name = 'users';
END;

---BorrowedBook Sequence Trigger-----------------------------------
CREATE TRIGGER reset_borrowed_books_sequence 
AFTER DELETE ON BorrowedBooks
BEGIN
    UPDATE 
        sqlite_sequence 
    SET 
        seq = (SELECT MAX(borrow_id) FROM BorrowedBooks) 
    WHERE name = 'BorrowedBooks';
END;

DELETE FROM Authors;
DELETE FROM Books;
DELETE FROM BorrowedBooks;

------------------------------------------------------------------
---INSERT INTO----------------------------------------------------
------------------------------------------------------------------

---Insert authors--------------------------------------------------
INSERT INTO Authors (name) VALUES
('J.K. Rowling'), 
('George R.R. Martin'), 
('J.R.R. Tolkien'), 
('Agatha Christie'), 
('Dan Brown'), 
('Stephen King'), 
('Haruki Murakami'), 
('Isaac Asimov'), 
('Arthur C. Clarke'), 
('Jane Austen'), 
('Charles Dickens'), 
('Mark Twain'), 
('Ernest Hemingway'), 
('Leo Tolstoy'), 
('F. Scott Fitzgerald'),
('Margaret Atwood'),
('Neil Gaiman'),
('Kurt Vonnegut'),
('Ray Bradbury'),
('Toni Morrison'),
('Virginia Woolf'),
('Gabriel Garcia Marquez'),
('John Steinbeck'),
('H.G. Wells'),
('Philip K. Dick'),
('Chuck Palahniuk'),
('Cormac McCarthy'),
('Zadie Smith'),
('Alice Walker'),
('David Mitchell'),
('Salman Rushdie'),
('Michael Ende'),
('E.L. James'),
('Stephen Hawking'),
('Colson Whitehead'),
('Danielle Steel'),
('John Grisham'),
('Nora Roberts'),
('Elena Ferrante'),
('Octavia Butler'),
('C.S. Lewis'),
('Roald Dahl'),
('Louisa May Alcott'),
('D.H. Lawrence'),
('Friedrich Nietzsche'),
('Herman Melville'),
('Jack Kerouac'),
('Philip Roth'),
('Maya Angelou'),
('Ralph Ellison'),
('J.D. Salinger'),
('George Orwell'),
('Mary Shelley'),
('Edgar Allan Poe'),
('Anne Rice'),
('David Foster Wallace'),
('Gillian Flynn'),
('Yaa Gyasi'),
('Chimamanda Ngozi Adichie'),
('Liane Moriarty'),
('Khaled Hosseini'),
('Tess Gerritsen'),
('Richard Adams'),
('Stephen Chbosky'),
('Willa Cather'),
('Ken Follett'),
('John le Carré');

---Insert books with valid author_id-------------------------------
INSERT INTO Books (title, author_id, ISBN, publication_date, available_copies) VALUES
('Harry Potter and the Sorcerers Stone', 1, '1001', '1997-06-26', 10),
('Harry Potter and the Chamber of Secrets', 1, '1002', '1998-07-02', 8),
('Harry Potter and the Prisoner of Azkaban', 1, '1003', '1999-07-08', 6),
('Harry Potter and the Goblet of Fire', 1, '1004', '2000-07-08', 5),
('Harry Potter and the Order of the Phoenix', 1, '1005', '2003-06-21', 4),
('Harry Potter and the Half-Blood Prince', 1, '1006', '2005-07-16', 3),
('Harry Potter and the Deathly Hallows', 1, '1007', '2007-07-21', 2),

('A Game of Thrones', 2, '1008', '1996-08-06', 5),
('A Clash of Kings', 2, '1009', '1998-11-16', 4),
('A Storm of Swords', 2, '1010', '2000-03-21', 3),
('A Feast for Crows', 2, '1011', '2005-10-17', 2),
('A Dance with Dragons', 2, '1012', '2011-07-12', 1),

('The Hobbit', 3, '1013', '1937-09-21', 8),
('The Fellowship of the Ring', 3, '1014', '1954-07-29', 5),
('The Two Towers', 3, '1015', '1954-11-11', 4),
('The Return of the King', 3, '1016', '1955-10-20', 3),

('Murder on the Orient Express', 4, '1017', '1934-01-01', 6),
('And Then There Were None', 4, '1018', '1939-11-06', 5),
('The ABC Murders', 4, '1019', '1936-01-01', 4),

('The Da Vinci Code', 5, '1020', '2003-03-18', 12),
('Angels & Demons', 5, '1021', '2000-05-01', 10),
('The Lost Symbol',  5, '1022', '2009-09-15', 8),

('The Shining', 6, '1023', '1977-01-28', 4),
('It', 6, '1024', '1986-09-15', 6),
('Misery', 6, '1025', '1987-06-08', 5),

('Norwegian Wood', 7, '1026', '1987-09-04', 9),
('Kafka on the Shore', 7, '1027', '2002-09-12', 7),

('Foundation', 8, '1028', '1951-06-01', 7),
('Foundation and Empire', 8, '1029', '1952-05-01', 5),
('Second Foundation', 8, '1030', '1953-06-01', 4),

('2001: A Space Odyssey', 9, '1031', '1968-06-01', 5),
('Rendezvous with Rama', 9, '1032', '1973-06-01', 6),

('Pride and Prejudice', 10, '1033', '1813-01-28', 15),
('Sense and Sensibility', 10, '1034', '1811-10-30', 10),

('Oliver Twist', 11, '1035', '1837-02-01', 7),
('A Christmas Carol', 11, '1036', '1843-12-19', 6),

('The Adventures of Huckleberry Finn', 12, '1037', '1884-12-10', 6),
('The Prince and the Pauper', 12, '1038', '1881-01-01', 5),

('The Old Man and the Sea', 13, '1039', '1952-09-01', 4),
('For Whom the Bell Tolls', 13, '1040', '1940-10-21', 3),

('War and Peace', 14, '1041', '1869-03-01', 3),
('Anna Karenina', 14, '1042', '1877-04-01', 2),

('The Great Gatsby', 15, '1043', '1925-04-10', 10),
('Tender is the Night', 15, '1044', '1934-04-12', 5),

('The Handmaids Tale', 16, '1045', '1985-04-17', 8),
('Oryx and Crake', 16, '1046', '2003-09-24', 6),

('American Gods', 17, '1047', '2001-06-19', 5),
('Coraline', 17, '1048', '2002-07-01', 4),

('Slaughterhouse-Five', 18, '1049', '1969-03-31', 6),
('Cats Cradle', 18, '1050', '1963-01-01', 5),

('Fahrenheit 451', 19, '1051', '1953-10-19', 7),
('The Martian Chronicles', 19, '1052', '1950-09-01', 4),

('Beloved', 20, '1053', '1987-09-16', 4),
('Song of Solomon', 20, '1054', '1977-09-01', 3),

('To the Lighthouse', 21, '1055', '1927-05-05', 3),
('Mrs. Dalloway', 21, '1056', '1925-05-14', 2),

('One Hundred Years of Solitude', 22, '1057', '1967-05-30', 9),
('Love in the Time of Cholera', 22, '1058', '1985-03-01', 5),

('The Grapes of Wrath', 23, '1059', '1939-04-14', 5),
('East of Eden', 23, '1060', '1952-09-19', 4),

('The Time Machine', 24, '1061', '1895-01-01', 8),
('The Invisible Man', 24, '1062', '1897-01-01', 6),

('Rebecca', 25, '1063', '1938-04-01', 6),
('Jamaica Inn', 25, '1064', '1936-01-01', 5),

('Do Androids Dream of Electric Sheep?', 26, '1065', '1968-01-01', 7),
('The Man in the High Castle', 26, '1066', '1962-11-01', 4),

('Fight Club', 27, '1067', '1996-08-17', 5),
('Choke', 27, '1068', '2001-09-17', 3),

('The Road', 28, '1069', '2006-09-26', 5),
('No Country for Old Men', 28, '1070', '2005-07-01', 4),

('White Teeth', 29, '1071', '2000-01-01', 10),
('On Beauty', 29, '1072', '2005-09-01', 6),

('The Night Circus', 30, '1073', '2011-09-13', 8),
('The Starless Sea', 30, '1074', '2019-11-05', 5),

('The Kite Runner', 31, '1075', '2003-05-29', 6),
('A Thousand Splendid Suns', 31, '1076', '2007-05-21', 4),

('The Alchemist', 32, '1077', '1988-05-01', 9),
('Brida', 32, '1078', '1990-01-01', 5),

('The Fault in Our Stars', 33, '1079', '2012-01-10', 7),
('Looking for Alaska', 33, '1080', '2005-03-03', 4),

('The Book Thief', 34, '1081', '2005-03-01', 5),
('I Am the Messenger', 34, '1082', '2002-05-01', 3),

('The Lovely Bones', 35, '1083', '2002-01-01', 4),
('Lucky', 35, '1084', '2005-01-01', 2),

('The Secret History', 36, '1085', '1992-09-01', 6),
('The Goldfinch', 36, '1086', '2013-10-22', 3),

('The Immortalists', 38, '1087', '2018-01-30', 8),
('Little Fires Everywhere', 39, '1088', '2017-09-12', 5),
('Where the Crawdads Sing', 40, '1089', '2018-08-14', 7),
('Circe', 41, '1090', '2018-04-10', 6),
('The Vanishing Half', 42, '1091', '2020-06-02', 4),
('Anxious People', 43, '1092', '2020-08-27', 5),
('The Midnight Library', 44, '1093', '2020-09-29', 8),
('The Seven Husbands of Evelyn Hugo', 45, '1094', '2017-06-13', 9),
('The Invisible Life of Addie LaRue', 46, '1095', '2020-10-06', 6),
('The Song of Achilles', 47, '1096', '2011-09-20', 5),
('The Nightingale', 48, '1097', '2015-02-03', 4),
('The Help', 49, '1098', '2009-02-10', 7),
('A Man Called Ove', 50, '1099', '2012-08-30', 8),
('The House in the Cerulean Sea', 51, '1100', '2020-03-17', 6),
('The Overstory', 52, '1101', '2018-04-03', 5),
('Normal People', 53, '1102', '2018-08-28', 4),
('The Testaments', 54, '1103', '2019-09-10', 3),
('The Water Dancer', 55, '1104', '2019-09-24', 7),
('The Dutch House', 56, '1105', '2019-09-24', 6),
('The Tattooist of Auschwitz', 57, '1106', '2018-01-11', 5),
('The Light We Lost', 58, '1107', '2017-04-04', 8),
('The Silent Patient', 59, '1108', '2019-02-05', 7),
('The Guest List', 60, '1109', '2020-06-02', 6),
('The Chain', 61, '1110', '2019-07-09', 5),
('Big Little Lies', 62, '1111', '2014-07-29', 4),
('The Girl on the Train', 63, '1112', '2015-01-13', 3),
('The Woman in the Window', 64, '1113', '2018-01-02', 8),
('The Couple Next Door', 65, '1114', '2016-01-12', 6),
('Behind Closed Doors', 66, '1115', '2016-02-11', 5),
('The Wife Between Us', 67, '1116', '2018-01-09', 4),
('Then She Was Gone', 68, '1117', '2017-04-20', 3),
('The Last Thing He Told Me', 69, '1118', '2021-05-04', 7),
('The Other Mrs.', 70, '1119', '2020-02-18', 6),
('The Family Upstairs', 71, '1120', '2019-06-11', 5),
('The Perfect Nanny', 72, '1121', '2016-08-25', 4),
('The Last Mrs. Parrish', 73, '1122', '2017-11-21', 8),
('The Girl with the Dragon Tattoo', 76, '1123', '2005-08-01', 5),
('The Cuckoos Calling', 77, '1124', '2013-04-30', 4),
('The Casual Vacancy', 78, '1125', '2012-09-27', 3),
('The Silkworm', 79, '1126', '2014-06-19', 7),
('Career of Evil', 80, '1127', '2015-10-20', 6),
('Lethal White', 81, '1128', '2020-09-29', 5),
('Troubled Blood', 82, '1129', '2020-09-15', 4),
('The Ink Black Heart', 83, '1130', '2022-08-30', 3),
('The Last Wish', 84, '1131', '1993-01-01', 8),
('Sword of Destiny', 85, '1132', '2015-03-01', 6),
('Blood of Elves', 86, '1133', '2009-08-01', 5),
('Time of Contempt', 87, '1134', '2013-08-01', 4),
('Baptism of Fire', 88, '1135', '2014-08-01', 3),
('The Tower of Swallows', 89, '1136', '2016-08-01', 7),
('The Lady of the Lake', 90, '1137', '2017-08-01', 6),
('Season of Storms', 91, '1138', '2018-05-01', 5),
('The Witcher: The Last Wish', 92, '1139', '2018-05-01', 4),
('The Witcher: Sword of Destiny', 93, '1140', '2018-05-01', 3),
('The Witcher: Blood of Elves', 94, '1141', '2018-05-01', 8),
('The Witcher: Time of Contempt', 95, '1142', '2018-05-01', 6),
('The Witcher: Baptism of Fire', 96, '1143', '2018-05-01', 5),
('The Witcher: The Tower of Swallows', 97, '1144', '2018-05-01', 4),
('The Witcher: The Lady of the Lake', 98, '1145', '2018-05-01', 3),
('The Witcher: Season of Storms', 99, '1146 ', '2018-05-01', 7),
('The Witcher: The Last Wish', 100, '1147', '2018-05-01', 6);

---Members Data-----------------------------------------------------
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

---Users Data-------------------------------------------------------
INSERT INTO users (username, password) VALUES
('admin', '123'),
('eron', '123'),
('paul', '123'),
('zio', '123'),
('jules', '123');

---BorrowedBooks Data------------------------------------------------
INSERT INTO BorrowedBooks (member_id, book_id, borrow_date, return_date) VALUES
(1, 1, '2023-01-15', '2023-02-15'),
(1, 8, '2023-01-20', '2023-02-20'),
(2, 3, '2023-02-01', '2023-03-01'),
(2, 10, '2023-02-10', '2023-03-10'),
(3, 5, '2023-01-25', '2023-02-25'),
(3, 18, '2023-02-15', '2023-03-15'),
(4, 6, '2023-01-30', '2023-02-28'),
(4, 22, '2023-02-05', '2023-03-05'),
(5, 12, '2023-01-10', '2023-02-10'),
(5, 19, '2023-02-12', '2023-03-12'),
(6, 2, '2023-01-18', '2023-02-18'),
(6, 20, '2023-02-20', '2023-03-20'),
(7, 15, '2023-01-22', '2023-02-22'),
(7, 25, '2023-02-02', '2023-03-02'),
(8, 4, '2023-01-28', '2023-02-28'),
(8, 16, '2023-02-14', '2023-03-14'),
(9, 14, '2023-01-12', '2023-02-12'),
(9, 11, '2023-02-03', '2023-03-03'),
(10, 17, '2023-01-05', '2023-02-05'),
(10, 21, '2023-02-08', '2023-03-08'),
(1, 9, '2023-01-19', '2023-02-19'),
(2, 13, '2023-01-15', '2023-02-15'),
(3, 7, '2023-01-25', '2023-02-25'),
(4, 23, '2023-01-30', '2023-02-28'),
(5, 24, '2023-01-18', '2023-02-18'),
(6, 26, '2023-02-01', '2023-03-01'),
(7, 27, '2023-01-22', '2023-02-22'),
(8, 28, '2023-02-10', '2023-03-10'),
(9, 29, '2023-01-12', '2023-02-12'),
(10, 30, '2023-02-05', '2023-03-05'),
(1, 31, '2023-01-15', '2023-02-15'),
(2, 32, '2023-01-20', '2023-02-20');

------------------------------------------------------------------
---VIEW-----------------------------------------------------------
------------------------------------------------------------------

--View for displaying member details with borrowed books, borrow dates, and return dates
CREATE VIEW member_details_view AS
SELECT 
    m.member_id, 
    m.member_name, 
    GROUP_CONCAT(b.title, ', ') AS borrowed_books,
    GROUP_CONCAT(bb.borrow_date, ', ') AS borrow_dates,
    GROUP_CONCAT(bb.return_date, ', ') AS return_dates
FROM 
    members m
LEFT JOIN 
    BorrowedBooks bb 
ON 
    m.member_id = bb.member_id
LEFT JOIN 
    Books b 
ON 
    bb.book_id = b.book_id
GROUP BY 
    m.member_id, m.member_name;
