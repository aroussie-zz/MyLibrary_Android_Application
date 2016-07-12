# My Book Library
This is an Android application which allows the user to manage his personal library. 
The goal is to be able to look for books information thanks to the Google Book API and then be able to 
add this book to our personal library on this app. Then the user can update an added books to put his personal rate
and his own point a view about it for example. I'm using Google Sign in API to connect to the app, Google Book API to
find information about a book entered by the user, Retrofit to deal with the JSON file sent by the API and I use 
a SQLite database. 

Use example of the app: 

  - Click on the magnifying glass on the top of the app.
  - Write the name of a book like "Hunger games for example"
  - Click on one of the displayed book
  - Click on the "add to library" button. The book should now appear in your library, both into "All" and "To Read" categories.
  - Now click again on the Book. You should see again the information about the book and there are also two icons next to the cover. If you click on the book, that means that you have read the book. If you click on the heart, the book will be added to your favorites.
  - You can also update the book. To do so, from you library view (home page), hold your finger on a book. A small window appears with "Update" and "Delete". Delete will remove the book from your library. Update makes you able to add your own rate to the book and also to write some comments about it (like your personal feelings)
  
![Search Hunger Games](https://cloud.githubusercontent.com/assets/16949791/16747021/a90f4faa-4782-11e6-9802-cdc8951d605e.png)
![Add a Book](https://cloud.githubusercontent.com/assets/16949791/16747022/ab0e4e50-4782-11e6-9c70-66b816377c02.png)
![Home Page](https://cloud.githubusercontent.com/assets/16949791/16747024/acca4bd6-4782-11e6-9043-99c1b9dba771.png)


![Book Information](https://cloud.githubusercontent.com/assets/16949791/16747026/adc2e39a-4782-11e6-82b9-454e4d76240f.png)
![Update / Delete](https://cloud.githubusercontent.com/assets/16949791/16747027/aeb2b910-4782-11e6-84df-b01e2a093ebd.png)
![Update Book](https://cloud.githubusercontent.com/assets/16949791/16747292/047635d8-4784-11e6-8aec-0bccb96290c2.png)
  

I design the application in a way to make it as user-friendly as possible. This is why there is not a lot of buttons or icons everywhere.
There are 2 magnifying glasses, one on the top of the app to look for a new book using Google Book API and the other one, a little 
smaller to filter the books already added to the app. There are not to many information displayed about a book, only the ones I 
find useful when I'm looking for a new book to read. 

Libraries used: 

  - SDK
  - Retrofit
  - Picasso
  - Google services for Google Sign In



