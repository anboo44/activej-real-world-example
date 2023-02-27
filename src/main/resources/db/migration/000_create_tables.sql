-- Create user_group table
CREATE TABLE `user_group`
(
    `id`         int          NOT NULL AUTO_INCREMENT,
    `name`       varchar(255) NOT NULL,
    `group_type` varchar(10)  NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB default CHARSET=utf8mb4 default collate utf8mb4_general_ci;

-- Create user table
CREATE TABLE `user`
(
    `id`       int          NOT NULL AUTO_INCREMENT,
    `name`     varchar(255) NOT NULL,
    `age`      int          NOT NULL,
    `group_id` int DEFAULT NULL,
    PRIMARY KEY (`id`),
    foreign key (`group_id`) references user_group (id),
    KEY        `idx_user_group_id` (`group_id`)
) ENGINE=InnoDB default CHARSET=utf8mb4 default collate utf8mb4_general_ci;