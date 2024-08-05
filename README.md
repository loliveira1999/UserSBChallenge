The idea of this exercise is to implement a simple API (backend) as a way to test (and improve)
your knowledge in web development. You need to provide functionality based on REST or
GraphQL API. Ensure that the API can be tested and checked using Postman or something
similar

Requirements:

 - Users:
    - Each user should have at least the following properties:
        - Name
        - Email
        - Password
    - Create a CRUD that has the following considerations:
        - You can’t create a user with the same email address
        - Password should always have a minimum of 8 characters
        - Allow getting a single user or all users
        - Updating should not replace the entire user but only the fields that
changed
        - Deleting a user should only “soft delete” it, no need to provide a way to
activate it again in this example
 - Authentication:
    - Please use a token authentication method. You don’t have to implement a login
process, just generate a predefined token for a user that can easily be used in
other calls
        - A token is required for anything other than creating a user
 - Bonus:
    - Implement end-to-end tests for different methods (2 is enough but you can do
more)
    - Save data into a database of your choice
    - Provide a Postman file (or anything similar) to test the API

The estimated time to finish this is 3 hours
We will also evaluate how you organize your backend so keep that in mind as you are
structuring the code