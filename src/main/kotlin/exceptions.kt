class NotEnoughArguments : Exception("Not enough arguments")

class WrongCommand : Exception("Wrong command")

class DatabaseNotFound(database: String) : Exception("Database $database doesn't exist")

class DatabaseAlreadyExists(database: String) : Exception("Database $database already exists")