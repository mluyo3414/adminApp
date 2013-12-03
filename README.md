adminApp
========

Admin app for the FoodNow project.

Admin must add a name and take a picture in order to login to the server. The 
picture is analyzed using Face Detection to determine it is a valid person.
The server sends the current orders for the admin to review. Each order contains
a name, order, phone number, confirmation, and total. When the order is complete,
the admin can select the order in the list view and delete it. The client is then
sent an SMS message that their order is ready and the server is updated to remove 
the order.