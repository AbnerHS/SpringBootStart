CREATE TABLE `person` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(80) NOT NULL,
  `last_name` varchar(80) NOT NULL,
  `address` varchar(80) NOT NULL,
  `gender` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
);