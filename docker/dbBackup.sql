CREATE DATABASE IF NOT EXISTS `UserSBChallenge`;
USE `UserSBChallenge`;

CREATE TABLE `UserSBChallenge`.`users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `password_salt` varchar(255) NOT NULL,
  `birthdate` date NOT NULL,
  `created_on` datetime(3) NOT NULL DEFAULT NOW(3),
  `updated_on` datetime(3) NOT NULL DEFAULT NOW(3),
  `is_active` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `UserSBChallenge`.`audit` (
  `id` int NOT NULL AUTO_INCREMENT,
  `process_uuid` varchar(100) DEFAULT NULL,
  `process` varchar(100) DEFAULT NULL,
  `status` varchar(100) DEFAULT NULL,
  `audit` blob DEFAULT NULL,
  `created_on` datetime(3) NOT NULL DEFAULT NOW(3),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;