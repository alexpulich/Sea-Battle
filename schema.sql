CREATE TABLE User (
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`user_nickname` varchar(20) NOT NULL UNIQUE,
	`email` varchar(20) NOT NULL UNIQUE,
	`games_count` INT NOT NULL DEFAULT '0',
	`wins_count` INT NOT NULL DEFAULT '0',
	`password` varchar(20) NOT NULL,
	`raiting` INT NOT NULL DEFAULT '0'
) ENGINE=InnoDB;

CREATE TABLE Game (
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`winner_id` INT UNSIGNED NOT NULL,
	`loser_id` INT UNSIGNED NOT NULL,
	`winner_eff` INT NOT NULL DEFAULT '0',
	`loser_eff` INT NOT NULL DEFAULT '0',
	`loser_nickname` varchar(20) NOT NULL,
	`winner_nickname` varchar(20) NOT NULL
) ENGINE=InnoDB;


