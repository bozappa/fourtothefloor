-- phpMyAdmin SQL Dump
-- version 5.0.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: May 05, 2020 at 09:22 PM
-- Server version: 10.4.11-MariaDB
-- PHP Version: 7.4.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `FourToTheFloor`
--

-- --------------------------------------------------------

--
-- Table structure for table `comments`
--

CREATE TABLE `comments` (
  `cid` int(11) NOT NULL,
  `comment` text NOT NULL,
  `commentBy` varchar(255) NOT NULL,
  `commentDate` timestamp NOT NULL DEFAULT current_timestamp(),
  `superParentId` int(11) NOT NULL,
  `parentId` int(11) NOT NULL,
  `hasSubComment` tinyint(1) NOT NULL DEFAULT 0,
  `level` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `friends`
--

CREATE TABLE `friends` (
  `friendId` int(11) NOT NULL,
  `userId` varchar(255) NOT NULL,
  `profileId` varchar(255) NOT NULL,
  `friendOn` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `nid` int(11) NOT NULL,
  `notificationTo` varchar(255) NOT NULL,
  `notificationFrom` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  `notificationTiime` timestamp NOT NULL DEFAULT current_timestamp(),
  `postId` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `posts`
--

CREATE TABLE `posts` (
  `postId` int(11) NOT NULL,
  `postUserId` varchar(255) NOT NULL,
  `post` text NOT NULL,
  `statusImage` text NOT NULL,
  `statusTime` timestamp NOT NULL DEFAULT current_timestamp(),
  `likeCount` int(11) NOT NULL,
  `commentCount` int(11) NOT NULL,
  `hasComment` tinyint(1) NOT NULL DEFAULT 0,
  `privacy` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Triggers `posts`
--
DELIMITER $$
CREATE TRIGGER `alert_trigger` AFTER INSERT ON `posts` FOR EACH ROW BEGIN
DECLARE done INT DEFAULT FALSE;
DECLARE ids VARCHAR(255);
DECLARE privacyLevel INT;

DECLARE cur CURSOR FOR SELECT profileId FROM friends WHERE userId = new.postUserId;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

OPEN cur;
ins_loop:LOOP
FETCH cur INTO ids;
IF done THEN
LEAVE ins_loop;
END IF;


SELECT privacy INTO privacyLevel FROM posts WHERE postId = new.postId AND privacy = new.privacy;
IF ( privacyLevel = 0 OR privacylevel = 2) THEN


INSERT INTO timeline VALUES (null, ids, new.postId,CURRENT_TIMESTAMP);

END IF;



END LOOP;
CLOSE cur;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `requests`
--

CREATE TABLE `requests` (
  `requestId` int(11) NOT NULL,
  `sender` varchar(255) NOT NULL,
  `receiver` varchar(255) NOT NULL,
  `requestDate` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `timeline`
--

CREATE TABLE `timeline` (
  `tid` int(11) NOT NULL,
  `whoseTimeLine` varchar(255) NOT NULL,
  `postId` int(11) NOT NULL,
  `statusTime` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `userPostLikes`
--

CREATE TABLE `userPostLikes` (
  `likeId` int(11) NOT NULL,
  `likeBy` varchar(255) NOT NULL,
  `postOn` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `uid` varchar(255) NOT NULL,
  `name` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `profileUrl` text NOT NULL,
  `coverUrl` text NOT NULL,
  `userToken` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`cid`);

--
-- Indexes for table `friends`
--
ALTER TABLE `friends`
  ADD PRIMARY KEY (`friendId`),
  ADD UNIQUE KEY `userId` (`userId`,`profileId`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`nid`);

--
-- Indexes for table `posts`
--
ALTER TABLE `posts`
  ADD PRIMARY KEY (`postId`);

--
-- Indexes for table `requests`
--
ALTER TABLE `requests`
  ADD PRIMARY KEY (`requestId`);

--
-- Indexes for table `timeline`
--
ALTER TABLE `timeline`
  ADD PRIMARY KEY (`tid`),
  ADD UNIQUE KEY `whoseTimeLine` (`whoseTimeLine`,`postId`);

--
-- Indexes for table `userPostLikes`
--
ALTER TABLE `userPostLikes`
  ADD PRIMARY KEY (`likeId`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`uid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `comments`
--
ALTER TABLE `comments`
  MODIFY `cid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `friends`
--
ALTER TABLE `friends`
  MODIFY `friendId` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `nid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `posts`
--
ALTER TABLE `posts`
  MODIFY `postId` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `requests`
--
ALTER TABLE `requests`
  MODIFY `requestId` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `timeline`
--
ALTER TABLE `timeline`
  MODIFY `tid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `userPostLikes`
--
ALTER TABLE `userPostLikes`
  MODIFY `likeId` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
