
# Banking API Mock

The system have 4 types of accounts: StudentChecking, Checking, Savings, and CreditCard.

The system have 3 types of Users: Admins, AccountHolders and ThirdParty.

Admins can create new accounts. When creating a new account they can create Checking, Savings or CreditCard Accounts.

[![Diagrama-sin-t-tulo-drawio.png](https://i.postimg.cc/ZqQs5F3c/Diagrama-sin-t-tulo-drawio.png)](https://postimg.cc/bGQ96nmD)
## Import all the API routes to Postman

[Postman Import file](https://github.com/dyrmig/bank-api-mock/blob/main/Banking.postman_collection.json)

## API Reference
#### Admin only routes
Complete list of all users (admins and account holders):
```http
  GET /users
```
Create a new Admin user (requires an Admin object in the body):
```http
  POST /admins
```
Create a new AccountHolder user (requires an AccountHolder object in the body):
```http
  POST /accountholders
```
Create a new Checking account for an AccountHolder:
```http
  POST /accountholders/{accountHolderId}/checking
```
Create a new Savings account for an AccountHolder:
```http
  POST /accountholders/{accountHolderId}/savings
```
Create a new CreditCard account for an AccountHolder:
```http
  POST /accountholders/{accountHolderId}/creditcard
```
Subtract amount from the account with accountId (requires an amountOfOperationDTO object in the body):
```http
  PATCH /accounts/{accountId}/subtract
```
Add amount to the account with accountId (requires an amountOfOperationDTO object in the body):
```http
  PATCH /accounts/{accountId}/add
```
Add new ThirdParty (requires an ThirdParty object in the body):
```http
  POST /thirdparty
```
Get all the ThirdParty elements:
```http
  GET /thirdparty
```
Delete a ThirdParty:
```http
  DELETE /thirdparty/{thirdPartyId}
```
#### AccountHolder only routes
Transfer amount from the account with accountId to any other account (requires a TransferForm(contains amount and receiver account) object in the body):
```http
  POST /accountholders/{accountHolderId}/accounts/{accountId}/transfer
```
#### Admin and AccountHolder routes
Get all accounts belonging to AccountHolder:
```http
  GET /accountholders/{accountHolderId}/accounts
```
Get specific account:
```http
  GET /accounts/{accountId}
```
Get specific AccountHolder:
```http
  GET /accountholders/{accountHolderId}
```
#### Public routes
ThirdParty charges amount from account (requires an ThirdPartyOperationDTO with amount and target account SecretKey in the body and valid hashed-key of a ThirdParty in header):
```http
  POST /thirdparty/{accountId}/charge
```
ThirdParty adds amount to account (requires an ThirdPartyOperationDTO with amount and target account SecretKey in the body and valid hashed-key of a ThirdParty in header):
```http
  POST /thirdparty/{accountId}/refund
```
## Tech Stack

**Server:** Java Spring Boot

**Data Base:** MySQL

**Auth:** Spring Boot Security + JSON Web Token
