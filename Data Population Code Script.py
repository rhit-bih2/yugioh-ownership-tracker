import requests
import json
import pyodbc

#This python program gets API call from YuGiOh Offcial Database 
#Parses each information of card into attributes by using Json Library
#Connects to SQL Server to populate all data to Card and Set Table 
#Reference Videos and Websites
#https://ygoprodeck.com/api-guide/
#https://discuss.python.org/t/obtain-api-data-token-using-requests/54430
#https://www.youtube.com/watch?v=JVQNywo4AbU
#https://www.codecademy.com/resources/docs/python/sql-connectors/pyodbc
#https://stackoverflow.com/questions/33254191/how-to-find-the-odbc-driver-name-for-a-connection-string
#https://stackoverflow.com/questions/43480466/how-to-parse-json-data-from-api-response-in-python
#https://mariadb.com/docs/connectors/mariadb-connector-python/api/cursor


#Official YuGiOh Card API Endpoint 
Card_API_endpoint_URL = "https://db.ygoprodeck.com/api/v7/cardinfo.php"
Set_API_endpoint_URL =  "https://db.ygoprodeck.com/api/v7/cardsets.php"

#Function for DB Connection
def DbServiceConnection():
    try:
        conn = pyodbc.connect(
            "DRIVER={ODBC Driver 17 for SQL Server};"
            "SERVER=golem.csse.rose-hulman.edu;"
            "DATABASE=YuGiOhTrackerDemo_S2G4;"
            "UID=kimy8;"
            "PWD=!!eden123;"
        )
        print("Connected to Database")
        return conn
    
    except Exception as e:
        print("Connection failed:", e)
        return None

#Main 
def main():

    #Get response to the API Call
    cardResponse = requests.get(Card_API_endpoint_URL)
    cardResponse.raise_for_status()
    cardData = cardResponse.json()
    

    setResponse = requests.get(Set_API_endpoint_URL)
    setResponse.raise_for_status()
    setData = setResponse.json()

    print("API Success")
    
    try:
        DBCon = DbServiceConnection()
        if DBCon is not None:
            stmt = DBCon.cursor()
            #Parsing data from full API data

            #Nested For Loop to make each set
            for set in setData[:1000]:
                setName = set.get("set_name")
                setCode = set.get("set_code")
                # Check given set exists or not by setName since there are sets have same setCode with different name
                stmt.execute("Select * From [Set] Where NAME = ? AND SetCode = ?", setName, setCode)
                result = stmt.fetchone()          #get the first row of data or return None
                if result is None:
                    # Insert new set
                    InsertNewSet = "Insert Into [Set] (NAME, SetCode) Values(?, ?)"
                    stmt.execute(InsertNewSet, setName, setCode)
                #print(f"SetCode: {setCode}, SetName: {setName} \n")

            print("Populating YuGiOh Card & Set Data")
            #Nested For Loop to make each card
            for card in cardData["data"][:700]:
                cardName = card.get("name")
                cardCode = None
                cardRarity = None
                cardDescription = card.get("desc")
                cardPrice = None
                cardType = card.get("type")
                cardATK = card.get("atk")
                cardDEF = card.get("def")
                cardLevel = card.get("level")
                cardRace = card.get("race")
                cardAttribute = card.get("attribute")
                cardImageURL = card["card_images"][0].get("image_url")
                if not card.get("card_sets"):
                    continue

                #Nested loop again for card sets
                for cardSet in card["card_sets"]:
                    cardCode = cardSet.get("set_code")
                    cardRarity = cardSet.get("set_rarity")
                    cardSetName = cardSet.get("set_name")

                    # We need to get SetID for Insert Card SQL statement
                    stmt.execute("Select ID From [Set] Where NAME = (?)", cardSetName)
                    row = stmt.fetchone()

                    if row is None:
                        #If the set is not in previous card sets, we will make new set for it
                        InsertNewSet = "Insert Into [Set] (NAME, SetCode) Values(?, ?)"
                        cardSetCode = cardCode.split('-')[0]
                        stmt.execute(InsertNewSet, cardSetName, cardSetCode)

                        #Get the setID just we made it
                        stmt.execute("Select ID From [Set] Where NAME = (?)", cardSetName)
                        SetID = stmt.fetchone()[0]
                    else: 
                        SetID = row[0]
                    
                    # Insert Card data into the DB if it does not exist
                    stmt.execute("Select Name, Code From Card Where Name = (?) And Code = (?) And Rarity = (?)", cardName, cardCode, cardRarity)
                    CardExist = stmt.fetchone()
                    if CardExist is None:
                        InsertNewCard = "Insert Into [Card] (Name, Code, Rarity, Description, MarketPrice, Type, ATK, DEF, Level, Race, Attribute, ImageURL, SetID) Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        stmt.execute(InsertNewCard, cardName, cardCode, cardRarity, cardDescription, cardPrice, cardType, cardATK, cardDEF, cardLevel, cardRace, cardAttribute, cardImageURL, SetID)
                    #print(f"Card\nName: {cardName}, Code: {cardCode}, Rarity: {cardRarity}, Description: {cardDescription}, Price: {cardPrice}, Type: {cardType}, ATK: {cardATK}, DEF: {cardDEF}, Level: {cardLevel}, Race: {cardRace}, Attribute: {cardAttribute}, ImageURL: {cardImageURL} \n")

            #Commit all changes or transactions on DB
            DBCon.commit()
            print("Data successfully populated!")
            
            #Close Connection
            stmt.close()
            DBCon.close()
        else:
            print("DB Connection is None")
            return None
        
    except Exception as e:
        print("Error Occured:", e)
        return None

main()







