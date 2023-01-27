# SpringSecurity3.0


Login
http://localhost:9696/api/v1/auth/login  POST

Req :   {
    "username":"v@gmail.com",
    "password":"qaaaa"
}

Res : {
    "tokenExpTime": "Sat Jan 28 11:30:29 IST 2023",
    "message": "Success",
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ2QGdtYWlsLmNvbSIsImV4cCI6MTY3NDg4NTYyOSwiaWF0IjoxNjc0Nzk5MjI5fQ.ZBO9LzcU5vQSslQrctAms6tYIwzwaYGz4NTtcCCILDEU2xIn0La0MZ7j7Iq7MoywdCFeXxUwWDDqfHXJv_oSBA"
}


Create User :

http://localhost:9696/api/v1/user  POST

Req {
   
    "fname": "vikas",
    "lname": "Kumar11",
    "email": "v@gmail.com",
    "about": "hggasgh",
    "regDate": "2020-03-02",
    "profilePic": "a.png",
    "password": "qaaaa"
}


Res : 
[
    {
        "id": "63d106f17b019f5f5c26f616",
        "fname": "vikas",
        "lname": "Kumar11",
        "email": "v@gmail.com",
        "about": "hggasgh",
        "regDate": "2020-03-02",
        "profilePic": "a.png",
        "password": "$2a$10$/9S/v81dYrcprtCyCuEy/OIrcj/vOfMJY2Y71UIjWQSZX.TH5YH7O",
        "categories": [
            {
                "catName": "Fun",
                "catDesc": "Spent money on Fun thing",
                "catIcon": ""
            },
            {
                "catName": "Movie",
                "catDesc": "Spent money on Watching Movie",
                "catIcon": ""
            },
            {
                "catName": "Learning",
                "catDesc": "Spent money on Learning new",
                "catIcon": ""
            }
        ],
      
   
        "authorities": [
            {
                "authority": "NORMAL"
            }
        ]
       
    }
]
