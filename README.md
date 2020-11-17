# Magnolia-protobuf
This repository contains an experimental implementation of scala case class to protobuf DynamicMessage (of course DynamicSchema)

### Example
```
import PSchema._
case class Person(name: String, age: Int)

val result: Type[Person]         = PSchema[Person]
val schema: DynamicSchema        = result.schema
val covertToDMsg: DynamicMessage = result.to(Person("Franco", 28))

val from: Person = result.from(covertToDMsg)
```
