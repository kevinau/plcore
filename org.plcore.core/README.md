# Core code

A collection of classes that support all other plcore code.{::comment}eos{:/}  It includes:

* A math library that provides a Decimal class.  The Decimal class provides all decimal 
  (base 10) arithmetic without the overhead of Java's BigDecimal.  Decimal also includes
  accounting methods that are not present in BigDecimal.
* A nio library that provides some additional input and output streams.
* A time library that provides a date factory.  The date factory converts an input string 
  to a date and is very tolerant of the format of the input string.  
* A utility library that provides a miscellaneous collection of useful classes.  It 
  includes MD5 and CRC32 digest classes.
* A value library that provide classes that represent different values.  These value 
  classes have specific application in accounting applications.
