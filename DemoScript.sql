USE [master]
GO

-- Create Database
CREATE DATABASE [YuGiOhTrackerDemo_S2G4]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'YuGiOhTrackerDemo_S2G4', FILENAME = N'/var/opt/mssql/data/YuGiOhTrackerDemo_S2G4.mdf' , SIZE = 48512KB , MAXSIZE = UNLIMITED, FILEGROWTH = 10%)
 LOG ON 
( NAME = N'YuGiOhTrackerDemo_S2G4_log', FILENAME = N'/var/opt/mssql/data/YuGiOhTrackerDemo_S2G4_log.ldf' , SIZE = 149568KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO

USE [YuGiOhTrackerDemo_S2G4]
GO

-- Create Table User
CREATE TABLE [dbo].[User](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[PasswordSalt] [varchar](50) NOT NULL,
	[PasswordHash] [varchar](50) NOT NULL,
	[Username] [varchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [Username_Unique] UNIQUE NONCLUSTERED 
(
	[Username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

--Create Table Set
CREATE TABLE [dbo].[Set](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[NAME] [nvarchar](100) NOT NULL,
	[SetCode] [varchar](10) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

--Create Table TradeInfo
CREATE TABLE [dbo].[TradeInfo](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[IsComplete] [bit] NOT NULL,
	[SenderConfirmed] [bit] NOT NULL,
	[ReceiverConfirmed] [bit] NOT NULL,
	[DateCreated] [date] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

-- Create Table Card
CREATE TABLE [dbo].[Card](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](70) NOT NULL,
	[Code] [varchar](20) NOT NULL,
	[Rarity] [varchar](40) NOT NULL,
	[Description] [nvarchar](max) NOT NULL,
	[MarketPrice] [money] NULL,
	[Type] [varchar](100) NOT NULL,
	[ATK] [int] NULL,
	[DEF] [int] NULL,
	[Level] [int] NULL,
	[Race] [varchar](30) NULL,
	[Attribute] [varchar](10) NULL,
	[ImageURL] [varchar](512) NULL,
	[SetID] [int] NOT NULL,
 CONSTRAINT [PK__Card__3214EC2784BA1FFD] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

ALTER TABLE [dbo].[Card]  WITH CHECK ADD  CONSTRAINT [FK__Card__SetID__76969D2E] FOREIGN KEY([SetID])
REFERENCES [dbo].[Set] ([ID])
GO

ALTER TABLE [dbo].[Card] CHECK CONSTRAINT [FK__Card__SetID__76969D2E]
GO

ALTER TABLE [dbo].[Card]  WITH CHECK ADD  CONSTRAINT [CK__Card__ImageURL__75A278F5] CHECK  (([ImageURL] like 'https://%' OR [ImageURL] like 'http://%'))
GO

ALTER TABLE [dbo].[Card] CHECK CONSTRAINT [CK__Card__ImageURL__75A278F5]
GO

ALTER TABLE [dbo].[Card]  WITH CHECK ADD  CONSTRAINT [CK__Card__MarketPric__74AE54BC] CHECK  (([MarketPrice]>(0.00)))
GO

ALTER TABLE [dbo].[Card] CHECK CONSTRAINT [CK__Card__MarketPric__74AE54BC]
GO

-- Create Table Collection
CREATE TABLE [dbo].[Collection](
	[ID] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](200) NOT NULL,
	[UserID] [int] NOT NULL,
	[Description] [nvarchar](200) NULL,
PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[Collection]  WITH CHECK ADD FOREIGN KEY([UserID])
REFERENCES [dbo].[User] ([ID])
GO

--Create Table Seller
CREATE TABLE [dbo].[Seller](
	[ID] [int] NOT NULL,
	[StoreName] [nvarchar](500) NOT NULL,
	[Address] [nvarchar](300) NOT NULL,
	[City] [nvarchar](50) NOT NULL,
	[State] [char](2) NOT NULL,
	[ZipCode] [nchar](5) NOT NULL,
	[Description] [nvarchar](1000) NULL,
	[Phone] [nvarchar](24) NOT NULL,
 CONSTRAINT [PK__Seller__3214EC27357F3EF5] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[Seller]  WITH CHECK ADD  CONSTRAINT [FK__Seller__ID__71D1E811] FOREIGN KEY([ID])
REFERENCES [dbo].[User] ([ID])
GO

ALTER TABLE [dbo].[Seller] CHECK CONSTRAINT [FK__Seller__ID__71D1E811]
GO

ALTER TABLE [dbo].[Seller]  WITH CHECK ADD  CONSTRAINT [Phone#] CHECK  (([Phone] like '___-___-____'))
GO

ALTER TABLE [dbo].[Seller] CHECK CONSTRAINT [Phone#]
GO

ALTER TABLE [dbo].[Seller]  WITH CHECK ADD  CONSTRAINT [StateAbbreviation] CHECK  (([State]='WY' OR [State]='WI' OR [State]='WV' OR [State]='WA' OR [State]='VA' OR [State]='VT' OR [State]='UT' OR [State]='TX' OR [State]='TN' OR [State]='SD' OR [State]='SC' OR [State]='RI' OR [State]='PA' OR [State]='OR' OR [State]='OK' OR [State]='OH' OR [State]='ND' OR [State]='NC' OR [State]='NY' OR [State]='NM' OR [State]='NJ' OR [State]='NH' OR [State]='NV' OR [State]='NE' OR [State]='MT' OR [State]='MO' OR [State]='MS' OR [State]='MN' OR [State]='MI' OR [State]='MA' OR [State]='MD' OR [State]='ME' OR [State]='LA' OR [State]='KY' OR [State]='KS' OR [State]='IA' OR [State]='IN' OR [State]='IL' OR [State]='ID' OR [State]='HI' OR [State]='GA' OR [State]='FL' OR [State]='DE' OR [State]='CT' OR [State]='CO' OR [State]='CA' OR [State]='AR' OR [State]='AZ' OR [State]='AK' OR [State]='AL'))
GO

ALTER TABLE [dbo].[Seller] CHECK CONSTRAINT [StateAbbreviation]
GO

ALTER TABLE [dbo].[Seller]  WITH CHECK ADD  CONSTRAINT [ZipCodeOnlyDigits] CHECK  (([ZipCode] like '[0-9][0-9][0-9][0-9][0-9]'))
GO

ALTER TABLE [dbo].[Seller] CHECK CONSTRAINT [ZipCodeOnlyDigits]
GO

--Create Table InCollection
CREATE TABLE [dbo].[InCollection](
	[CollectionID] [int] NOT NULL,
	[CardID] [int] NOT NULL,
	[Quantity] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[CollectionID] ASC,
	[CardID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[InCollection]  WITH CHECK ADD  CONSTRAINT [FK__InCollect__CardI__02084FDA] FOREIGN KEY([CardID])
REFERENCES [dbo].[Card] ([ID])
GO

ALTER TABLE [dbo].[InCollection] CHECK CONSTRAINT [FK__InCollect__CardI__02084FDA]
GO

ALTER TABLE [dbo].[InCollection]  WITH CHECK ADD FOREIGN KEY([CollectionID])
REFERENCES [dbo].[Collection] ([ID])
GO

ALTER TABLE [dbo].[InCollection]  WITH CHECK ADD CHECK  (([Quantity]>(0)))
GO

--Create Table InTrade
CREATE TABLE [dbo].[InTrade](
	[CardID] [int] NOT NULL,
	[TradeInfoID] [int] NOT NULL,
	[ProviderID] [int] NOT NULL,
	[Quantity] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[CardID] ASC,
	[TradeInfoID] ASC,
	[ProviderID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[InTrade]  WITH CHECK ADD  CONSTRAINT [FK__InTrade__CardID__123EB7A3] FOREIGN KEY([CardID])
REFERENCES [dbo].[Card] ([ID])
GO

ALTER TABLE [dbo].[InTrade] CHECK CONSTRAINT [FK__InTrade__CardID__123EB7A3]
GO

ALTER TABLE [dbo].[InTrade]  WITH CHECK ADD FOREIGN KEY([ProviderID])
REFERENCES [dbo].[User] ([ID])
GO

ALTER TABLE [dbo].[InTrade]  WITH CHECK ADD FOREIGN KEY([TradeInfoID])
REFERENCES [dbo].[TradeInfo] ([ID])
GO

ALTER TABLE [dbo].[InTrade]  WITH CHECK ADD FOREIGN KEY([TradeInfoID])
REFERENCES [dbo].[TradeInfo] ([ID])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[InTrade]  WITH CHECK ADD CHECK  (([Quantity]>(0)))
GO

--Create Table ListingCard
CREATE TABLE [dbo].[ListingCard](
	[SellerID] [int] NOT NULL,
	[CardID] [int] NOT NULL,
	[Quantity] [int] NOT NULL,
	[Price] [money] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[SellerID] ASC,
	[CardID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[ListingCard]  WITH CHECK ADD  CONSTRAINT [FK__ListingCa__CardI__08B54D69] FOREIGN KEY([CardID])
REFERENCES [dbo].[Card] ([ID])
GO

ALTER TABLE [dbo].[ListingCard] CHECK CONSTRAINT [FK__ListingCa__CardI__08B54D69]
GO

ALTER TABLE [dbo].[ListingCard]  WITH CHECK ADD  CONSTRAINT [FK__ListingCa__Selle__07C12930] FOREIGN KEY([SellerID])
REFERENCES [dbo].[Seller] ([ID])
GO

ALTER TABLE [dbo].[ListingCard] CHECK CONSTRAINT [FK__ListingCa__Selle__07C12930]
GO

ALTER TABLE [dbo].[ListingCard]  WITH CHECK ADD CHECK  (([Price]>(0.00)))
GO

ALTER TABLE [dbo].[ListingCard]  WITH CHECK ADD CHECK  (([Quantity]>(0)))
GO

--Create Table TradeRequest
CREATE TABLE [dbo].[TradeRequest](
	[TradeInfoID] [int] NOT NULL,
	[SenderID] [int] NOT NULL,
	[ReceiverID] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[TradeInfoID] ASC,
	[SenderID] ASC,
	[ReceiverID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[TradeRequest]  WITH CHECK ADD FOREIGN KEY([ReceiverID])
REFERENCES [dbo].[User] ([ID])
GO

ALTER TABLE [dbo].[TradeRequest]  WITH CHECK ADD FOREIGN KEY([SenderID])
REFERENCES [dbo].[User] ([ID])
GO

ALTER TABLE [dbo].[TradeRequest]  WITH CHECK ADD FOREIGN KEY([TradeInfoID])
REFERENCES [dbo].[TradeInfo] ([ID])
GO

ALTER TABLE [dbo].[TradeRequest]  WITH CHECK ADD FOREIGN KEY([TradeInfoID])
REFERENCES [dbo].[TradeInfo] ([ID])
ON DELETE CASCADE
GO

--Create Table UserHas
CREATE TABLE [dbo].[UserHas](
	[UserID] [int] NOT NULL,
	[CardID] [int] NOT NULL,
	[Quantity] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[UserID] ASC,
	[CardID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[UserHas]  WITH CHECK ADD  CONSTRAINT [FK__UserHas__CardID__7D439ABD] FOREIGN KEY([CardID])
REFERENCES [dbo].[Card] ([ID])
GO

ALTER TABLE [dbo].[UserHas] CHECK CONSTRAINT [FK__UserHas__CardID__7D439ABD]
GO

ALTER TABLE [dbo].[UserHas]  WITH CHECK ADD FOREIGN KEY([UserID])
REFERENCES [dbo].[User] ([ID])
GO

ALTER TABLE [dbo].[UserHas]  WITH CHECK ADD FOREIGN KEY([UserID])
REFERENCES [dbo].[User] ([ID])
GO

ALTER TABLE [dbo].[UserHas]  WITH CHECK ADD FOREIGN KEY([UserID])
REFERENCES [dbo].[User] ([ID])
GO

ALTER TABLE [dbo].[UserHas]  WITH CHECK ADD CHECK  (([Quantity]>=(0)))
GO

ALTER TABLE [dbo].[UserHas]  WITH CHECK ADD CHECK  (([Quantity]>=(0)))
GO

ALTER TABLE [dbo].[UserHas]  WITH CHECK ADD CHECK  (([Quantity]>=(0)))
GO

ALTER TABLE [dbo].[UserHas]  WITH CHECK ADD CHECK  (([Quantity]>(0)))
GO

--Create Procedure AddCardIntoCollection
CREATE   PROCEDURE [dbo].[AddCardIntoCollection]
(
@CollectionID int,
@CardID int,
@Username varchar(100)
)
AS
BEGIN

IF (Not Exists (Select * From [Collection] Where ID = @CollectionID))
	BEGIN;
		THROW 51000, 'Invalid Parameter: CollectionID does not exist.',1
	END
IF (Not Exists (Select * From [Card] Where ID = @CardID))
	BEGIN;
		THROW 51001, 'Invalid Parameter: CardID does not exist.',1
	END

IF (NOT EXISTS (Select * From [User] Where Username = @Username))
	BEGIN;
		THROW 51000, 'Invalid Parameter: Username does not exist.', 1
	END

DECLARE @UserID int
SELECT @UserID = ID FROM [User] WHERE Username = @Username

DECLARE @UserQuantity int

IF (Not Exists (Select * FROM [UserHas] WHERE UserID = @UserID AND CardID = @CardID))
	BEGIN;
		THROW 51000, 'User does not own card', 1
	END
	ELSE
	BEGIN
		SELECT @UserQuantity = Quantity FROM UserHas WHERE UserID = @UserID AND CardID = @CardID
	END

DECLARE @CollectionQuantity int

--Check if card is already in collection
IF (EXISTS (SELECT * FROM InCollection WHERE CollectionID = @CollectionID AND CardID = @CardID))
	BEGIN
	SELECT @CollectionQuantity = Quantity FROM InCollection WHERE CollectionID = @CollectionID AND CardID = @CardID
	IF (@CollectionQuantity + 1 <= @UserQuantity)
		BEGIN
			UPDATE InCollection
			SET Quantity = @CollectionQuantity + 1
			WHERE CollectionID = @CollectionID AND CardID = @CardID;
		END
	ELSE
		BEGIN;
			THROW 51000, 'Quantity of card in collection cannot exceed quantity of card user owns', 1
		END
	END
ELSE
	IF (@UserQuantity > 0)
		BEGIN
			INSERT INTO InCollection (CollectionID, CardID, Quantity)
			VALUES (@CollectionID, @CardID, 1)
		END
	ELSE
		BEGIN;
			THROW 51000, 'Quantity of card in collection cannot exceed quantity of card user owns', 1
		END

END
GO

--Create Procedure [AddCardToListing]
CREATE   PROCEDURE [dbo].[AddCardToListing]
(	@CardID int,
	@Username varchar(100)
)
AS
BEGIN

IF (Not Exists (Select * From [Card] Where ID = @CardID))
	BEGIN;
		THROW 51001, 'Invalid Parameter: CardID does not exist.',1
	END
IF (NOT EXISTS (Select * From [User] Where Username = @Username))
	BEGIN;
		THROW 51000, 'Invalid Parameter: Username does not exist.', 1
	END

DECLARE @UserID int
SELECT @UserID = ID FROM [User] WHERE Username = @Username

DECLARE @UserQuantity int

IF (Not Exists (Select * FROM [UserHas] WHERE UserID = @UserID AND CardID = @CardID))
	BEGIN;
		THROW 51000, 'User does not own card', 1
	END
	ELSE
	BEGIN
		SELECT @UserQuantity = Quantity FROM UserHas WHERE UserID = @UserID AND CardID = @CardID
	END

DECLARE @ListingQuantity int
SELECT @ListingQuantity = Quantity FROM ListingCard WHERE SellerID = @UserID AND CardID = @CardID
IF (@ListingQuantity is null)
	SET @ListingQuantity = 0

DECLARE @TradeRequestQuantity int
SELECT @TradeRequestQuantity = SUM(Quantity)
FROM InTrade
WHERE CardID = @CardID and ProviderID = @UserID

IF (@UserQuantity - @TradeRequestQuantity <= @ListingQuantity)
	BEGIN;
		THROW 51000, 'Card exists in your trade requests', 1
	END;

--Check if card is already in a listing
IF (EXISTS (SELECT * FROM ListingCard WHERE SellerID = @UserID AND CardID = @CardID))
	BEGIN
	SELECT @ListingQuantity = Quantity FROM ListingCard WHERE SellerID = @UserID AND CardID = @CardID
	IF (@ListingQuantity + 1 <= @UserQuantity)
		BEGIN
			UPDATE ListingCard
			SET Quantity = @ListingQuantity + 1
			WHERE SellerID = @UserID AND CardID = @CardID;
		END
	ELSE
		BEGIN;
			THROW 51000, 'Quantity of card in listing cannot exceed quantity of card user owns', 1
		END
	END
ELSE
	IF (@UserQuantity > 0)
		BEGIN
			INSERT INTO ListingCard (SellerID, CardID, Quantity, Price)
			VALUES (@UserID, @CardID, 1, 0.01)
		END
	ELSE
		BEGIN;
			THROW 51000, 'Quantity of card in listing cannot exceed quantity of card user owns', 1
		END

END
GO

--Create Procedure [AddCardToOwned]
CREATE   PROCEDURE [dbo].[AddCardToOwned]
(
@CardID int,
@Username varchar(100)
)
AS
BEGIN

IF (Not Exists (Select * From [Card] Where ID = @CardID))
	BEGIN;
		THROW 51000, 'Invalid Parameter: CardID does not exist.',1
	END

IF (NOT EXISTS (Select * From [User] Where Username = @Username))
	BEGIN;
		THROW 51001, 'Invalid Parameter: Username does not exist.', 1
	END

DECLARE @UserID int
SELECT @UserID = ID FROM [User] WHERE Username = @Username

--Check if card is already in collection
IF (EXISTS (SELECT * FROM UserHas WHERE UserID = @UserID AND CardID = @CardID))
	UPDATE UserHas
	SET Quantity = Quantity + 1
	WHERE CardID = @CardID AND UserID = @UserID
ELSE
	INSERT INTO UserHas(UserID, CardID, Quantity)
	VALUES(@UserID, @CardID, 1)
END
GO

--Create Procedure [CardOwnershipCheck]
CREATE   PROCEDURE [dbo].[CardOwnershipCheck]
(
@CardID int,
@Username varchar(100)
)
As
Begin
IF (Not Exists (Select * From [Card] Where ID = @CardID))
	BEGIN;
		THROW 51000, 'Invalid Parameter: CardID does not exist.',1
	END

IF (NOT EXISTS (Select * From [User] Where Username = @Username))
	BEGIN;
		THROW 51001, 'Invalid Parameter: Username does not exist.', 1
	END
IF (EXISTS (
        SELECT * FROM UserHas uh
        JOIN [User] u ON uh.UserID = u.ID
        WHERE u.Username = @Username AND uh.CardID = @CardID))
        SELECT 1 AS Result
    ELSE
        SELECT 0 AS Result
End
GO

--Create Procedure [ConfirmTrade]
CREATE   PROCEDURE [dbo].[ConfirmTrade]
(
@TradeID int,
@Username varchar(100)
)
AS
BEGIN
Declare @UserID int = (Select ID From [User] Where Username = @Username)

IF (Not Exists(Select * From TradeRequest Where TradeInfoID = @TradeID))
	BEGIN;
		Throw 51000, 'Invalid TradeInfoID', 1
	END
IF (Not Exists(Select * From TradeRequest Where (SenderID = @UserID OR ReceiverID = @UserID)))
	BEGIN;
		Throw 51001, 'Invalid UserID', 1
	END

IF ((Select SenderID From TradeRequest Where TradeInfoID = @TradeID) = @UserID)
	UPDATE TradeInfo
	SET SenderConfirmed = 1
	Where ID = @TradeID

	
IF ((Select ReceiverID From TradeRequest Where TradeInfoID = @TradeID) = @UserID)
	UPDATE TradeInfo
	SET ReceiverConfirmed = 1
	Where ID = @TradeID

Declare @IsCompleted bit
IF((Select SenderConfirmed From TradeInfo Where ID = @TradeID) = 1 AND 
   (Select ReceiverConfirmed From TradeInfo Where ID = @TradeID) = 1)
	Begin
		Set @IsCompleted = 1
	END
ELSE Set @IsCompleted = 0

UPDATE TradeInfo
SET IsComplete = @IsCompleted
Where ID = @TradeID

IF (@IsCompleted = 1)
BEGIN
	Declare @SenderID int, @ReceiverID int

	Select @SenderID = SenderID, @ReceiverID = ReceiverID
	From TradeRequest
	Where TradeInfoID = @TradeID

	--Delete Card from Sender
	DELETE uh
	From UserHas uh
	Join InTrade it on uh.CardID = it.CardID
	Where uh.UserID = @SenderID AND it.ProviderID = uh.UserID AND it.TradeInfoID = @TradeID
			AND uh.Quantity-it.Quantity = 0

	UPDATE uh
	SET uh.Quantity = uh.Quantity - it.Quantity
	From UserHas uh
	Join InTrade it on uh.CardID = it.CardID
	Where uh.UserID = @SenderID AND it.ProviderID = uh.UserID AND it.TradeInfoID = @TradeID
		AND uh.Quantity - it.Quantity > 0

	--Give Sender Card to Receiver
	UPDATE uh
	SET uh.Quantity = uh.Quantity + it.Quantity
	From Userhas uh
	Join InTrade it on uh.CardID = it.CardID
	Where uh.UserID = @ReceiverID AND it.ProviderID = @SenderID AND it.TradeInfoID = @TradeID
	
	INSERT UserHas (UserID, CardID, Quantity)
	Select @ReceiverID, it.CardID, it.Quantity
	From InTrade it
	Where it.ProviderID = @SenderID AND it.TradeInfoID = @TradeID AND Not Exists (Select * 
																				  From UserHas uh
																				  Where uh.UserID = @ReceiverID AND uh.CardID = it.CardID)

	--Delete Card from Receiver
	DELETE uh
	From UserHas uh
	Join InTrade it on uh.CardID = it.CardID
	Where uh.UserID = @ReceiverID AND it.ProviderID = uh.UserID AND it.TradeInfoID = @TradeID
			AND uh.Quantity-it.Quantity = 0

	UPDATE uh
	SET uh.Quantity = uh.Quantity - it.Quantity
	From UserHas uh
	Join InTrade it on uh.CardID = it.CardID
	Where uh.UserID = @ReceiverID AND it.ProviderID = uh.UserID AND it.TradeInfoID = @TradeID
		AND uh.Quantity - it.Quantity > 0

	--Give Receiver Card to Sender
	UPDATE uh
	SET uh.Quantity = uh.Quantity + it.Quantity
	From Userhas uh
	Join InTrade it on uh.CardID = it.CardID
	Where uh.UserID = @SenderID AND it.ProviderID = @ReceiverID AND it.TradeInfoID = @TradeID
	
	INSERT UserHas (UserID, CardID, Quantity)
	Select @SenderID, it.CardID, it.Quantity
	From InTrade it
	Where it.ProviderID = @ReceiverID AND it.TradeInfoID = @TradeID AND Not Exists (Select * 
																					From UserHas uh
																					Where uh.UserID = @SenderID AND uh.CardID = it.CardID)
	
END
END
GO

--Create Procedure [CreateCollection]
CREATE   PROCEDURE [dbo].[CreateCollection](
	@Username varchar(100),
	@Name nvarchar(200)
)
AS
BEGIN
	IF(@Username is null)
	BEGIN;
		Throw 51000, 'Invalid Parameter: Username cannot be null', 1
	END

	IF(@Name is null)
	BEGIN;
		Throw 51001, 'Invalid Parameter: Collection name cannot be null', 1
	END

	DECLARE @UserID int
	SELECT @UserID = ID FROM [User] WHERE Username = @Username

	IF(Not Exists (Select * 
				   From [User] 
				   Where ID = @UserID))
	BEGIN;
		Throw 51002, 'Invalid Parameter: UserID does not exist in User', 1
	END

	INSERT INTO [Collection]
	([Name], UserID)
	VALUES(@Name, @UserID)
END
GO

--Create Procedure [CreateTradeRequest]
CREATE   PROCEDURE [dbo].[CreateTradeRequest] (
	@SenderUsername varchar(100),
	@ReceiverUsername varchar(100))
AS
BEGIN

	IF(@SenderUsername is null OR @ReceiverUsername is null)
	BEGIN;
		Throw 51000, 'Invalid Parameter: Username cannot be null', 1
	END

	DECLARE @SenderID int
	SELECT @SenderID = ID FROM [User] WHERE Username = @SenderUsername

	DECLARE @ReceiverID int
	SELECT @ReceiverID = ID FROM [User] WHERE Username = @ReceiverUsername

	IF(Not Exists (Select * 
				   From [User] 
				   Where ID = @SenderID) OR Not Exists (Select * 
														From [User] 
														Where ID = @ReceiverID))
	BEGIN;
		Throw 51002, 'Invalid Parameter: UserID does not exist in User', 1
	END

	INSERT INTO [TradeInfo]
	(IsComplete, SenderConfirmed, ReceiverConfirmed, DateCreated)
	VALUES(0, 0, 0, GETDATE())

	DECLARE @TradeID int
	SET @TradeID = SCOPE_IDENTITY()

	INSERT INTO [TradeRequest]
	(TradeInfoID, SenderID, ReceiverID)
	VALUES(@TradeID, @SenderID, @ReceiverID)

END
GO

--Create Procedure [DecrementCardOffered]
CREATE   PROCEDURE [dbo].[DecrementCardOffered]
(
@TradeID int,
@CardID int,
@Username varchar(100)
)
AS
BEGIN
Declare @UserID int = (Select ID From [User] Where Username = @Username)

IF (Not Exists(Select * From InTrade Where TradeInfoID = @TradeID))
	BEGIN;
		Throw 51000, '[DecrementCardOffered]: Invalid TradeInfoID', 1
	END
IF (Not Exists(Select * From InTrade Where ProviderID = @UserID))
	BEGIN;
		Throw 51001, '[DecrementCardOffered]: Invalid UserID', 1
	END

IF ((Select IsComplete From TradeInfo Where ID = @TradeID) = 1)
	Throw 51002, 'Cannot Remove Card: Trade Already Completed, Please Refresh the Page', 1
IF ((Select SenderConfirmed From TradeInfo Where ID = @TradeID) = 1)
	Throw 51003, 'Cannot Remove Card: Trade Already Confirmed by the Sender, Please Refresh the Page', 1
IF ((Select ReceiverConfirmed From TradeInfo Where ID = @TradeID) = 1)
	Throw 51004, 'Cannot Remove Card: Trade Already Confirmed by the Receiver, Please Refresh the Page', 1

IF (Not Exists(Select * From InTrade Where CardID = @CardID)) 
	BEGIN;
		Throw 51005, '[DecrementCardOffered]: Invalid CardID', 1
	END

UPDATE InTrade 
SET Quantity = Quantity - 1
Where TradeInfoID = @TradeID AND ProviderID = @UserID AND CardID = @CardID
END
GO

--Create Procedure  [DecrementOwnedCard]
CREATE PROCEDURE [dbo].[DecrementOwnedCard]
(
@CardID int,
@Username varchar(100)
)
AS
BEGIN
	DECLARE @UserID int
	SELECT @UserID = ID FROM [User] WHERE Username = @Username

	DECLARE @Quantity int
	SELECT @Quantity = Quantity FROM UserHas WHERE CardID = @CardID AND UserID = @UserID

	IF (EXISTS (SELECT * FROM ListingCard WHERE SellerID = @UserID AND CardID = @CardID AND Quantity >= @Quantity))
		BEGIN;
		THROW 51000, 'Cannot Delete: The card exists in a listing', 1
		END

	IF(@Quantity > 1)
		UPDATE UserHas
		SET Quantity = Quantity - 1
		WHERE CardID = @CardID AND UserID = @UserID
	ELSE
		DELETE FROM [UserHas]
		WHERE CardID = @CardID AND UserID = @UserID
		
END
GO

--Create Procedure [DeleteCardFromCollection]
CREATE   PROCEDURE [dbo].[DeleteCardFromCollection]
(
@CollectionID int,
@CardID int
)
AS
BEGIN
	IF (Not Exists (Select * From [InCollection] Where CollectionID = @CollectionID AND CardID = @CardID))
		BEGIN;
			THROW 51000, 'Invalid Parameter: Card does not exist in collection.', 1
		END

	DECLARE @CollectionQuantity int
	SELECT @CollectionQuantity = Quantity FROM InCollection WHERE CollectionID = @CollectionID AND CardID = @CardID

	IF (@CollectionQuantity = 1)
		DELETE FROM [InCollection]
		WHERE CollectionID = @CollectionID AND CardID = @CardID
	ELSE
		UPDATE InCollection
		SET Quantity = @CollectionQuantity - 1
		WHERE CollectionID = @CollectionID AND CardID = @CardID
END
GO

--Create Procedure [DeleteCollection]
CREATE   PROCEDURE [dbo].[DeleteCollection](
	@CollectionID int
)
AS
BEGIN
	IF(@CollectionID is Null)
	BEGIN;
		Throw 51000, 'Invalid Parameter: CollectionID cannot be null', 1
	END

	IF(Not Exists (Select *
				  From [Collection]
				  Where ID = @CollectionID))
	BEGIN;
		Throw 51001, 'Invalid Parameter: CollectionID does not exist in Collection', 1
	END

	DELETE FROM [InCollection]
	WHERE CollectionID = @CollectionID

	DELETE FROM [Collection]
	WHERE ID = @CollectionID
END
GO

--Create Procedure [DeleteListing]
CREATE   PROCEDURE [dbo].[DeleteListing]
(	@CardID int,
	@Username varchar(100)
)
AS
BEGIN
	IF(@CardID is Null)
	BEGIN;
		Throw 51000, 'Invalid Parameter: CardID cannot be null', 1
	END
	IF(@Username is Null)
	BEGIN;
		Throw 51000, 'Invalid Parameter: Username cannot be null', 1
	END

	DECLARE @UserID int
	SELECT @UserID = ID FROM [User] WHERE Username = @Username
	
	IF(Not Exists (Select *
				  From Seller
				  Where ID = @UserID))
	BEGIN;
		Throw 51001, 'Invalid Parameter: Seller does not exist', 1
	END

	DELETE FROM [ListingCard]
	WHERE SellerID = @UserID AND CardID = @CardID

END
GO

--Create Procedure [DeleteTradeRequest]
CREATE PROCEDURE [dbo].[DeleteTradeRequest](
	@TradeRequestID int
)
AS
BEGIN
IF(@TradeRequestID is Null)
	BEGIN;
		Throw 51000, '[DeleteTradeRequest]: TradeInfoID is Null', 1
	END

IF(Not Exists (Select *
				  From [TradeInfo]
				  Where ID = @TradeRequestID))
	BEGIN;
		Throw 51001, '[DeleteTradeRequest]: Invalid TradeInfoID', 1
	END

IF ((Select IsComplete From TradeInfo Where ID = @TradeRequestID) = 1)
	Throw 51002, 'Cannot Abort the Trade: Trade Already Completed, Please Refresh the Page', 1

DELETE FROM [TradeInfo]
	WHERE ID = @TradeRequestID
END
GO

--Create Procedure [GetAllTradeInfo]
CREATE   PROCEDURE [dbo].[GetAllTradeInfo]
(
@TradeInfoID int
)
AS
BEGIN
IF (Not Exists(Select * From TradeRequest Where TradeInfoID = @TradeInfoID))
	BEGIN;
		Throw 51000, 'Invalid TradeInfoID', 1
	END

Select ti.ID, ti.IsComplete, ti.ReceiverConfirmed, ti.SenderConfirmed, ti.DateCreated, s.Username As SenderUsername, r.Username As ReceiverUsername
From TradeInfo ti
Join TradeRequest tr on ti.ID = tr.TradeInfoID
Join [User] s on tr.SenderID = s.ID
Join [User] r on tr.ReceiverID = r.ID
Where ti.ID = @TradeInfoID

END
GO

--Create Procedure [GetCardImage]
CREATE PROCEDURE [dbo].[GetCardImage]
	@CardID int
AS
IF (Not Exists (Select * From [Card] Where ID = @CardID))
	BEGIN;
		Throw 51000, 'Invalid CardID',1
	END
SELECT ImageURL FROM [Card] WHERE ID = @CardID
GO

--Create Procedure [GetCardInfo]
CREATE   PROCEDURE [dbo].[GetCardInfo]
(
@CardID int
)
AS
Begin
If(Not Exists (Select * From [Card] Where ID = @cardID))
	Begin;
		Throw 51000, 'Given CardID is invalid', 1
	End

Select *
From [Card]
Where ID = @cardID
End
GO

--Create Procedure [GetCardNameFromID]
CREATE PROCEDURE [dbo].[GetCardNameFromID]
(
@CardID int
)
AS
BEGIN
	SELECT [Name] FROM [Card] Where ID = @CardID
END
GO

--Create Procedure [GetCardSalesDetail]
CREATE   PROCEDURE [dbo].[GetCardSalesDetail]
(
@CardID int,
@Username varchar(100)
)
AS
BEGIN
declare @SellerID int = (SELECT [ID] FROM [User] WHERE Username = @Username)
IF (Not Exists (Select * From [Seller] Where ID = @SellerID))
	BEGIN;
		Throw 51000, 'Invalid Parameter: SellerID does not exist', 1
	END
IF (Not Exists (Select * From [Card] Where ID = @CardID))
	BEGIN;
		Throw 51001, 'Invalid Parameter: CardID does not exist', 1
	END

Select u.Username AS SellerUsername, s.ID AS SellerID, s.StoreName, s.[Address], s.City, s.[State], s.ZipCode, s.[Description] AS SellerDescription, s.Phone, 
	   c.ID AS CardID, c.Name AS CardName, c.Code AS CardCode, c.Rarity, lc.Price AS ListingPrice, c.MarketPrice
From ListingCard lc
Join Seller s on lc.SellerID = s.ID
Join [User] u on s.ID = u.ID
Join [Card] c on lc.CardID = c.ID
Where c.ID = @CardID AND s.ID = @SellerID
END
GO

--Create Procedure [GetCardsOffered]
CREATE   PROCEDURE [dbo].[GetCardsOffered]
(
@TradeID int,
@Username varchar(100)
)
AS
BEGIN
Declare @UserID int = (Select ID From [User] Where Username = @Username)
IF(((Select SenderID From TradeRequest Where TradeInfoID = @TradeID) != @UserID) AND
   (Select ReceiverID From TradeRequest Where TradeInfoID = @TradeID) != @UserID)
	Begin;
		Throw 51001, 'Invalid ProviderID', 1
	End

Select c.ID AS CardID, c.Name AS CardName, it.Quantity AS CardQuantity
From InTrade it
Join Card c on it.CardID = c.ID
Where it.TradeInfoID = @TradeID AND it.ProviderID = @UserID

END
GO

--Create Procedure [GetCollectionCardQuantity]
CREATE PROCEDURE [dbo].[GetCollectionCardQuantity]
(
@CollectionID int,
@CardID int
)
AS
SELECT Quantity FROM InCollection WHERE CollectionID = @CollectionID AND CardID = @CardID
GO

--Create Procedure [GetCollectionCards]
CREATE   PROCEDURE [dbo].[GetCollectionCards] (
	@CollectionID int)
AS
SELECT ID, [Name]
FROM [Card] c
JOIN InCollection i ON c.ID = i.CardID
WHERE CollectionID = @CollectionID
GO

--Create Procedure [GetCollectionName]
CREATE   PROCEDURE [dbo].[GetCollectionName] (
	@collectionID int)
AS
SELECT [Name] FROM [Collection] WHERE ID = @collectionID
GO
 
--Create Procedure [GetCredentials]
CREATE PROCEDURE [dbo].[GetCredentials]
	@Username varchar(100)
AS
BEGIN
	SELECT PasswordSalt, PasswordHash FROM [User] WHERE Username = @Username
END
GO
 
--Create Procedure [GetListingDetail]
CREATE PROCEDURE [dbo].[GetListingDetail]
(
@CardID int
)
AS
BEGIN
IF (Not Exists (Select * From [Card] Where ID = @CardID))
	BEGIN;
		Throw 51001, 'Invalid Parameter: CardID does not exist', 1
	END
 
Select u.Username AS SellerUsername, s.ID AS SellerID, s.StoreName, s.Phone, 
	   c.ID AS CardID, c.Name AS CardName, c.Code AS CardCode, c.Rarity, lc.Price AS ListingPrice, c.MarketPrice
From ListingCard lc
Join Seller s on lc.SellerID = s.ID
Join [User] u on s.ID = u.ID
Join [Card] c on lc.CardID = c.ID
Where c.ID = @CardID
END
GO
 
--Create Procedure [GetSellerListings]
CREATE PROCEDURE [dbo].[GetSellerListings] (
	@Username varchar(100))
AS
BEGIN
DECLARE @UserID int
SELECT @UserID = ID FROM [User] WHERE Username = @Username
SELECT CardID, Quantity, Price FROM [ListingCard] WHERE SellerID = @UserID
END
GO
 
--Create Procedure [GetTradeRequests]
CREATE   PROCEDURE [dbo].[GetTradeRequests] (
	@Username varchar(100))
AS
BEGIN
DECLARE @UserID int
SELECT @UserID = ID FROM [User] WHERE Username = @Username
SELECT u.Username AS SenderUsername, u2.Username AS ReceiverUsername, ti.ID, ti.DateCreated, ti.IsComplete
FROM TradeRequest tr
JOIN TradeInfo ti ON tr.TradeInfoID = ti.ID
JOIN [User] u ON tr.SenderID = u.ID
JOIN [User] u2 ON tr.ReceiverID = u2.ID
WHERE tr.SenderID = @UserID OR tr.ReceiverID = @UserID
END
GO
 
--Create Procedure [GetUserCardIDs]
CREATE PROCEDURE [dbo].[GetUserCardIDs]
(
@Username varchar(100)
)
AS
BEGIN
	DECLARE @UserID int
	SELECT @UserID = ID FROM [User] WHERE Username = @Username
 
	SELECT CardID FROM UserHas Where UserID = @UserID
END
GO
 
--Create Procedure [GetUserCardQuantity]
CREATE PROCEDURE [dbo].[GetUserCardQuantity]
(
@CardID int,
@Username varchar(100)
)
AS
DECLARE @UserID int
SELECT @UserID = ID FROM [User] WHERE Username = @Username
 
SELECT Quantity FROM UserHas WHERE CardID = @CardID AND UserID = @UserID
GO
 
--Create Procedure [GetUserCollectionIDs]
CREATE   PROCEDURE [dbo].[GetUserCollectionIDs] (
	@Username varchar(100))
AS
BEGIN
DECLARE @UserID int
SELECT @UserID = ID FROM [User] WHERE Username = @Username
SELECT ID FROM [Collection] WHERE UserID = @UserID
END
GO

--Create Procedure [IncrementCardOffered]
CREATE   PROCEDURE [dbo].[IncrementCardOffered]
(
@TradeID int,
@CardID int,
@Username varchar(100)
)
AS
BEGIN
Declare @UserID int = (Select ID From [User] Where Username = @Username)
 
IF (Not Exists(Select * From InTrade Where TradeInfoID = @TradeID))
	BEGIN;
		Throw 51000, '[IncrementCardOffered]: Invalid TradeInfoID', 1
	END
IF (Not Exists(Select * From InTrade Where ProviderID = @UserID))
	BEGIN;
		Throw 51001, '[IncrementCardOffered]: Invalid UserID', 1
	END
 
IF ((Select IsComplete From TradeInfo Where ID = @TradeID) = 1)
	Throw 51002, 'Cannot Add Card: Trade Already Completed, Please Refresh the Page', 1
IF ((Select SenderConfirmed From TradeInfo Where ID = @TradeID) = 1)
	Throw 51003, 'Cannot Add Card: Trade Already Confirmed by the Sender, Please Refresh the Page', 1
IF ((Select ReceiverConfirmed From TradeInfo Where ID = @TradeID) = 1)
	Throw 51004, 'Cannot Add Card: Trade Already Confirmed by the Receiver, Please Refresh the Page', 1
 
IF (Not Exists(Select * From InTrade Where CardID = @CardID)) 
	BEGIN;
		Throw 51005, '[IncrementCardOffered]: Invalid CardID', 1
	END
 
UPDATE InTrade 
SET Quantity = Quantity + 1
Where TradeInfoID = @TradeID AND ProviderID = @UserID AND CardID = @CardID
END
GO
 
--Create Procedure [IncrementOwnedCard]
CREATE PROCEDURE [dbo].[IncrementOwnedCard]
(
@CardID int,
@Username varchar(100)
)
AS
BEGIN
	DECLARE @UserID int
	SELECT @UserID = ID FROM [User] WHERE Username = @Username
 
	UPDATE UserHas
	SET Quantity = Quantity + 1
	WHERE CardID = @CardID AND UserID = @UserID
END
GO
 
--Create Procedure [IsSeller] 
CREATE   PROCEDURE [dbo].[IsSeller]
(	@Username varchar(100)
)
AS
BEGIN
	DECLARE @UserID int
	SELECT @UserID = ID FROM [User] WHERE Username = @Username
	SELECT CAST(CASE 
        WHEN EXISTS(SELECT 1 FROM Seller WHERE ID = @UserID) THEN 1 
        ELSE 0 
    END AS BIT) AS Result;
END
GO
 
--Create Procedure [ListCardValues] 
CREATE   PROCEDURE [dbo].[ListCardValues]
	@QueryAttribute varchar(100)
AS
BEGIN
	IF (@QueryAttribute = 'Name') SELECT DISTINCT [Name] FROM [Card] WHERE NOT [Name] IS NULL AND [Name] <> '' ORDER BY [Name] ASC;
	ELSE IF(@QueryAttribute = 'Code') SELECT DISTINCT [Code] FROM [Card] WHERE NOT [Code] IS NULL ORDER BY [Code] ASC;
	ELSE IF(@QueryAttribute = 'Rarity') SELECT DISTINCT [Rarity] FROM [Card] WHERE NOT [Rarity] IS NULL AND [Rarity] <> '' ORDER BY [Rarity] ASC;
	ELSE IF(@QueryAttribute = 'MarketPrice') SELECT DISTINCT [MarketPrice] FROM [Card] WHERE NOT [MarketPrice] IS NULL ORDER BY [MarketPrice] ASC;
	ELSE IF(@QueryAttribute = 'Type') SELECT DISTINCT [Type] FROM [Card] WHERE NOT [Type] IS NULL AND [Type] <> '' ORDER BY [Type] ASC;
	ELSE IF(@QueryAttribute = 'ATK') SELECT DISTINCT [ATK] FROM [Card] WHERE NOT [ATK] IS NULL ORDER BY [ATK] ASC;
	ELSE IF(@QueryAttribute = 'DEF') SELECT DISTINCT [DEF] FROM [Card] WHERE NOT [DEF] IS NULL ORDER BY [DEF] ASC;
	ELSE IF(@QueryAttribute = 'Level') SELECT DISTINCT [Level] FROM [Card] WHERE NOT [Level] IS NULL ORDER BY [Level] ASC;
	ELSE IF(@QueryAttribute = 'Race') SELECT DISTINCT [Race] FROM [Card] WHERE NOT [Race] IS NULL AND [RACE] <> '' ORDER BY [Race] ASC;
	ELSE IF(@QueryAttribute = 'Attribute') SELECT DISTINCT [Attribute] FROM [Card] WHERE NOT [Attribute] IS NULL AND [Attribute] <> '' ORDER BY [Attribute] ASC;
	ELSE throw 51000, 'invalid attribute name', 1;
END
GO
 
--Create Procedure [OfferCard]
CREATE   PROCEDURE [dbo].[OfferCard]
(
@TradeID int,
@CardID int,
@Username varchar(100)
)
AS
BEGIN
Declare @UserID int = (Select ID From [User] Where Username = @Username)
 
IF (Not Exists(Select * From TradeInfo Where ID = @TradeID))
	BEGIN;
		Throw 51000, '[OfferCard]: Invalid TradeInfoID', 1
	END
IF (Not Exists(Select * From TradeRequest tr
				Join TradeInfo ti on tr.TradeInfoID = ti.ID
				Where ti.ID = @TradeID AND (tr.ReceiverID = @UserID OR tr.SenderID = @UserID)))
	BEGIN;
		Throw 51001, '[OfferCard]: Invalid UserID', 1
	END
 
IF ((Select IsComplete From TradeInfo Where ID = @TradeID) = 1)
	Throw 51002, 'Cannot Add Card: Trade Already Completed, Please Refresh the Page', 1
IF ((Select SenderConfirmed From TradeInfo Where ID = @TradeID) = 1)
	Throw 51003, 'Cannot Add Card: Trade Already Confirmed by the Sender, Please Refresh the Page', 1
IF ((Select ReceiverConfirmed From TradeInfo Where ID = @TradeID) = 1)
	Throw 51004, 'Cannot Add Card: Trade Already Confirmed by the Receiver, Please Refresh the Page', 1
 
 
IF (Not Exists(Select * From [Card] Where ID = @CardID)) 
	BEGIN;
		Throw 51005, '[OfferCard]: Invalid CardID', 1
	END
 
IF (Exists (SELECT * FROM [InTrade] WHERE CardID = @CardID and ProviderID = @UserID and TradeInfoID = @TradeID))
	EXEC IncrementCardOffered @TradeID, @CardID, @Username
ELSE BEGIN
	INSERT InTrade (CardID, TradeInfoID, ProviderID, Quantity)
	VALUES (@CardID, @TradeID, @UserID, 1)
	END
END
GO
 
--Create Procedure [Register]
CREATE   PROCEDURE [dbo].[Register]
(
	@Username nvarchar(100),
	@PasswordSalt varchar(50),
	@PasswordHash varchar(50)
)
AS
BEGIN
	if @Username is null or @Username = ''
	BEGIN;
		throw 51001, 'Invalid Parameter: Username cannot be null or empty.', 1;
	END
	if @PasswordSalt is null or @PasswordSalt = ''
	BEGIN;
		throw 51002, 'Invalid Parameter: PasswordSalt cannot be null or empty.', 1;
	END
	if @PasswordHash is null or @PasswordHash = ''
	BEGIN;
		throw 51003, 'Invalid Parameter: PasswordHash cannot be null or empty.', 1;
	END
 
	IF (SELECT COUNT(*) FROM [User]
          WHERE Username = @Username) = 1
	BEGIN;
		throw 51004, 'Invalid Username: Username already exists.', 1;
	END
 
	INSERT INTO [User](Username, PasswordSalt, PasswordHash)
	VALUES (@Username, @PasswordSalt, @PasswordHash)	
 
END
GO
 
--Create Procedure [RegisterSeller]
CREATE PROCEDURE [dbo].[RegisterSeller]
(
	@Username varchar(100),
	@Phone nvarchar(24),
	@StoreName nvarchar(500),
	@Address nvarchar(300),
	@City nvarchar(50),
	@State char(2),
	@ZipCode nchar(5),
	@Description nvarchar(1000)
)
AS
BEGIN
	if @Username is null or @Username = ''
		throw 51001, 'Invalid Parameter: Username cannot be null or empty.', 1;
	if @Phone is null or @Phone = ''
		throw 51002, 'Invalid Parameter: Phone cannot be null or empty.', 1;
	if @StoreName is null or @StoreName = ''
		throw 51003, 'Invalid Parameter: StoreName cannot be null or empty.', 1;
	if @Address is null or @Address = ''
		throw 51004, 'Invalid Parameter: Address cannot be null or empty.', 1;
	if @City is null or @City = ''
		throw 51005, 'Invalid Parameter: City cannot be null or empty.', 1;
	if @State is null or @State = ''
		throw 51006, 'Invalid Parameter: State cannot be null or empty.', 1;
	if @ZipCode is null or @ZipCode = ''
		throw 51007, 'Invalid Parameter: ZipCode cannot be null or empty.', 1;
	if (SELECT COUNT(*) FROM [User] WHERE Username = @Username) < 1
		throw 51008, 'User does not exist.', 1;
 
	INSERT INTO Seller (ID, StoreName, [Address], [City], [State], [ZipCode], [Description], [Phone])
	VALUES ((SELECT ID FROM [User] WHERE Username = @Username), 
			@StoreName, @Address, @City, @State, @ZipCode, @Description, @Phone);
 
END
GO
 
--Create Procedure [RemoveCardFromListing]
CREATE   PROCEDURE [dbo].[RemoveCardFromListing]
(	@CardID int,
	@Username varchar(100)
)
AS
BEGIN
	IF(@CardID is Null)
	BEGIN;
		Throw 51000, 'Invalid Parameter: CardID cannot be null', 1
	END
	IF(@Username is Null)
	BEGIN;
		Throw 51000, 'Invalid Parameter: Username cannot be null', 1
	END
 
	DECLARE @UserID int
	SELECT @UserID = ID FROM [User] WHERE Username = @Username
	IF(Not Exists (Select *
				  From Seller
				  Where ID = @UserID))
	BEGIN;
		Throw 51001, 'Invalid Parameter: Seller does not exist', 1
	END
 
	IF (Not Exists (Select * From [ListingCard] Where SellerID = @UserID AND CardID = @CardID))
		BEGIN;
			THROW 51000, 'Invalid Parameter: Card does not exist in listing.', 1
		END
 
	DECLARE @ListingQuantity int
	SELECT @ListingQuantity = Quantity FROM ListingCard WHERE SellerID = @UserID AND CardID = @CardID
 
	IF (@ListingQuantity = 1)
		DELETE FROM [ListingCard]
		WHERE SellerID = @UserID AND CardID = @CardID
	ELSE
		UPDATE ListingCard
		SET Quantity = @ListingQuantity - 1
		WHERE SellerID = @UserID AND CardID = @CardID
END
GO
 
--Create Procedure [RemoveCardOffered]
CREATE   PROCEDURE [dbo].[RemoveCardOffered]
(
@TradeID int,
@CardID int,
@Username varchar(100)
)
AS
BEGIN
Declare @UserID int = (Select ID From [User] Where Username = @Username)
 
IF (Not Exists(Select * From InTrade Where TradeInfoID = @TradeID))
	BEGIN;
		Throw 51000, '[RemoveCardOffered]: Invalid TradeInfoID', 1
	END
IF (Not Exists(Select * From InTrade Where ProviderID = @UserID))
	BEGIN;
		Throw 51001, '[RemoveCardOffered]: Invalid UserID', 1
	END
 
IF ((Select IsComplete From TradeInfo Where ID = @TradeID) = 1)
	Throw 51002, 'Cannot Remove Card: Trade Already Completed, Please Refresh the Page', 1
IF ((Select SenderConfirmed From TradeInfo Where ID = @TradeID) = 1)
	Throw 51003, 'Cannot Remove Card: Trade Already Confirmed by the Sender, Please Refresh the Page', 1
IF ((Select ReceiverConfirmed From TradeInfo Where ID = @TradeID) = 1)
	Throw 51004, 'Cannot Remove Card: Trade Already Confirmed by the Receiver, Please Refresh the Page', 1
 
IF (Not Exists(Select * From InTrade Where CardID = @CardID)) 
	BEGIN;
		Throw 51005, '[RemoveCardOffered]: Invalid CardID', 1
	END
 
DELETE InTrade
Where TradeInfoID = @TradeID AND ProviderID = @UserID AND CardID = @CardID
END
GO
 
--Create Procedure [RetrieveCard]
CREATE   PROCEDURE [dbo].[RetrieveCard]
	(@Name nvarchar(70) = NULL,
	@Code varchar(20) = NULL,
	@Rarity varchar(40) = NULL,
	@MarketPrice money = NULL,
	@Type varchar(20) = NULL,
	@ATK int = NULL,
	@DEF int = NULL,
	@Level int = NULL,
	@Race varchar(30) = NULL,
	@Attribute varchar(10) = NULL,
	@SetID int = NULL)
AS
	SELECT *
	FROM [Card]
	WHERE (@Name is null OR [Name] LIKE '%' + @Name + '%') AND
	(@Code is null OR Code = @Code) AND
	(@Rarity is null OR Rarity = @Rarity) AND
	(@MarketPrice is null OR MarketPrice = @MarketPrice) AND
	(@Type is null OR [Type] = @Type) AND
	(@ATK is null OR ATK = @ATK) AND
	(@DEF is null OR DEF = @DEF) AND
	(@Level is null OR [Level] = @Level) AND
	(@Race is null OR Race = @Race) AND
	(@Attribute is null OR Attribute = @Attribute) AND
	(@SetID is null OR SetID = @SetID)
GO
 
--Create Procedure [UpdateCollectionName] 
CREATE   PROCEDURE [dbo].[UpdateCollectionName](
	@NewName nvarchar(200),
	@CollectionID int
)
AS
BEGIN
	IF (@NewName IS NULL)
	BEGIN;
		THROW 51000, 'Invalid Parameter: Name cannot be null', 1
	END
 
	IF (@CollectionID IS NULL)
	BEGIN;
		THROW 51000, 'Invalid Parameter: Collection ID cannot be null', 1
	END
 
	IF (NOT EXISTS(SELECT * FROM [Collection] WHERE ID = @CollectionID))
	BEGIN;
		THROW 51000, 'Invalid Parameter: The Collection does not exist', 1
	END
 
	UPDATE [Collection]
	SET [Name] = @NewName
	WHERE (ID = @CollectionID)
 
END
GO
 
--Create Procedure [UpdateListingPrice]
CREATE PROCEDURE [dbo].[UpdateListingPrice](
	@CardID int,
	@Username varchar(100),
	@Price money
)
AS
BEGIN
	IF (@CardID IS NULL)
	BEGIN;
		THROW 51000, 'Invalid Parameter: CardID cannot be null', 1
	END
 
	IF (@Username IS NULL)
	BEGIN;
		THROW 51000, 'Invalid Parameter: Username cannot be null', 1
	END
 
	IF (@Price IS NULL)
	BEGIN;
		THROW 51000, 'Invalid Parameter: Price cannot be null', 1
	END
 
	DECLARE @UserID int
	SELECT @UserID = ID FROM [User] WHERE Username = @Username
 
	IF (NOT EXISTS(SELECT * FROM [ListingCard] WHERE SellerID = @UserID AND CardID = @CardID))
	BEGIN;
		THROW 51000, 'Invalid Parameter: The Listing does not exist', 1
	END
 
	UPDATE [ListingCard]
	SET [Price] = @Price
	WHERE (SellerID = @UserID AND CardID = @CardID)
 
END
GO

--CREATE TRIGGER [TradingCardQuantity]
CREATE   TRIGGER [dbo].[TradingCardQuantity]
ON [dbo].[InTrade] AFTER INSERT, UPDATE
AS
BEGIN
Declare @UserID int, @CardID int, @OwnedCardNum int, @ListedCardNum int, @OfferedCardNum int, @AvailableCardNum int
 
Select @UserID = ProviderID, @CardID = CardID
From inserted
 
Select @OwnedCardNum = Quantity
From UserHas
Where UserID = @UserID AND CardID = @CardID
 
Select @ListedCardNum = Quantity
From ListingCard
Where SellerID = @UserID AND CardID = @CardID
 
Select @OfferedCardNum = Quantity
From InTrade
Where ProviderID = @UserID AND CardID = @CardID
 
IF (@OwnedCardNum Is Null) SET @OwnedCardNum = 0
IF (@ListedCardNum IS Null) SET @ListedCardNum = 0
 
SET @AvailableCardNum = @OwnedCardNum - @ListedCardNum - @OfferedCardNum
 
IF (@AvailableCardNum<0)
	BEGIN;
		Throw 51000, 'Insufficient Quantity of Cards Available: Try to remove card from My Listing or other Trades', 1
	END
 
END
GO
 
ALTER TABLE [dbo].[InTrade] ENABLE TRIGGER [TradingCardQuantity]
GO
 
--CREATE TRIGGER [UpdateMarketPrice] 
CREATE   TRIGGER [dbo].[UpdateMarketPrice] 
   ON  [dbo].[ListingCard]
   AFTER UPDATE, INSERT, DELETE
AS
BEGIN
declare @CardID int;
IF EXISTS (SELECT * FROM inserted) SET @CardID = (SELECT CardID FROM inserted)
ELSE SET @CardID = (SELECT CardID FROM deleted)
UPDATE [Card]
SET MarketPrice = (
	SELECT AVG(Price)
	FROM ListingCard
	WHERE CardID = @CardID)
WHERE ID = @CardID
 
END
GO
 
ALTER TABLE [dbo].[ListingCard] ENABLE TRIGGER [UpdateMarketPrice]
GO

CREATE USER YuGi FROM LOGIN YuGi;
exec sp_addrolemember db_datareader, YuGi; 
GO
 
GRANT EXECUTE, SELECT TO YuGi;
GO

CREATE USER [kimmelog] FROM LOGIN [kimmelog];
exec sp_addrolemember 'db_owner', 'kimmelog';
GO

CREATE USER [bih2] FROM LOGIN [bih2];
exec sp_addrolemember 'db_owner', 'bih2';
GO

/*
CREATE USER [kimy8] FROM LOGIN [kimy8];
exec sp_addrolemember 'db_owner', 'kimy8';
GO
*/