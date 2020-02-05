# ReservationModule

1.**Load Static Data** : It loads static details of Inventory count upto 90 days.
  >>**HTTP Method** - GET
  >>**URL** -  [localhost:8080/reservation/loadData]
 
2.**Get Invetory Details**: Method Gets Inventory details Of Specific date Range.
  >>**HTTP Method** - GET
  >>**Example URL** - [localhost:8080/reservation/getAvailability?startDate=2020-02-15&endDate=2020-02-20]
  >>>**Response**:
  ```
  [
    {
        "month": "02_2020",
        "schedules": [
            {
                "date": 15,
                "priceList": [
                    {
                        "label": "Adult",
                        "price": "1000"
                    },
                    {
                        "label": "Child",
                        "price": "500"
                    }
                ],
                "totalInventory": 30,
                "availability": 30
            }
        ]
    }
]
```

3.**Save Booking**: Method Saves Booking details, which Accepts Customer details,Number Of Pax,Start Date And End Date as Parameters.
  >>**HTTP Method** - POST
  >>**Example URL** - [localhost:8080/reservation/savereservation]
  >>>**Request**:
  ```
  {
	"startDate":"2020-02-15",
	"endDate":"2020-02-18",
	"numberOfDays":"2",
	"paxDetails":[{
		"numberOfPax":"2",
		"label":"Adult",
		"price":"1000"
	}],
	"customerDetails":[{
		"firstName":"Joe",
		"lastName":"John",
		"mobileNumber":"07123456789",
		"emailId":"test@gmail.com",
		"age":"20"
	}]
}
```
>**Response**:
```
{
    "message": "Booking is successfull ,Booking Id - NSGBLi",
    "status": "Success"
}
```

4.**Modify Booking**: Customer's bookings can be modified.
  >>**HTTP Method** - POST
  >>**Example URL** - [localhost:8080/reservation/modifyreservation]
  >>>**Request**:
  ```
{
	"startDate":"2020-02-18",
	"endDate":"2020-02-20",
	"numberOfDays":"2",
	"bookingId":"NSGBLi",
	"paxDetails":[{
		"numberOfPax":"2",
		"label":"Adult",
		"price":"1000"
	}],
	"customerDetails":[{
		"firstName":"Joe",
		"lastName":"John",
		"mobileNumber":"07123456789",
		"emailId":"test@gmail.com",
		"age":"20"
	}]
}
```
>**Response**:
```
{
    "message": "Modified booking successfully for booking id  - NSGBLi",
    "status": "Success"
}
```

5.**Cancel Booking**: Customer can cancel booking by passing booking id.
  >>**HTTP Method** - POST
  >>**Example URL** -[localhost:8080/reservation/cancelBooking] 
  >>>**Request**:
  ```
{
	"bookingId":"NSGBLi"
}
```
>**Response**:
```
{
    "message": "Booking has been cancelled for booking id NSGBLi",
    "status": "Success"
}
```



