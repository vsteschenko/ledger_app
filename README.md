# Finances Tracker Backend

This is a backend application for tracking finances. With it, you can add, update, and delete expenses.

## Launching Locally

To set up the project on your local machine, follow these steps:

1. **Install IntelliJ IDEA**.

2. **Clone this repository**.

3. **Install PostgreSQL**.

   In your terminal, run the following commands:
   ```bash
   createuser -d -P "name_of_the_user_without_quotes"
   createdb "name_of_the_db_without_quotes" -O "username_you_created_before"
   
4. **Open the project in IntelliJ IDEA**.

5. In the configuration settings:
   - Add a new configuration.
   - Give it any name you want.
   - Set **Run on**: `Local Machine`.
   - Set **Main class**: `ApplicationKt`.
   - The **Working directory** should be set automatically. If it isn't, choose `ledger_app` from the list.

6. **Set up environment variables**:
   - `DATABASE_URL`: 
     ```
     jdbc:postgresql://localhost:5432/db_name?user=username&password=password
     ```
   - `HASH_SECRET_KEY`: (randomly generated value)
   - `JDBC_DRIVER`: 
     ```
     org.postgresql.Driver
     ```
   - `JWT_SECRET`: (randomly generated value)

7. **Use the classpath of the module**: 
   - Choose `ktor-sample.main`.

8. **CRUD**
   Launch the app, open postman and make a get request on http://localhost:8080/

9. **SIGN UP**
   Make a POST request to http://localhost:8080/v1/users/register, in body add:
   {
      "name": "name",
      "password": "password",
      "email": "email"
   }
10. **SIGN IN**
    Make a POST request to http://localhost:8080/v1/users/register, in body add:
   {
      "password": "password",
      "email": "email"
   }
11. **Add expense**
    Make a POST request to http://localhost:8080/v1/txs/create
    In Authorization choose Bearer Token and insert the message you received after sign up or sign in.
    {
      "comment": "description",
      "category": "category",
      "amount": integer (positive or negative depending on whether its an income or expense)
      "location": "location"
    }
13. **All Transactions**
    Make a GET request to http://localhost:8080/v1/txs
    In Authorization choose Bearer Token and insert the message you received after sign up or sign in.




