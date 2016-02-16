CREATE TABLE User (
  `id`            INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_nickname` VARCHAR(20)  NOT NULL UNIQUE,
  `email`         VARCHAR(254)  NOT NULL UNIQUE,
  `games_count`   INT          NOT NULL DEFAULT '0',
  `wins_count`    INT          NOT NULL DEFAULT '0',
  `password`      VARCHAR(20)  NOT NULL,
  `raiting`       INT          NOT NULL DEFAULT '0'
)
  ENGINE = InnoDB;

CREATE TABLE Game (
  `id`              INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `winner_id`       INT UNSIGNED NOT NULL,
  `loser_id`        INT UNSIGNED NOT NULL,
  `winner_eff`      INT          NOT NULL DEFAULT '0',
  `loser_eff`       INT          NOT NULL DEFAULT '0',
  `winner_nickname` VARCHAR(20)  NOT NULL,
  `loser_nickname`  VARCHAR(20)  NOT NULL
)
  ENGINE = InnoDB;


