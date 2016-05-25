CREATE TABLE rings (
	id VARCHAR(30) NOT NULL PRIMARY KEY,
	member1 VARCHAR(30) NOT NULL,
	member2 VARCHAR(30) NOT NULL,
	member3 VARCHAR(30) NOT NULL);
	
CREATE TABLE topics (
	id varchar(30) NOT NULL PRIMARY KEY,
	private varchar(6) NOT NULL,
	type varchar(10) NOT NULL,
	question varchar(40));
	
CREATE TABLE options (
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    topic varchar(30), 
    opt varchar(30) NOT NULL,
    FOREIGN KEY (topic) 
        REFERENCES topics(id)
        ON DELETE CASCADE);

CREATE TABLE multiple_votes (
    topic varchar(30),
	opt INT, 
    email varchar(30) NOT NULL,
    PRIMARY KEY(topic, opt, email),
    FOREIGN KEY (opt) 
        REFERENCES options(id)
        ON DELETE CASCADE,
    FOREIGN KEY (topic) 
        REFERENCES topics(id)
        ON DELETE CASCADE);

CREATE TABLE unique_votes (
    topic varchar(30),
	opt INT, 
    email varchar(30) NOT NULL,
    PRIMARY KEY(topic, email),
    FOREIGN KEY (opt) 
        REFERENCES options(id)
        ON DELETE CASCADE,
    FOREIGN KEY (topic) 
        REFERENCES topics(id)
        ON DELETE CASCADE);

CREATE TABLE given_votes (
    topic varchar(30) NOT NULL,
	opt INT, 
    email_user varchar(30) NOT NULL,
    email_member varchar(30) NOT NULL);