# Error message style

Error messages for data entry types following the style described here.

## Required message

When a data entry field is 'required', the message displayed is simply "Required". 
It is assumed that "what is required?" is shown elsewhere on the page.

The "Required" message is similar to the * often shown on required fields.  The difference 
between a "Required" message and * is:

* * is always shown.
* "Required" is only shown when the field is empty.

## Incomplete messages

When the first characters of a field are entered, they may not make a valid value. 
When more characters are entered, the field is completed, and a valid value is formed.

For example, assume negative numbers are allowed.  If a minus sign is entered, the 
field is in error.  A solitary "-" is not a valid number.  If the next character 
is entered, the field becomes valid.

If a field is in error, but the entry of more characters would eliminate the error, 
the field is said to be "incomplete".  Incomplete fields show an error message, but 
the message is shown in a lighter colour to avoid distracting the flow of data entry.

Dates are another example of where the field is in "incomplete" until all the characters 
of the date have been entered.

Incomplete messages should explain what is required, not what is wrong.  For example, 
for a date field, after a one or two digit day number has been entered:

Right: "month and year required" Wrong: "missing month and year"

Incomplete messages are shown because of the overriding philosophy of applying character 
by character validation.

If a data entry field looses focus, a incomplete message will shown as an error message.

## Error message

Error messages explain what is wrong with the data entry. 

Error messages should be written assuming a competent user will know what is wrong 
when they see the message.  They should describe only what is wrong, not what is required. 


The error messages associated with "types" are fairly low level error messages--competent 
users will generally not generate them.  For example, if a quantity is required, 
a competent user will know that only digits should be entered--but checking for digits 
must be done and an error message shown if non-digits are found.

Error messages should be written assuming a competent user.  They should briefly 
describe what is wrong, and if appropriate, describe what is expected.

For example:
* If a field requires a minimum of 4 characters, and only 3 have been entered: : 
  "4 characters required" : "


Error messages implicitly refer to the data entry.  The name of the data entry field 
should not be included:

Right   "more than 4 digits" Wrong   "the postcode contains more than 4 digits" Worse   
"an Australian postcode cannot contain more than 4 digits"



, in that a competent user will generally not are written as if completing the sentence 
"The characters you have entered ...".

For example, if an string field has a limit of 5 characters, and 6 characters have 
been entered, the error message is "have more than 5 characters".

As far as possible, the error message should describe what is wrong, rather than 
saying what is expected.  For example, if a date is expected, and "32/12/2018" is 
entered, the error message is "32 is not a valid day for month 12".


