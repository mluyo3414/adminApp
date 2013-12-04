ADMIN APPLICATION:
========

James Dagres,
Miguel Suarez,
Matt Luckam, 
Carl Barbee

DESCRIPTION:
=============

Admin app for the FoodNow project. Handles the orders that are sent from 
the server. Each order contains a name, order, phone number, confirmation, and total. 
The client is sent an SMS message once their order is ready from Twilio.

PROGRAM FLOW:
=============

Admin must add a name and take a picture in order to login to the server. The 
picture is analyzed using Android Face Detection to determine it is a valid person.
The server sends the current orders for the admin to review. Each order contains
a name, order, phone number, confirmation, and total. When the order is complete,
the admin can select the order in the list view and delete it. The client is then
sent an SMS message from Twilio that their order is ready and the server is updated
to remove the order.

SERVER PROJECT:
=============

For more information see the repo of the server:
https://github.com/mluyo3414/foodNowServer

CLIENT APPLICATION:
=============

For more information see the repo of the Client App:
https://github.com/mluyo3414/clientApp
