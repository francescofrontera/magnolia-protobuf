# Magnolia-protobuf
This repository contains no producion ready implementation of scala case class to protobuf DynamicMessage (of course DynamicSchema)

### Example
```
import PSchema._
case class Person(name: String, age: Int)

val result: Type[Person]         = PSchema[Person]
val schema: DynamicSchema        = result.schema
val covertToDMsg: DynamicMessage = result.to(Person("Franco", 28))
```
